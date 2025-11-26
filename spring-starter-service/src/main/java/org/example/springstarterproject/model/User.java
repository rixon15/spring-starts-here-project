package org.example.springstarterproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private long id;

    @NotNull
    private String username;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @CreatedDate // <--- 2. Spring sets this on INSERT
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate // <--- 3. Spring updates this on every UPDATE
    @Column(nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Project> ownedProjects;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<UserRole> userRoles;

    public User(long id) {
        this.id = id;
    }

}
