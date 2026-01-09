package org.example.springstarterproject.service.implementation;

import com.example.models.AuthResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.example.springstarterproject.exception.EntityNotFoundException;
import org.example.springstarterproject.mapper.UserMapper;
import org.example.springstarterproject.model.User;
import org.example.springstarterproject.model.Role;
import org.example.springstarterproject.model.UserRole;
import org.example.springstarterproject.model.UserRoleKey;
import org.example.springstarterproject.repository.RoleRepository;
import org.example.springstarterproject.repository.UserRepository;
import org.example.springstarterproject.service.AuthenticationService;
import org.example.springstarterproject.service.TokenBlacklistService;
import org.example.springstarterproject.service.TokenService;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;

@Service
public class AuthenticationServiceImp implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final JwtDecoder jwtDecoder;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthenticationServiceImp(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository, JwtDecoder jwtDecoder, UserDetailsService userDetailsService, TokenBlacklistService tokenBlacklistService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.jwtDecoder = jwtDecoder;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    public AuthResponse login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String accessToken = tokenService.generateAccessToken(authentication);
        String refreshToken = tokenService.generateRefreshToken(authentication);

        User userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found after authentication"));

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(accessToken);
        authResponse.setRefreshToken(refreshToken);
        authResponse.setTokenType("Bearer");
        authResponse.setExpiresIn(900);
        authResponse.setUser(userMapper.toDto(userEntity));

        return authResponse;
    }

    @Transactional
    @Override
    public AuthResponse register(String email, String username, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        Role roleUser = roleRepository.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Default role not found"));

        UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(roleUser);

        userRole.setId(new UserRoleKey());

        user.setUserRoles(Set.of(userRole));

        AuthResponse authResponse = new AuthResponse();
        authResponse.setUser(userMapper.toDto(userRepository.save(user)));

        return authResponse;
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        Jwt jwt = jwtDecoder.decode(refreshToken);

        String email = jwt.getSubject();

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found after authentication"));

        String newAccessToken = tokenService.generateAccessToken(authentication);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(newAccessToken);
        authResponse.setAccessToken(newAccessToken);
        authResponse.setRefreshToken(tokenService.generateRefreshToken(authentication));
        authResponse.setTokenType("Bearer");
        authResponse.setUser(userMapper.toDto(user));
        authResponse.setExpiresIn(900);

        return authResponse;
    }

    @Override
    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth/")
                .maxAge(5 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
    }

    @Override
    public ResponseCookie deleteCookie(HttpServletRequest request) {

        String refreshToken = extractTokenFromCookie(request);

        if(refreshToken != null) {
            tokenBlacklistService.blacklistToken(refreshToken);
        }

        return ResponseCookie.from("refresh_token", "")
                .httpOnly(true)
                .secure(true)
                .path("/api/v1/auth/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if(request.getCookies() == null) {
            return null;
        }

        return Arrays.stream(request.getCookies())
                .filter(cookie -> "refresh_token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }


}
