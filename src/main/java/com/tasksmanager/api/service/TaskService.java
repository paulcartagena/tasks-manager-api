package com.tasksmanager.api.service;

import com.tasksmanager.api.dto.TaskRequestDTO;
import com.tasksmanager.api.dto.TaskResponseDTO;
import com.tasksmanager.api.exception.TaskNotFoundException;
import com.tasksmanager.api.model.Project;
import com.tasksmanager.api.model.Task;
import com.tasksmanager.api.model.User;
import com.tasksmanager.api.model.enums.TaskStatus;
import com.tasksmanager.api.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final AccessService accessService;

    public TaskService(TaskRepository taskRepository,
                       AccessService accessService) {
        this.taskRepository = taskRepository;
        this.accessService = accessService;
    }

    @Transactional(readOnly = true)
    public List<TaskResponseDTO> findAllTasks(Long projectId, User currentUser) {
        Project project = accessService.getProjectIfMember(projectId, currentUser);

        return taskRepository.findAllByProjectId(project.getId())
                .stream().map(this::buildTaskResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO findTaskById(Long projectId, Long taskId, User currentUser) {
        accessService.getProjectIfMember(projectId, currentUser);

        Task task = getTaskByIdAndProjectId(taskId, projectId);

        return buildTaskResponse(task);
    }

    public TaskResponseDTO createTask(Long projectId, TaskRequestDTO taskRequestDTO, User currentUser) {
        Project project = accessService.getProjectIfMember(projectId,currentUser);

        Task task = new Task();
        task.setProject(project);
        task.setName(taskRequestDTO.getName());
        task.setDescription(taskRequestDTO.getDescription());
        task.setStatus(TaskStatus.TODO);
        task.setDueDate(taskRequestDTO.getDueDate());
        task.setFilePath(taskRequestDTO.getFilePath());
        Task savedTask = taskRepository.save(task);

        return buildTaskResponse(savedTask);
    }

    public TaskResponseDTO updateTask(Long projectId, Long taskId, TaskRequestDTO taskRequestDTO, User currentUser) {
        accessService.getProjectIfMember(projectId, currentUser);

        Task task = getTaskByIdAndProjectId(taskId, projectId);

        task.setName(taskRequestDTO.getName());
        task.setDescription(taskRequestDTO.getDescription());
        task.setStatus(taskRequestDTO.getStatus());
        task.setDueDate(taskRequestDTO.getDueDate());
        task.setFilePath(taskRequestDTO.getFilePath());
        taskRepository.save(task);

        return buildTaskResponse(task);
    }

    public void deleteTask(Long projectId, Long taskId, User currentUser) {
        accessService.getProjectIfMember(projectId, currentUser);

        Task task = getTaskByIdAndProjectId(taskId, projectId);

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

    private Task getTaskByIdAndProjectId(Long taskId, Long projectId) {
        return taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new TaskNotFoundException("Task not found."));
    }
}
