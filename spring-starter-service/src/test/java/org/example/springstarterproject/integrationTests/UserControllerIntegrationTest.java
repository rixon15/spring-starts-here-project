package org.example.springstarterproject.integrationTests;

import com.example.client.api.UsersApi;
import com.example.client.models.UserResponse;
import com.example.client.models.UserUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class UserControllerIntegrationTest extends BaseIntegrationTest {

    private UsersApi usersApi;


    @BeforeEach
    public void setup() {
        this.usersApi = new UsersApi(getApiClient());
    }

    @Test
    @DisplayName("Should return list of users")
    void getUsers() {

        List<UserResponse> users = usersApi.getAllUsers();

        assertNotNull(users);
        assertFalse(users.isEmpty());
        assertThat(users).extracting(UserResponse::getUsername).contains("demo_user");

    }

    @Test
    @DisplayName("Should return user with given id")
    void getUserById() {

        UserResponse fetched = usersApi.getUserById(1L);

        assertNotNull(fetched);
        assertNotNull(fetched.getId());
        assertEquals(1L, fetched.getId());

    }


    @Test
    @DisplayName("Should return user with updated values")
    void updateUser() {

        UserResponse fetched = usersApi.getUserById(1L);

        UserUpdateRequest userUpdateRequest = new UserUpdateRequest();
        userUpdateRequest.setUsername("demo_user_updated");
        userUpdateRequest.setEmail("demoUserUpdated@updated.com");

        UserResponse updatedUser = usersApi.updateUser(1L, userUpdateRequest);

        assertNotNull(updatedUser);
        assertEquals("demo_user_updated", updatedUser.getUsername());
        assertEquals("demoUserUpdated@updated.com", updatedUser.getEmail());
        assertEquals(1L, updatedUser.getId());

        userUpdateRequest.setEmail(fetched.getEmail());
        userUpdateRequest.setUsername(fetched.getUsername());

        usersApi.updateUser(1L, userUpdateRequest);

    }


}
