package com.tasksmanager.api.dto;

import com.tasksmanager.api.model.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class TaskResponseDTO {
    private Long id;
    private Long projectId;
    private String name;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    private String filePath;
}
