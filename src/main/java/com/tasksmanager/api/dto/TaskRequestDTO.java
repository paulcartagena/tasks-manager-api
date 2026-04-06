package com.tasksmanager.api.dto;

import com.tasksmanager.api.model.enums.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TaskRequestDTO {
    @NotBlank(message = "Name is required.")
    private String name;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private String filePath;
}
