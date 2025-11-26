package org.example.springstarterproject.mapper;

import com.example.models.ProjectRequest;
import com.example.models.ProjectResponse;
import org.example.springstarterproject.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProjectResponse toDto(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true) // We will handle this in the service
    @Mapping(target = "tasks", ignore = true)
    Project fromDto(ProjectRequest request);



}
