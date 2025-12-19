package org.example.springstarterproject.integrationTests;

import com.example.client.api.ProjectsApi;
import com.example.client.models.ProjectRequest;
import com.example.client.models.ProjectResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ProjectControllerIntegrationTest extends BaseIntegrationTest {

    private ProjectsApi projectsApi;

    @BeforeEach
    void SetUp() {

        String accessToken = loginAndGetToken("demo@example.com", "demo");

        this.projectsApi = new ProjectsApi(getApiClient(accessToken));
    }

    @Test
    @DisplayName("Create a project and retrieve it from db")
    void shouldCreateAndRetrieveProject() {

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Integration test project");
        projectRequest.setDescription("Testing with test container");

        ProjectResponse created = projectsApi.createProject(projectRequest);

        assertNotNull(created.getId());
        assertEquals("Integration test project", created.getName());
        assertEquals(1L, created.getOwnerId());
        //This has to be checked against the currently logged-in user, currently 1L hardcoded

        ProjectResponse fetched = projectsApi.getProjectById(created.getId());
        assertEquals("Integration test project", fetched.getName());

    }

    @Test
    @DisplayName("Should retrieve all projects from db")
    void ShouldListAllProjects() {

        List<ProjectResponse> projects = projectsApi.getAllProjects();

        assertNotNull(projects);
        assertFalse(projects.isEmpty());
        assertThat(projects).extracting(ProjectResponse::getName).contains("Website Redesign", "Mobile App");

    }

    @Test
    @DisplayName("Delete project with given id")
    void shouldDeleteProject() {
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName("Delete me");
        projectRequest.setDescription("Temporary");

        ProjectResponse created = projectsApi.createProject(projectRequest);

        projectsApi.deleteProject(created.getId());

        assertThrows(HttpClientErrorException.NotFound.class,() -> projectsApi.getProjectById(created.getId()));

    }

    @Test
    @DisplayName("Should get project with id")
    void getProjectById() {

        ProjectResponse response = projectsApi.getProjectById(1L);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(1,  response.getId());

    }

}
