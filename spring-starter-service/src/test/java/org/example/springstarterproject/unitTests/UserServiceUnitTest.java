package org.example.springstarterproject.unitTests;

import com.example.models.UserResponse;
import com.example.models.UserUpdateRequest;
import org.example.springstarterproject.exception.EntityNotFoundException;
import org.example.springstarterproject.mapper.UserMapper;
import org.example.springstarterproject.model.User;
import org.example.springstarterproject.repository.UserRepository;
import org.example.springstarterproject.service.implementation.UserServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserServiceImp userService;

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsers {
        @Test
        @DisplayName("Should return List of UserResponse")
        void getAllUsers_HappyPath() {

            User user = new User();

            when(userRepository.findAll()).thenReturn(List.of(user));

            List<UserResponse> result = userService.getAllUsers();

            assertNotNull(result);
            assertEquals(1, result.size());

            verify(userMapper, times(1)).toDto(user);
            verify(userRepository, times(1)).findAll();

        }

        @Test
        @DisplayName("Should return empty list with no users present")
        void getAllUsers_EmptyList() {

            User user = new User();

            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            List<UserResponse> result = userService.getAllUsers();

            assertNotNull(result);
            assertEquals(0, result.size());

            verify(userRepository, times(1)).findAll();
            verify(userMapper, never()).toDto(user);
        }
    }

    @Nested
    @DisplayName("gerUserById")
    class GerUserById {

        @Test
        @DisplayName("Should return user with id")
        void getUserById_HappyPath() {

            User user = new User();
            UserResponse userResponse = new UserResponse();

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(userMapper.toDto(user)).thenReturn(userResponse);

            var result = userService.getUserById(anyLong());

            assertNotNull(result);
            assertEquals(userResponse, result);
            verify(userRepository, times(1)).findById(anyLong());
            verify(userMapper, times(1)).toDto(user);

        }

        @Test
        @DisplayName("Should throw EntityNotFoundException if user with id not found")
        void getUserById_NotFound() {

            User user = new User();

            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    userService.getUserById(1L));

            verify(userRepository, times(1)).findById(anyLong());
            verify(userMapper, never()).toDto(user);

        }

    }

    @Nested
    @DisplayName("updateUser validations")
    class UpdateUser {

        @Test
        @DisplayName("Should throw EntityNotFoundException if user with id cannot be found")
        void userNotFound() {

            UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
            when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () ->
                    userService.updateUser(1L, userUpdateRequest));

        }

        @Test
        @DisplayName("Should throw IllegalArgument if Username is whitespace")
        void usernameWhitespace() {
            User user = new User();

            UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
            userUpdateRequest.setUsername(" ");

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    userService.updateUser(1L, userUpdateRequest));

            assertEquals("Username cannot be whitespace", ex.getMessage());
        }

        @Test
        @DisplayName("Should throw Exception if new Email is already in use")
        void emailTaken() {
            String oldEmail = "old@test.com";
            String newEmail = "taken@test.com";

            User user = new User();
            user.setEmail(oldEmail);

            UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
            userUpdateRequest.setEmail(newEmail);

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(userRepository.existsByEmail(newEmail)).thenReturn(true);

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                    userService.updateUser(1L, userUpdateRequest));

            assertEquals("Email already in use", ex.getMessage());

        }

    }

    @Nested
    @DisplayName("updateUser happy paths")
    class HappyPaths {

        @Test
        @DisplayName("Should update user successfully")
        void success() {
            User user = new User();
            user.setId(1L);
            user.setEmail("old@test.com");

            UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
            userUpdateRequest.setUsername("NewName");
            userUpdateRequest.setEmail("new@test.com");

            UserResponse userResponse = new UserResponse();
            userResponse.setUsername("NewName");

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(userResponse);

            UserResponse result = userService.updateUser(anyLong(), userUpdateRequest);

            assertNotNull(result);
            verify(userMapper).updateUserFromDto(userUpdateRequest, user);
            verify(userRepository).save(user);

        }

        @Test
        @DisplayName("Edge Case: Should SKIP existsByEmail check if email is same as current")
        void sameEmailSkipCheck() {
            String email = "email@test.com";

            User user = new User();
            user.setEmail(email);

            UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
            userUpdateRequest.setEmail(email);

            UserResponse userResponse = new UserResponse();

            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(userRepository.save(user)).thenReturn(user);
            when(userMapper.toDto(user)).thenReturn(userResponse);

            userService.updateUser(anyLong(), userUpdateRequest);

            verify(userRepository, never()).existsByEmail(anyString());

            verify(userRepository).save(user);

        }

    }

}
