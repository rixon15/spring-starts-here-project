package org.example.springstarterproject.utils;

import org.example.springstarterproject.repository.ProjectRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("projectSecurity")
public class ProjectSecurity {

    private final ProjectRepository projectRepository;

    public ProjectSecurity(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public boolean isOwner(Authentication authentication, Long id) {

        return projectRepository.findById(id)
                .map(project -> project.getOwner().getEmail().equals(authentication.getName()))
                .orElse(false);

    }

}
