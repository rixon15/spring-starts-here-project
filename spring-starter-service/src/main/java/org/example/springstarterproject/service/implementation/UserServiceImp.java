package org.example.springstarterproject.service.implementation;

import com.example.models.UserResponse;
import com.example.models.UserUpdateRequest;
import org.example.springstarterproject.exception.EntityNotFoundException;
import org.example.springstarterproject.mapper.UserMapper;
import org.example.springstarterproject.model.User;
import org.example.springstarterproject.repository.UserRepository;
import org.example.springstarterproject.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImp(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        if (userUpdateRequest.getEmail() != null
                && !userUpdateRequest.getEmail().isBlank()
                && !userUpdateRequest.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(userUpdateRequest.getEmail())) {
                throw new IllegalArgumentException("Email already in use");
            }
        }

        if (userUpdateRequest.getUsername() != null && userUpdateRequest.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username cannot be whitespace");
        }

        userMapper.updateUserFromDto(userUpdateRequest, user);

        return userMapper.toDto(userRepository.save(user));
    }

    @Override
    public User processOAuthLogin(String email) {

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername("{noop}HasToBePromptedOnFirstLoginToSetUsername");
        newUser.setPassword("{noop}HasToBePromptedOnFirstLoginToSetPassword");

        userRepository.save(newUser);
        return newUser;
    }
}
