package org.example.springstarterproject.mapper;

import com.example.models.TaskRequest;
import com.example.models.TaskResponse;
import org.example.springstarterproject.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.openapitools.jackson.nullable.JsonNullable;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "user.id", target = "assigneeId")
    TaskResponse toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true) // handled inside service
    @Mapping(target = "user", ignore = true) // handled inside service
    @Mapping(target = "status", source = "status")
    Task fromDto(TaskRequest request);

    void updateTaskFromDto(TaskRequest dto, @MappingTarget Task entity);

    default JsonNullable<Long> map(Long value) {
        return value != null ? JsonNullable.of(value) : JsonNullable.undefined();
    }

    default OffsetDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        // Converts the Instant to UTC OffsetDateTime
        return instant.atOffset(ZoneOffset.UTC);
    }

}
