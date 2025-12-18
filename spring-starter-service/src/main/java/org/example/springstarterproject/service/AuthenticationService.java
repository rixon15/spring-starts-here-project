package org.example.springstarterproject.service;

import com.example.models.AuthResponse;

public interface AuthenticationService {

    AuthResponse login(String email, String password);
    AuthResponse register(String email, String username, String password);

}
