package org.example.springstarterproject;

import com.example.models.ProjectRequest;
import com.example.models.ProjectResponse;
import com.example.models.TaskRequest;
import com.example.models.TaskResponse;
import org.example.springstarterproject.exception.EntityNotFoundException;
import org.example.springstarterproject.mapper.ProjectMapper;
import org.example.springstarterproject.mapper.TaskMapper;
import org.example.springstarterproject.model.Project;
import org.example.springstarterproject.model.Task;
import org.example.springstarterproject.repository.ProjectRepository;
import org.example.springstarterproject.repository.TaskRepository;
import org.example.springstarterproject.service.implementation.ProjectServiceImp;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceUnitTest {

    @Mock
    ProjectRepository projectRepository;
    @Mock
    ProjectMapper projectMapper;
    @Mock
    TaskRepository taskRepository;
    @Mock
    TaskMapper taskMapper;

    @InjectMocks
    private ProjectServiceImp projectService;

    @Test
    @DisplayName("getAllProjects: Should return a list of projects")
    public void getAllProjects_HappyFlow() {
        Project project = new Project();
        ProjectResponse projectResponse = new ProjectResponse();

        when(projectRepository.findAll()).thenReturn(List.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectResponse);

        List<ProjectResponse> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(projectRepository, times(1)).findAll();

    }

    @Test
    @DisplayName("getAllProjects: Should return an empty list when project doesnt exist or has no tasks")
    public void getAllProjects_ValidExecution() {

        when(projectRepository.findAll()).thenReturn(Collections.emptyList());

        List<ProjectResponse> result = projectService.getAllProjects();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(projectRepository, times(1)).findAll();
        verifyNoInteractions(projectMapper);

    }

    @Test
    @DisplayName("deleteProject: Should call repository and delete project if it exists")
    public void deleteProject_HappyFlow() {
        Project project = new Project();
        project.setId(1L);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteProject(1L);

        verify(projectRepository).findById(1L);

        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteProject: Should throw EntityNotFoundException if the project doesn't exist")
    public void deleteProject_Exception_ProjectDoesNotExist() {
        when(projectRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.deleteProject(99L));

        verify(projectRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("getProjectById: Should return project with given id")
    public void getProjectById_HappyFlow() {

        Project project = new Project();
        project.setId(1L);
        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setId(project.getId());

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectMapper.toDto(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getProjectById: Should throw EntityNotFoundException when project with id doesn't exist")
    public void getProjectById_Exception_ProjectDoesNotExist() {

        Project project = new Project();

        when(projectRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.getProjectById(99L));

        verify(projectMapper, never()).toDto(project);

    }

    @Test
    @DisplayName("createProject: Should return newly created project")
    public void createProject_HappyFlow() {

        String name = "Project";

        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName(name);
        ProjectResponse projectResponse = new ProjectResponse();
        projectResponse.setName(name);
        Project project = new Project();
        project.setName(name);

        when(projectMapper.fromDto(projectRequest)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(project);
        when(projectMapper.toDto(project)).thenReturn(projectResponse);

        ProjectResponse result = projectService.createProject(projectRequest);

        assertNotNull(result);
        assertEquals(name, result.getName());
        verify(projectRepository, times(1)).save(project);

        //Check for user
        verify(projectRepository).save(argThat(argument ->
                argument.getName().equals(name) &&
                        argument.getOwner() != null &&
                        argument.getOwner().getId() == 1
        ));
    }

    @Test
    @DisplayName("createProject: Should throw exception when db fails")
    public void createProject_Exception_dbFails() {
        String name = "Project";
        ProjectRequest projectRequest = new ProjectRequest();
        projectRequest.setName(name);

        Project project = new Project();
        project.setName(name);

        when(projectMapper.fromDto(projectRequest)).thenReturn(project);
        when(projectRepository.save(project)).thenThrow(new DataIntegrityViolationException("Name already exists"));


        assertThrows(org.springframework.dao.DataIntegrityViolationException.class, () ->
                projectService.createProject(projectRequest));

        verify(projectMapper, never()).toDto(any());

    }

    @Test
    @DisplayName("getAllTasks: Should return all tasks for given project by id")
    public void getAllTasks_HappyFlow() {

        Task task = new Task();
        task.setId(1L);
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(task.getId());
        Project project = new Project();

        when(taskRepository.findTaskByProject_Id(anyLong())).thenReturn(List.of(task));
        when(taskMapper.toDto(task)).thenReturn(taskResponse);
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        List<TaskResponse> result = projectService.getAllTasks(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(taskResponse.getId(), result.getFirst().getId());

        verify(taskRepository, times(1)).findTaskByProject_Id(anyLong());
        verify(taskMapper, times(1)).toDto(task);


    }

    @Test
    @DisplayName("getAllTasks: Should return empty list if there are no tasks associated with the given project id")
    public void getAllTasks_ValidExecution() {

        Project project = new Project();

        when(taskRepository.findTaskByProject_Id(anyLong())).thenReturn(Collections.emptyList());
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));

        List<TaskResponse> result = projectService.getAllTasks(1L);

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(taskRepository, times(1)).findTaskByProject_Id(anyLong());
        verify(taskMapper, never()).toDto(any());

    }

    @Test
    @DisplayName("getAllTasks: Should throw EntityNotFoundException when project with id doesn't exit")
    public void getAllTasks_Exception_ProjectDoesNotExit() {

        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.getAllTasks(1L));

        verify(taskRepository, never()).findTaskByProject_Id(anyLong());
        verify(taskMapper, never()).toDto(any());

    }

    @Test
    @DisplayName("createTaskInProject: Should return created task as a TaskResponse")
    public void createTaskInProject_HappyPath() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTitle("Title");
        Project project = new Project();
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setTitle("Title");
        Task task = new Task();

        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(project));
        when(taskMapper.fromDto(taskRequest)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(taskResponse);
        when(taskRepository.save(task)).thenReturn(task);

        TaskResponse result = projectService.createTaskInProject(1L, taskRequest);

        assertNotNull(result);
        assertEquals(taskResponse.getId(), result.getId());
        assertEquals(taskResponse.getTitle(), result.getTitle());

        verify(projectRepository, times(1)).findById(1L);
        verify(taskMapper, times(1)).toDto(task);
        verify(taskMapper, times(1)).fromDto(taskRequest);

    }

    @Test
    @DisplayName("createTaskInProject: Should throw EntityNotFoundException when project with the given id doesn't exist")
    public void createTaskInProject_Exception_ProjectDoesNotExist() {

        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> projectService.createTaskInProject(1L, new TaskRequest()));

        verify(taskMapper, never()).fromDto(any());
        verify(taskMapper, never()).toDto(any());
    }
}
