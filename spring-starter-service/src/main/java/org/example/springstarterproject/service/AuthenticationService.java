package org.example.springstarterproject.service;

import com.example.models.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

public interface AuthenticationService {

    AuthResponse login(String email, String password);

    AuthResponse register(String email, String username, String password);

    AuthResponse refreshToken(String refreshToken);

    ResponseCookie createRefreshTokenCookie(String refreshToken);

    ResponseCookie deleteCookie(HttpServletRequest request);
}
