package org.example.springstarterproject.integrationTests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class SecurityIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Public Endpoints: Should allow access to register and login without token")
    void publicEndpointsWork() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"test\", \"email\":\"test@test.com\", \"password\":\"password\"}"))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"demo@example.com\", \"password\":\"demo\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Protected Endpoints: Should return 401 Unauthorized when no token is provided")
    void protectedEndpointsRejectUnauthenticated() throws Exception {
        mvc.perform(get("/api/v1/projects"))
                .andExpect(status().isUnauthorized());

        mvc.perform(get("/api/v1/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@test.com")
    @DisplayName("Authenticated User: Should allow access to protected endpoints with valid mock user")
    void protectedEndpointsAllowAuthentication() throws Exception {
        mvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Swagger/OpenAPI:Should be accessible publicly")
    void swaggerEndpointIsPublic() throws Exception {
        mvc.perform(get("/api/v1/v3/api-docs/swagger-config"))
                .andExpect(status().isOk());
        mvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "malicious_user@example.com")
    @DisplayName("Method security: Should return 403 when trying to delete a project owned by someone else")
    void rejectDeletionByNonOwner() throws Exception {
        mvc.perform(delete("/api/v1/projects/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "demo@example.com")
    @DisplayName("Method security: SShould allow owner to delete their project")
    void allowDeletionByOwner() throws Exception {
        mvc.perform(delete("/api/v1/projects/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Login: Should return 200 and set a secure, http only refresh token cookie")
    void LoginSetsSecureCookie() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"demo@example.com\", \"password\":\"demo\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().httpOnly("refresh_token", true))
                .andExpect(cookie().secure("refresh_token", true))
                .andExpect(cookie().value("refresh_token", org.hamcrest.Matchers.not(org.hamcrest.Matchers.emptyString())));
    }

    @Test
    @WithMockUser
    @DisplayName("Logout: Should clear the refresh token cookie")
    void logoutClearsCookie() throws Exception {
        mvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isOk())
                .andExpect(cookie().exists("refresh_token"))
                .andExpect(cookie().maxAge("refresh_token", 0));
    }

    @Test
    @WithMockUser(username = "demo@example.com")
    @DisplayName("Create Project: Should automatically assign the logged-in user as owner of the project")
    void createProjectAssignsCurrentOwner() throws Exception {
        mvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Sec Project\", \"description\":\"Testing ownership\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Sec Project"))
                .andExpect(jsonPath("$.ownerId").value(1));
    }

}
