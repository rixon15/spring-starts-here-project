package org.example.springstarterproject.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Data
@Table(name = "user_roles")
@NoArgsConstructor
public class UserRole {

    @EmbeddedId
    private UserRoleKey id;


    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

//    public UserRole(User user, Role role) {
//        this.user = user;
//        this.role = role;
//        this.id = new UserRoleKey(user.getId(), role.getId());
//    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserRole that = (UserRole) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
