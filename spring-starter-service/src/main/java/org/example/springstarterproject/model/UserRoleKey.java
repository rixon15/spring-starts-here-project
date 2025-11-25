package org.example.springstarterproject.model;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleKey {

    @NotNull
    private long userId;
    @NotNull
    private long roleId;

    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserRoleKey that = (UserRoleKey) obj;
        return Objects.equals(userId, that.userId) && Objects.equals(roleId, that.roleId);
    }
}
