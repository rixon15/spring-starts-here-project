package org.example.springstarterproject.repository;

import org.example.springstarterproject.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    List<Task> findTaskByProject_Id(Long id);
}
