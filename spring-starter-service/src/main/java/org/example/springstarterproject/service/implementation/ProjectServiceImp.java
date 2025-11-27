package org.example.springstarterproject.service.implementation;

import com.example.models.ProjectRequest;
import com.example.models.ProjectResponse;
import com.example.models.TaskRequest;
import com.example.models.TaskResponse;
import org.example.springstarterproject.exception.EntityNotFoundException;
import org.example.springstarterproject.mapper.ProjectMapper;
import org.example.springstarterproject.mapper.TaskMapper;
import org.example.springstarterproject.model.Project;
import org.example.springstarterproject.model.Task;
import org.example.springstarterproject.model.User;
import org.example.springstarterproject.repository.ProjectRepository;
import org.example.springstarterproject.repository.TaskRepository;
import org.example.springstarterproject.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImp implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public ProjectServiceImp(ProjectRepository projectRepository, ProjectMapper projectMapper, TaskRepository taskRepository, TaskMapper taskMapper) {
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAll()
                .stream().map(projectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProject(Long id) {

        //TODO: User has to be the owner of the project

        projectRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(id));

        projectRepository.deleteById(id);


    }

    @Override
    public ProjectResponse getProjectById(Long id) {
        Project result = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        return projectMapper.toDto(result);
    }

    @Override
    public ProjectResponse createProject(ProjectRequest projectRequest) {
        Project project = projectMapper.fromDto(projectRequest);

        // TODO: Set the owner of the project to the currently logged in user
        project.setOwner(new User(1));

        return projectMapper.toDto(projectRepository.save(project));
    }


    @Override
    public List<TaskResponse> getAllTasks(Long id) {

        projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        List<Task> tasks = taskRepository.findTaskByProject_Id(id);

        return tasks.stream().map(taskMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public TaskResponse createTaskInProject(Long id, TaskRequest taskRequest) {

        //TODO: If current user is not the owner of the project the task has to be created in

        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(id));

        Task task = taskMapper.fromDto(taskRequest);
        task.setProject(project);

        return taskMapper.toDto(taskRepository.save(task));

    }


}
