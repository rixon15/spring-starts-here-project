package org.example.springstarterproject.service.implementation;

import com.example.models.AuthResponse;
import org.example.springstarterproject.exception.EntityNotFoundException;
import org.example.springstarterproject.mapper.UserMapper;
import org.example.springstarterproject.model.User;
import org.example.springstarterproject.repository.UserRepository;
import org.example.springstarterproject.service.AuthenticationService;
import org.example.springstarterproject.service.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImp implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public AuthenticationServiceImp(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository, UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public AuthResponse login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        String token = tokenService.generateToken(authentication);

        User userEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found after authentication"));

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(token);
        authResponse.setTokenType("Bearer");
        authResponse.setExpiresIn(600);
        authResponse.setUser(userMapper.toDto(userEntity));

        return authResponse;
    }
}
