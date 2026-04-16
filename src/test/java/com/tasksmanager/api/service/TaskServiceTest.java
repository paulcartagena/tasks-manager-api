package com.tasksmanager.api.service;

import com.tasksmanager.api.dto.TaskRequestDTO;
import com.tasksmanager.api.dto.TaskResponseDTO;
import com.tasksmanager.api.exception.TaskNotFoundException;
import com.tasksmanager.api.model.Project;
import com.tasksmanager.api.model.Task;
import com.tasksmanager.api.model.User;
import com.tasksmanager.api.model.enums.TaskStatus;
import com.tasksmanager.api.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private AccessService accessService;

    @InjectMocks
    private TaskService taskService;

    @Test
    void shouldCreateTask() {
        // Arrange
        Long projectId = 1L;

        TaskRequestDTO dto = new TaskRequestDTO();
        dto.setName("My Task");
        dto.setDescription("Task test description");
        dto.setDueDate(LocalDate.now());

        User currentUser = new User();
        currentUser.setId(1L);

        Project project = new Project();
        project.setId(projectId);

        Task savedTask = new Task();
        savedTask.setId(10L);
        savedTask.setProject(project);
        savedTask.setName(dto.getName());
        savedTask.setDescription(dto.getDescription());
        savedTask.setStatus(TaskStatus.TODO);
        savedTask.setDueDate(dto.getDueDate());

        when(accessService.getProjectIfMember(projectId, currentUser))
                .thenReturn(project);
        when(taskRepository.save(any(Task.class)))
                .thenReturn(savedTask);

        // Act
        TaskResponseDTO result = taskService.createTask(projectId, dto, currentUser);

        // Assert
        assertNotNull(result);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldFailWhenTaskNotFound() {
        // Arrange
        Long projectId = 1L;
        Long taskId = 10L;

        User currentUser = new User();
        currentUser.setId(1L);

        Project project = new Project();
        project.setId(projectId);

        when(accessService.getProjectIfMember(projectId, currentUser))
                .thenReturn(project);

        when(taskRepository.findByIdAndProjectId(taskId, projectId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.findTaskById(projectId, taskId, currentUser);
        });
    }

    @Test
    void shouldDeleteTask() {
        // Arrange
        Long projectId = 1L;
        Long taskId = 10L;

        User currentUser = new User();
        currentUser.setId(2L);

        Project project = new Project();
        project.setId(projectId);

        Task task = new Task();
        task.setId(taskId);
        task.setProject(project);

        when(accessService.getProjectIfMember(projectId, currentUser))
                .thenReturn(project);

        when(taskRepository.findByIdAndProjectId(taskId, projectId))
                .thenReturn(Optional.of(task));

        // Act
        taskService.deleteTask(projectId, taskId, currentUser);

        // Assert
        verify(taskRepository).delete(task);
    }
}
