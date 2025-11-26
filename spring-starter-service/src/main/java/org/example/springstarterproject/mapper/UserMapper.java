package org.example.springstarterproject.mapper;

import com.example.models.Role;
import com.example.models.UserResponse;
import com.example.models.UserUpdateRequest;
import org.example.springstarterproject.model.User;
import org.example.springstarterproject.model.UserRole;
import org.mapstruct.*;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "ownedProjects", ignore = true)
    void updateUserFromDto(UserUpdateRequest dto, @MappingTarget User entity);

    @Mapping(source = "userRoles", target = "roles", qualifiedByName = "mapUserRolesToRoles")
    UserResponse toDto(User user);

    default OffsetDateTime map(Instant instant) {
        if (instant == null) {
            return null;
        }
        // Converts the Instant to UTC OffsetDateTime
        return instant.atOffset(ZoneOffset.UTC);
    }

    @Named("mapUserRolesToRoles")
    default List<Role> mapUserRolesToRoles(Set<UserRole> userRoles) {
        if (userRoles == null) {
            return List.of();
        }

        return userRoles.stream()
                .map(userRole -> {
                    var entityRole = userRole.getRole();

                    Role dtoRole = new Role();

                    dtoRole.setId(entityRole.getId());
                    dtoRole.setName(entityRole.getName());

                    return dtoRole;
                })
                .collect(Collectors.toList());
    }
}
