package org.example.springstarterproject.controller;

import com.example.api.TasksApi;
import com.example.models.TaskRequest;
import com.example.models.TaskResponse;
import jakarta.validation.Valid;
import org.example.springstarterproject.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController implements TasksApi {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        taskService.deleteTask(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Override
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        return new ResponseEntity<>(taskService.getTaskById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @Override
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, @Valid TaskRequest taskRequest) {
        return new ResponseEntity<>(taskService.updateTask(id, taskRequest), HttpStatus.OK);
    }

}
