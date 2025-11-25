package org.example.springstarterproject.controller;

import com.example.api.ProjectsApi;
import com.example.models.ProjectRequest;
import com.example.models.ProjectResponse;
import com.example.models.TaskRequest;
import com.example.models.TaskResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController implements ProjectsApi {

    @Override
    @GetMapping("/")
    public ResponseEntity<List<ProjectResponse>> projectsGet() {
        return null;
    }

    @Override
    public ResponseEntity<Void> projectsIdDelete(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<ProjectResponse> projectsIdGet(Long id) {
        return null;
    }

    @Override
    public ResponseEntity<ProjectResponse> projectsPost(ProjectRequest projectRequest) {
        return null;
    }

    @Override
    public ResponseEntity<List<TaskResponse>> projectsProjectIdTasksGet(Long projectId) {
        return null;
    }

    @Override
    public ResponseEntity<TaskResponse> projectsProjectIdTasksPost(Long projectId, TaskRequest taskRequest) {
        return null;
    }
}
