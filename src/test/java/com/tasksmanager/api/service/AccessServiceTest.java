package com.tasksmanager.api.service;

import com.tasksmanager.api.exception.ProjectNotFoundException;
import com.tasksmanager.api.model.Project;
import com.tasksmanager.api.model.User;
import com.tasksmanager.api.repository.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccessServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private AccessService accessService;

    @Test
    void shouldReturnProjectIfUserIsMember() {
        // Arrange
        Long projectId = 1L;

        User currentUser = new User();
        currentUser.setId(2L);

        Project project = new Project();
        project.setId(projectId);

        when(projectRepository.findByIdAndOwnerOrMember(projectId, currentUser))
                .thenReturn(Optional.of(project));

        // Act
        Project result = accessService.getProjectIfMember(projectId, currentUser);

        // Assert
        assertNotNull(result);
        assertEquals(projectId, result.getId());
        verify(projectRepository).findByIdAndOwnerOrMember(projectId, currentUser);
    }

    @Test
    void shouldThrowIfUserNotMember() {
        // Arrange
        Long projectId = 1L;

        User currentUser = new User();
        currentUser.setId(2L);

        when(projectRepository.findByIdAndOwnerOrMember(projectId, currentUser))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProjectNotFoundException.class, () -> {
            accessService.getProjectIfMember(projectId, currentUser);
        });
    }
}
