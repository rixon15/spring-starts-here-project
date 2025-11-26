package org.example.springstarterproject.service.implementation;

import com.example.models.TaskRequest;
import com.example.models.TaskResponse;
import jakarta.transaction.Transactional;
import org.example.springstarterproject.exception.EntityNotFoundException;
import org.example.springstarterproject.mapper.TaskMapper;
import org.example.springstarterproject.model.Task;
import org.example.springstarterproject.repository.TaskRepository;
import org.example.springstarterproject.repository.UserRepository;
import org.example.springstarterproject.service.TaskService;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TaskServiceImp implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;

    public TaskServiceImp(TaskRepository taskRepository, TaskMapper taskMapper, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.userRepository = userRepository;
    }

    @Override
    public void deleteTask(Long id) {

        //TODO: User has to be the owner of the project we want to delete

        Task deletedTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        taskRepository.delete(deletedTask);
    }

    @Override
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        return taskMapper.toDto(task);
    }

    @Transactional
    @Override
    public TaskResponse updateTask(Long id, TaskRequest taskRequest) {

        //TODO: if current User is not the owner of the project the task belongs to

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        if (taskRequest.getTitle() != null && taskRequest.getTitle().isBlank()) {
            throw new IllegalArgumentException("Task title cannot be whitespace");
        }

        if (taskRequest.getDueDate() != null
                && taskRequest.getDueDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }

        JsonNullable<Long> assigneeIdWrapper = taskRequest.getAssigneeId();

        if (assigneeIdWrapper != null && assigneeIdWrapper.isPresent()) {

            Long newAssigneeId = assigneeIdWrapper.get(); // This can be a Long or null

            if (newAssigneeId != null) {
                boolean userExists = userRepository.existsById(newAssigneeId);
                if (!userExists) {
                    throw new EntityNotFoundException(newAssigneeId);
                }
            }

        }

        taskMapper.updateTaskFromDto(taskRequest, task);

        return taskMapper.toDto(taskRepository.save(task));
    }
}
