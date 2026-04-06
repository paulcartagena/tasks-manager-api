package com.tasksmanager.api.service;

import com.tasksmanager.api.dto.TaskRequestDTO;
import com.tasksmanager.api.dto.TaskResponseDTO;
import com.tasksmanager.api.exception.ProjectNotFoundException;
import com.tasksmanager.api.exception.TaskNotFoundException;
import com.tasksmanager.api.model.Project;
import com.tasksmanager.api.model.Task;
import com.tasksmanager.api.model.enums.TaskStatus;
import com.tasksmanager.api.repository.ProjectRepository;
import com.tasksmanager.api.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> findAllTasks(Long projectId) {
        Project project = getProjectById(projectId);
        return taskRepository.findByProject(project)
                .stream().map(this::buildTaskResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO findTaskById(Long projectId, Long taskId) {
        Task task = getTaskById(taskId);
        validateTaskBelongsToProject(task, projectId);
        return buildTaskResponse(task);
    }

    public TaskResponseDTO createTask(Long projectId, TaskRequestDTO taskRequestDTO) {
        Task task = new Task();
        task.setProject(getProjectById(projectId));
        task.setName(taskRequestDTO.getName());
        task.setDescription(taskRequestDTO.getDescription());
        task.setStatus(TaskStatus.TODO);
        task.setDueDate(taskRequestDTO.getDueDate());
        task.setFilePath(taskRequestDTO.getFilePath());

        Task savedTask = taskRepository.save(task);
        return buildTaskResponse(savedTask);
    }

    public TaskResponseDTO updateTask(Long projectId, Long taskId, TaskRequestDTO taskRequestDTO) {
        Task task = getTaskById(taskId);
        validateTaskBelongsToProject(task, projectId);

        task.setName(taskRequestDTO.getName());
        task.setDescription(taskRequestDTO.getDescription());
        task.setStatus(taskRequestDTO.getStatus());
        task.setDueDate(taskRequestDTO.getDueDate());
        task.setFilePath(taskRequestDTO.getFilePath());

        taskRepository.save(task);
        return buildTaskResponse(task);
    }

    public void deleteTask(Long projectId, Long taskId) {
        Task task = getTaskById(taskId);
        validateTaskBelongsToProject(task, projectId);
        taskRepository.delete(task);
    }

    private TaskResponseDTO buildTaskResponse(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getProject().getId(),
                task.getName(),
                task.getDescription(),
                task.getStatus(),
                task.getDueDate(),
                task.getFilePath());
    }

    private Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found."));
    }

    private Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    private void validateTaskBelongsToProject(Task task, Long projectId) {
        if (!task.getProject().getId().equals(projectId)) {
            throw new TaskNotFoundException("Task not found.");
        }
    }
}
