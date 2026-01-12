package org.example.springstarterproject.unitTests;

import com.example.models.TaskRequest;
import com.example.models.TaskResponse;
import org.example.springstarterproject.exception.EntityNotFoundException;
import org.example.springstarterproject.mapper.TaskMapper;
import org.example.springstarterproject.model.Task;
import org.example.springstarterproject.repository.TaskRepository;
import org.example.springstarterproject.repository.UserRepository;
import org.example.springstarterproject.service.implementation.TaskServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceUnitTest {

    @Mock
    TaskRepository taskRepository;
    @Mock
    TaskMapper taskMapper;
    @Mock
    UserRepository userRepository;

    @InjectMocks
    TaskServiceImp taskService;

    @Test
    @DisplayName("deleteTask: Should delete task with given id")
    void deleteTask_HappyPath() {

        Task task = new Task();

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

        taskService.deleteTask(anyLong());

        verify(taskRepository, times(1)).delete(task);
        verify(taskRepository, times(1)).findById(anyLong());

    }

    @Test
    @DisplayName("deleteTask: Should throw EntityNotFoundException if Task doesn't exist")
    void deleteTask_Exception_TaskDoesNotExist() {

        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.deleteTask(1L));

        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskRepository, never()).delete(any());

    }

    @Test
    @DisplayName("getTaskById: Should return a task with the given id")
    void getTaskById_HappyPath() {

        Task task = new Task();
        task.setId(1);
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());

        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponse);

        TaskResponse result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(task.getId(), result.getId());
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskMapper, times(1)).toDto(task);

    }

    @Nested
    @DisplayName("Update task validation failures")
    class ValidationFailures {

        @Test
        @DisplayName("Should throw EntityNotFound if Task Id does not exist")
        void taskNotFound() {

            TaskRequest taskRequest = new TaskRequest();

            when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(1L, taskRequest));

        }

        @Test
        @DisplayName("Should throw IllegalArgument if Title is whitespace")
        void titleWhitespace() {
            Task task = new Task();
            TaskRequest taskRequest = new TaskRequest();
            taskRequest.setTitle("      ");

            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(1L, taskRequest));

            assertEquals("Task title cannot be whitespace", exception.getMessage());


        }

        @Test
        @DisplayName("Should throw IllegalArgument if Description is whitespace")
        void descriptionWhitespace() {
            Task task = new Task();
            TaskRequest taskRequest = new TaskRequest();
            taskRequest.setDescription("      ");

            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(1L, taskRequest));

            assertEquals("Task description cannot be whitespace", exception.getMessage());


        }

        @Test
        @DisplayName("Should throw IllegalArgument if due date is in the past")
        void pastDueDate() {
            Task task = new Task();
            TaskRequest taskRequest = new TaskRequest();
            taskRequest.setDueDate(LocalDate.now().minusDays(1));

            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(1L, taskRequest));

            assertEquals("Due date cannot be in the past", exception.getMessage());

        }

    }

    @Nested
    @DisplayName("Update task assignee logic")
    class AssigneeLogic {

        @Test
        @DisplayName("Should throw EntityNotFoundException if new Assignee id does not exist")
        void assigneeNotFound() {

            Task task = new Task();
            TaskRequest taskRequest = new TaskRequest();
            taskRequest.setAssigneeId(JsonNullable.of(50L));

            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
            when(userRepository.existsById(anyLong())).thenReturn(false);

            Long testNum = anyLong();

            assertThrows(EntityNotFoundException.class, () -> taskService.updateTask(testNum, taskRequest));

        }

        @Test
        @DisplayName("Should nor check user existence if assignee is et to null")
        void noAssignedUser() {
            Task task = new Task();
            TaskRequest taskRequest = new TaskRequest();

            taskRequest.setAssigneeId(JsonNullable.of(null));

            TaskResponse taskResponse = new TaskResponse();

            when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
            when(taskRepository.save(any())).thenReturn(task);
            when(taskMapper.toDto(task)).thenReturn(taskResponse);

            taskService.updateTask(anyLong(), taskRequest);

            verify(userRepository, never()).existsById(anyLong());
            verify(taskRepository).save(task);

        }

    }

    @Nested
    @DisplayName("Update task happy path")
    class HappyPath {

        @Test
        @DisplayName("Should update task successfully with valid data")
        void success() {

            Task task = new Task();
            task.setId(1);

            TaskRequest taskRequest = new TaskRequest();
            taskRequest.setTitle("Valid title");
            taskRequest.setAssigneeId(JsonNullable.of(50L));

            TaskResponse taskResponse = new TaskResponse();
            taskResponse.setId(task.getId());
            taskResponse.setTitle("Valid title");

            when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
            when(userRepository.existsById(50L)).thenReturn(true);
            when(taskRepository.save(task)).thenReturn(task);
            when(taskMapper.toDto(task)).thenReturn(taskResponse);

            TaskResponse result = taskService.updateTask(1L, taskRequest);

            assertNotNull(result);
            verify(taskMapper).updateTaskFromDto(taskRequest, task);
            verify(taskRepository).save(task);

        }

    }

}
