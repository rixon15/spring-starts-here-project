package org.example.springstarterproject.mapper;

import com.example.models.ProjectRequest;
import com.example.models.ProjectResponse;
import org.example.springstarterproject.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(source = "owner.id", target = "ownerId")
    ProjectResponse toDto(Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true) // We will handle this in the service
    @Mapping(target = "tasks", ignore = true)
    Project fromDto(ProjectRequest request);

    default OffsetDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        // Converts the Instant to UTC OffsetDateTime
        return instant.atOffset(ZoneOffset.UTC);
    }

}
