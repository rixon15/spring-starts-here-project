package org.example.springstarterproject.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotNull
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User user;
    @NotNull
    private String title;
    @NotNull
    private String description;
    @NotNull
    @Enumerated(EnumType.STRING)
    private TaskStatus status = TaskStatus.TODO;
    @NotNull
    private Date dueDate;
    @CreatedDate // <--- 2. Spring sets this on INSERT
    @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @LastModifiedDate // <--- 3. Spring updates this on every UPDATE
    @Column(nullable = false)
    private Instant updatedAt;


}
