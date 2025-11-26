package org.example.springstarterproject.controller;

import com.example.api.ProjectsApi;
import com.example.models.ProjectRequest;
import com.example.models.ProjectResponse;
import com.example.models.TaskRequest;
import com.example.models.TaskResponse;
import org.example.springstarterproject.service.implementation.ProjectServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController implements ProjectsApi {

    private final ProjectServiceImp projectService;

    public ProjectController(ProjectServiceImp projectService) {
        this.projectService = projectService;
    }

    //Get list of projects
    @Override
    @GetMapping("/")
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        return new ResponseEntity<>(projectService.getAllProjects(), HttpStatus.OK);

    }

    @DeleteMapping("/{id}")
    @Override
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/{id}")
    @Override
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable Long id) {
        return new ResponseEntity<>(projectService.getProjectById(id), HttpStatus.OK);
    }

    @PostMapping("/")
    @Override
    public ResponseEntity<ProjectResponse> createProject(@RequestBody ProjectRequest projectRequest) {
        return new ResponseEntity<>(projectService.createProject(projectRequest), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/tasks")
    @Override
    public ResponseEntity<List<TaskResponse>> getTasksByProjectId(@PathVariable Long id) {
        return new ResponseEntity<>(projectService.getAllTasks(id), HttpStatus.OK);
    }

    @PostMapping("/{id}/tasks")
    @Override
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long id, TaskRequest taskRequest) {
        return new ResponseEntity<>(projectService.createTaskInProject(id, taskRequest), HttpStatus.CREATED);
    }
}
