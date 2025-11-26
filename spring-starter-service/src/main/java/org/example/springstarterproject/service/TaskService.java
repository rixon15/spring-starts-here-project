package org.example.springstarterproject.service;

import com.example.models.TaskRequest;
import com.example.models.TaskResponse;

public interface TaskService {

    void deleteTask(Long id);
    TaskResponse getTaskById(Long id);
    TaskResponse updateTask(Long id, TaskRequest taskRequest);

}
