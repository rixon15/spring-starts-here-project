package org.example.springstarterproject.service;

import com.example.models.ProjectRequest;
import com.example.models.ProjectResponse;
import com.example.models.TaskRequest;
import com.example.models.TaskResponse;

import java.util.List;

public interface ProjectService {

    List<ProjectResponse> getAllProjects();
    void deleteProject(Long id);
    ProjectResponse getProjectById(Long id);
    ProjectResponse createProject(ProjectRequest projectRequest);
    List<TaskResponse>  getAllTasks(Long id);
    TaskResponse createTaskInProject(Long id, TaskRequest taskRequest);

}
