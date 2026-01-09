package org.example.springstarterproject.controller;

import com.example.api.AuthApi;
import com.example.models.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springstarterproject.service.AuthenticationService;
import org.example.springstarterproject.service.TokenBlacklistService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@RestController
@RequestMapping("/auth")
public class AuthController implements AuthApi {

    private final AuthenticationService authenticationService;
    private final TokenBlacklistService tokenBlacklistService;
    private final NativeWebRequest request;

    public AuthController(AuthenticationService authenticationService, NativeWebRequest request, TokenBlacklistService tokenBlacklistService) {
        this.authenticationService = authenticationService;
        this.request = request;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {

        AuthResponse authResponse = authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword());

//        response.addHeader(HttpHeaders.SET_COOKIE, authenticationService.createRefreshTokenCookie(authResponse.getRefreshToken()).toString());

        //Workaround so I don't have to mess with the openAPI.yaml to get a HttpServletResponse into the methode signature
        HttpServletResponse response = request.getNativeResponse(HttpServletResponse.class);

        if (response != null) {
            ResponseCookie cookie = authenticationService.createRefreshTokenCookie(authResponse.getRefreshToken());
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Override
    public ResponseEntity<Void> logoutUser() {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();


        if (response != null) {
            response.addHeader(HttpHeaders.SET_COOKIE, authenticationService.deleteCookie(request).toString());
        }

        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping("/refresh")
    @Override
    public ResponseEntity<AuthResponse> refreshToken(@CookieValue(name = "refresh_token") String refreshToken) {

        if(tokenBlacklistService.checkToken(refreshToken)) {
            throw new BadCredentialsException("Token has been revoked");
        }

        return new ResponseEntity<>(authenticationService.refreshToken(refreshToken), HttpStatus.OK);
    }


    @PostMapping("/register")
    @Override
    public ResponseEntity<AuthResponse> registerUser(RegisterRequest registerRequest) {
        return new ResponseEntity<>(authenticationService.register(
                registerRequest.getEmail(), registerRequest.getUsername(),
                registerRequest.getPassword()), HttpStatus.CREATED);
    }
}
