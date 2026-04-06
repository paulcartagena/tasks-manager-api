package com.tasksmanager.api.controller;

import com.tasksmanager.api.dto.MemberDTO;
import com.tasksmanager.api.dto.ProjectRequestDTO;
import com.tasksmanager.api.dto.ProjectResponseDTO;
import com.tasksmanager.api.model.User;
import com.tasksmanager.api.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/projects")
@Tag(name = "Projects", description = "Project management endpoints")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    @Operation(summary = "Get all projects", description = "Returns all projects where the user is owner or member")
    public List<ProjectResponseDTO> getAllProjects() {
        User currentUser = getCurrentUser();
        return projectService.findAllProjects(currentUser);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "Get project by id", description = "Returns a project by id")
    public ProjectResponseDTO getProject(@PathVariable Long projectId) {
        User currentUser = getCurrentUser();
        return projectService.findProjectById(projectId, currentUser);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new project", description = "Creates a new project with the authenticated user as owner")
    public ProjectResponseDTO createProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        User currentUser = getCurrentUser();
        return projectService.createProject(projectRequestDTO, currentUser);
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "Update project", description = "Updates an existing project by id")
    public ProjectResponseDTO updateProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO, @PathVariable Long projectId) {
        User currentUser = getCurrentUser();
        return projectService.updateProject(projectId, projectRequestDTO, currentUser);
    }

    @DeleteMapping("/{projectId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete project", description = "Deletes a project by id")
    public void deleteProject(@PathVariable Long projectId) {
        projectService.deleteProject(projectId);
    }

    @PostMapping("/{projectId}/members/{userId}")
    @Operation(summary = "Add member to project", description = "Adds a user as a member of the project")
    public ProjectResponseDTO addMember(@PathVariable Long projectId, @PathVariable Long userId) {
        User currentUser = getCurrentUser();

        return projectService.addMember(projectId, userId, currentUser);
    }

    @DeleteMapping("/{projectId}/members/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove member from project", description = "Removes a user from project")
    public void removeMember(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.removeMember(projectId, userId);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
