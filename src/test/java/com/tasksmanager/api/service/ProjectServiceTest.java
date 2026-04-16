package com.tasksmanager.api.service;

import com.tasksmanager.api.dto.ProjectRequestDTO;
import com.tasksmanager.api.dto.ProjectResponseDTO;
import com.tasksmanager.api.exception.MemberAlreadyExistsException;
import com.tasksmanager.api.exception.ProjectHasTasksException;
import com.tasksmanager.api.model.Project;
import com.tasksmanager.api.model.User;
import com.tasksmanager.api.repository.ProjectRepository;
import com.tasksmanager.api.repository.TaskRepository;
import com.tasksmanager.api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AccessService accessService;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void shouldCreateProject() {
        // Arrange
        ProjectRequestDTO dto = new ProjectRequestDTO();
        dto.setName("My Project");
        dto.setDescription("Project test description");

        User currentUser = new User();
        currentUser.setId(2L);

        Project savedProject = new Project();
        savedProject.setId(1L);
        savedProject.setName(dto.getName());
        savedProject.setDescription(dto.getDescription());
        savedProject.setOwner(currentUser);

        when(projectRepository.save(any(Project.class)))
                .thenReturn(savedProject);

        // Act
        ProjectResponseDTO result = projectService.createProject(dto, currentUser);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getOwnerId());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void shouldAddMember() {
        // Arrange
        Long projectId = 1L;
        Long userId = 5L;

        User currentUser = new User();
        currentUser.setId(2L);

        User newMember = new User();
        newMember.setId(5L);

        Project project = new Project();
        project.setId(projectId);
        project.setOwner(currentUser);
        project.setMembers(new ArrayList<>());

        when(accessService.getProjectIfOwner(projectId, currentUser))
                .thenReturn(project);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(newMember));

        when(projectRepository.save(any(Project.class)))
                .thenReturn(project);

        // Act
        ProjectResponseDTO result = projectService.addMember(projectId, userId, currentUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getMembers().size());
        verify(projectRepository).save(project);
    }

    @Test
    void shouldFailIfMemberExists() {
        // Arrange
        Long projectId = 1L;
        Long userId = 5L;

        User currentUser = new User();
        currentUser.setId(2L);

        User existingMember = new User();
        existingMember.setId(userId);

        Project project = new Project();
        project.setId(projectId);
        project.setOwner(currentUser);
        project.setMembers(new ArrayList<>(List.of(existingMember)));

        when(accessService.getProjectIfOwner(projectId, currentUser))
                .thenReturn(project);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(existingMember));

        // Act & Assert
        assertThrows(MemberAlreadyExistsException.class, () -> {
            projectService.addMember(projectId, userId, currentUser);
        });
    }

    @Test
    void shouldFailWhenProjectHasTasks() {
        // Arrange
        Long projectId = 1L;

        User currentUser = new User();
        currentUser.setId(2L);

        Project project = new Project();
        project.setId(projectId);
        project.setOwner(currentUser);

        when(accessService.getProjectIfOwner(projectId, currentUser))
                .thenReturn(project);

        when(taskRepository.existsByProjectId(projectId))
                .thenReturn(true);

        // Act & Assert
        assertThrows(ProjectHasTasksException.class, () -> {
            projectService.deleteProject(projectId, currentUser);
        });

        verify(projectRepository, never()).delete(any());
    }
}
