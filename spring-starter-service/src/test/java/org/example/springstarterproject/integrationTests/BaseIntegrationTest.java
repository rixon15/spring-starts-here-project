package org.example.springstarterproject.integrationTests;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import com.example.client.ApiClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:16");

    //We use the static block to instantiate our container manually
    //We do this to have full control, we can reuse the container across tests this way

    static {
        postgreSQLContainer.start();
    }

    @LocalServerPort
    protected int port;

    protected ApiClient getApiClient() {
        ApiClient client = new ApiClient();
        client.setBasePath("http://localhost:" + port + "/api/v1");
        return client;
    }

}
