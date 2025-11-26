package org.example.springstarterproject.mapper;

import com.example.models.UserResponse;
import com.example.models.UserUpdateRequest;
import org.example.springstarterproject.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    // This defines: Update existing 'target' using 'source'
    void updateUserFromDto(UserUpdateRequest dto, @MappingTarget User entity);

    UserResponse toDto(User user);

    default OffsetDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        // Converts the Instant to UTC OffsetDateTime
        return instant.atOffset(ZoneOffset.UTC);
    }
}
