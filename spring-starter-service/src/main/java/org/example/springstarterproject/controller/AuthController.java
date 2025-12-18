package org.example.springstarterproject.controller;

import com.example.api.AuthApi;
import com.example.models.AuthResponse;
import com.example.models.LoginRequest;
import com.example.models.OAuthLoginRequest;
import com.example.models.RegisterRequest;
import org.example.springstarterproject.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController implements AuthApi {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    @Override
    public ResponseEntity<AuthResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authenticationService.login(loginRequest.getEmail(), loginRequest.getPassword()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AuthResponse> loginViaOAuth(OAuthLoginRequest oauthLoginRequest) {
        return null;
    }

    @PostMapping("/register")
    @Override
    public ResponseEntity<AuthResponse> registerUser(RegisterRequest registerRequest) {
        return new ResponseEntity<>(authenticationService.register(
                registerRequest.getEmail(), registerRequest.getUsername(),
                registerRequest.getPassword()), HttpStatus.CREATED);
    }
}
