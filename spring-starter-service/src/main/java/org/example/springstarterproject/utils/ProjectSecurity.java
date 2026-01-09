package org.example.springstarterproject.utils;

import org.example.springstarterproject.repository.ProjectRepository;
import org.example.springstarterproject.repository.TaskRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("projectSecurity")
public class ProjectSecurity {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectSecurity(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public boolean isProjectOwner(Authentication authentication, Long id) {

        return projectRepository.findById(id)
                .map(project -> project.getOwner().getEmail().equals(authentication.getName()))
                .orElse(false);

    }

    @Transactional(readOnly = true)
    public boolean isTaskOwner(Authentication authentication, Long taskId) {
        return taskRepository.findById(taskId)
                .map(task -> {
                    var project = task.getProject();
                    var owner = (project != null) ? project.getOwner() : null;
                    return owner != null && owner.getEmail().equals(authentication.getName());
                })
                .orElse(false);
    }

}
