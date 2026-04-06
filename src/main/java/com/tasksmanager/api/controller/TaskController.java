package com.tasksmanager.api.controller;

import com.tasksmanager.api.dto.TaskRequestDTO;
import com.tasksmanager.api.dto.TaskResponseDTO;
import com.tasksmanager.api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}/tasks")
@Tag(name = "Tasks", description = "Tasks management endpoints")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Get all tasks", description = "Returns all tasks by project id")
    public List<TaskResponseDTO> getAllTasks(@PathVariable Long projectId) {
        return taskService.findAllTasks(projectId);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Get task by id", description = "Returns a task by id, needs project id")
    public TaskResponseDTO getTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        return taskService.findTaskById(projectId, taskId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new task", description = "Creates a new task into a project")
    public TaskResponseDTO createTask(@PathVariable Long projectId, @Valid @RequestBody TaskRequestDTO taskRequestDTO) {
        return taskService.createTask(projectId, taskRequestDTO);
    }

    @PutMapping("/{taskId}")
    @Operation(summary = "Update task", description = "Updates a existing task by task id")
    public TaskResponseDTO updateTask(@PathVariable Long projectId, @PathVariable Long taskId, @Valid @RequestBody TaskRequestDTO taskRequestDTO) {
        return taskService.updateTask(projectId, taskId, taskRequestDTO);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete task", description = "Deletes a task by task id")
    public void deleteTask(@PathVariable Long projectId, @PathVariable Long taskId) {
        taskService.deleteTask(projectId, taskId);
    }
}

