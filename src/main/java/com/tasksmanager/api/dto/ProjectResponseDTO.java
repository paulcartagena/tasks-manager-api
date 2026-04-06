package com.tasksmanager.api.dto;

import com.tasksmanager.api.model.enums.ProjectRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProjectResponseDTO {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private List<MemberDTO> members;
    private ProjectRole projectRole;
}
