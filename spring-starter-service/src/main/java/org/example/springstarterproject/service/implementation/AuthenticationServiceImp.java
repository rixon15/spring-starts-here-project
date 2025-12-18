package org.example.springstarterproject.service.implementation;

import com.example.models.AuthResponse;
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
import org.example.springstarterproject.service.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthenticationServiceImp implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public AuthenticationServiceImp(AuthenticationManager authenticationManager, TokenService tokenService, UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
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
}
