package org.example.springstarterproject.controller;

import com.example.api.UsersApi;
import com.example.models.UserResponse;
import com.example.models.UserUpdateRequest;
import jakarta.validation.Valid;
import org.example.springstarterproject.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController implements UsersApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Override
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.FOUND);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid UserUpdateRequest userUpdateRequest) {
        return new ResponseEntity<>(userService.updateUser(id, userUpdateRequest), HttpStatus.OK);
    }



}
