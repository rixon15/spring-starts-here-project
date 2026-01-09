package org.example.springstarterproject.integrationTests;

import com.example.models.AuthResponse;
import com.example.models.LoginRequest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import com.example.client.ApiClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    @Autowired
    protected TestRestTemplate restTemplate;

    //We use the static block to instantiate our container manually
    //We do this to have full control, we can reuse the container across tests this way

    static {
        postgreSQLContainer.start();
    }

    @LocalServerPort
    protected int port;
    @Autowired
    private Flyway flyway;

    @AfterEach
    void resetDatabase() {
        flyway.clean();
        flyway.migrate();
    }

    protected ApiClient getApiClient() {
        return getApiClient(null);
    }

    protected ApiClient getApiClient(String accessToken) {
        ApiClient client = new ApiClient();
        client.setBasePath("http://localhost:" + port + "/api/v1");
        if (accessToken != null) {
            client.setBearerToken(accessToken);
        }
        return client;
    }

    protected String loginAndGetToken(String email, String password) {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
                "/api/v1/auth/login",
                loginRequest,
                AuthResponse.class
        );

        assertEquals(200, response.getStatusCode().value(), "Login failed during test setup");
        assertNotNull(response.getBody(), "Login response body is null");

        return response.getBody().getAccessToken();

    }
}
