package com.tasksmanager.api.service;

import com.tasksmanager.api.dto.MemberDTO;
import com.tasksmanager.api.dto.ProjectRequestDTO;
import com.tasksmanager.api.dto.ProjectResponseDTO;
import com.tasksmanager.api.exception.MemberAlreadyExistsException;
import com.tasksmanager.api.exception.ProjectNotFoundException;
import com.tasksmanager.api.exception.UserNotFoundException;
import com.tasksmanager.api.model.Project;
import com.tasksmanager.api.model.User;
import com.tasksmanager.api.model.enums.ProjectRole;
import com.tasksmanager.api.repository.ProjectRepository;
import com.tasksmanager.api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> findAllProjects(User currentUser) {
        return projectRepository.findByOwnerOrMembersContaining(currentUser, currentUser)
                .stream().map(project -> buildResponse(project, currentUser)).toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponseDTO findProjectById(Long projectId, User currenUser) {
        Project project = getProjectById(projectId);
        return buildResponse(project, currenUser);
    }

    public ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO, User owner) {
        Project project = new Project();
        project.setOwner(owner);
        project.setName(projectRequestDTO.getName());
        project.setDescription(projectRequestDTO.getDescription());

        projectRepository.save(project);
        return buildResponse(project, owner);
    }

    public ProjectResponseDTO updateProject(Long projectId, ProjectRequestDTO projectRequestDTO, User currentUser) {
        Project project = getProjectById(projectId);

        project.setName(projectRequestDTO.getName());
        project.setDescription(projectRequestDTO.getDescription());

        projectRepository.save(project);
        return buildResponse(project, currentUser);
    }

    public void deleteProject(Long idProject) {
        Project project = getProjectById(idProject);
        projectRepository.delete(project);
    }

    public ProjectResponseDTO addMember(Long projectId, Long userId, User currentUser) {
        Project project = getProjectById(projectId);
        User user = getUserById(userId);

        if (project.getMembers().contains(user)) {
            throw new MemberAlreadyExistsException("Member already exists.");
        }

        project.getMembers().add(user);
        Project savedProject = projectRepository.save(project);
        return buildResponse(savedProject, currentUser);
    }

    public void removeMember(Long projectId, Long userId) {
        Project project = getProjectById(projectId);
        User user = getUserById(userId);

        project.getMembers().remove(user);
        projectRepository.save(project);
    }

    private ProjectResponseDTO buildResponse(Project project, User currentUser) {
       ProjectRole role = project.getOwner().getId().equals(currentUser.getId())
               ? ProjectRole.OWNER
               : ProjectRole.MEMBER;

        return new ProjectResponseDTO(
                project.getId(),
                project.getOwner().getId(),
                project.getName(),
                project.getDescription(),
                project.getMembers().stream()
                        .map(user -> new MemberDTO(user.getId(), user.getName(), user.getEmail()))
                        .toList(),
                role
        );
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() ->  new ProjectNotFoundException("Project not found."));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
    }
}
