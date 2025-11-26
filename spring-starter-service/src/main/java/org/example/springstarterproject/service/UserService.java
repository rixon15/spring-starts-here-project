package org.example.springstarterproject.service;

import com.example.models.UserResponse;
import com.example.models.UserUpdateRequest;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest);
}
