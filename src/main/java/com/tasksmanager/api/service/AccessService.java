package com.tasksmanager.api.service;

import com.tasksmanager.api.exception.ProjectNotFoundException;
import com.tasksmanager.api.exception.UnauthorizedException;
import com.tasksmanager.api.model.Project;
import com.tasksmanager.api.model.User;
import com.tasksmanager.api.repository.ProjectRepository;
import org.springframework.stereotype.Service;

@Service
public class AccessService {

    private final ProjectRepository projectRepository;

    public AccessService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Verifies that the user is the project owner or a member.
     * Throws unauthorized if access is denied.
     */
   public Project getProjectIfMember(Long projectId, User currentUser) {
        return projectRepository
                .findByIdAndOwnerOrMember(projectId, currentUser)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found"));
   }

    /**
     * Verifies that the user is the project owner.
     * Throws unauthorized if access is denied.
     */
   public Project getProjectIfOwner(long projectId, User currentUser) {
        return projectRepository
                .findByIdAndOwner(projectId, currentUser)
                .orElseThrow(() -> new UnauthorizedException("Access denied"));
   }
}
