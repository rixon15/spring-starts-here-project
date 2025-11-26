package org.example.springstarterproject.mapper;

import com.example.models.TaskRequest;
import com.example.models.TaskResponse;
import org.example.springstarterproject.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.jackson.nullable.JsonNullable;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "user.id", target = "assigneeId")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    TaskResponse toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true) // handled inside service
    @Mapping(target = "user", ignore = true) // handled inside service
    @Mapping(target = "status", source = "status")
    Task fromDto(TaskRequest request);

    default JsonNullable<Long> map(Long value) {
        return value != null ? JsonNullable.of(value) : JsonNullable.undefined();
    }

}
