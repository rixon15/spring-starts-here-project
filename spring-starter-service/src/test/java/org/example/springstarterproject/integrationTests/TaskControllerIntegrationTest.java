package org.example.springstarterproject.integrationTests;

import com.example.client.api.TasksApi;
import com.example.client.models.TaskRequest;
import com.example.client.models.TaskResponse;
import com.example.client.models.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TaskControllerIntegrationTest extends BaseIntegrationTest {

    private TasksApi tasksApi;

    @BeforeEach
    void SetUp() {

        String accessToken = loginAndGetToken("demo@example.com", "demo");

        this.tasksApi = new TasksApi(getApiClient(accessToken));
    }

    @Test
    @DisplayName("Should delete task with id")
    void deleteTaskById() {

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Create New Task");
        taskRequest.setDescription("Create New Task");
        taskRequest.setDueDate(LocalDate.now());
        taskRequest.setStatus(TaskStatus.TODO);
        taskRequest.setAssigneeId(1L);
        TaskResponse createdTask = tasksApi.createTask(1L, taskRequest);

        tasksApi.deleteTask(createdTask.getId());

        assertThrows(HttpClientErrorException.NotFound.class, () -> tasksApi.getTaskById(createdTask.getId()));

    }

    @Test
    @DisplayName("Should return task with id")
    void getTaskById() {

        TaskResponse taskResponse = tasksApi.getTaskById(1L);

        assertNotNull(taskResponse);
        assertNotNull(taskResponse.getId());
        assertEquals(1L, taskResponse.getId());

    }

    @Test
    @DisplayName("Should create task associated with project id")
    void createTask() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Create New Task");
        taskRequest.setDescription("Create New Task");
        taskRequest.setDueDate(LocalDate.now());
        taskRequest.setStatus(TaskStatus.TODO);
        taskRequest.setAssigneeId(1L);

        TaskResponse createdTask = tasksApi.createTask(1L, taskRequest);

        assertNotNull(createdTask);
        assertNotNull(createdTask.getId());
        assertEquals(1L, createdTask.getProjectId());

        TaskResponse fetched = tasksApi.getTaskById(createdTask.getId());
        assertEquals(createdTask.getId(), fetched.getId());
        assertEquals("Create New Task", fetched.getTitle());

    }

    @Test
    @DisplayName("Should list all tasks for a given project")
    void getAllTasks() {

        List<TaskResponse> taskList = tasksApi.getTasksByProjectId(1L);

        assertNotNull(taskList);
        assertFalse(taskList.isEmpty());
        assertThat(taskList).extracting(TaskResponse::getTitle).contains("Design Figma Mockups", "Implement Frontend Component");
        assertThat(taskList).extracting(TaskResponse::getProjectId).contains(1L);

    }

    @Test
    @DisplayName("Should update task with id")
    void updateTask() {

        TaskResponse fetchedTask = tasksApi.getTaskById(1L);

        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Update Task");
        taskRequest.setDescription("Update Task");
        taskRequest.setDueDate(LocalDate.now());
        taskRequest.setStatus(TaskStatus.TODO);
        taskRequest.setAssigneeId(1L);

        TaskResponse updatedTask = tasksApi.updateTask(1L, taskRequest);

        assertNotNull(updatedTask);
        assertEquals("Update Task", updatedTask.getTitle());
        assertEquals("Update Task", updatedTask.getDescription());
        assertEquals(1L, updatedTask.getId());

        taskRequest.setTitle(fetchedTask.getTitle());
        taskRequest.setDescription(fetchedTask.getDescription());

        tasksApi.updateTask(1L, taskRequest);


    }

}
