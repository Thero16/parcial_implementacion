package com.nomolestar.workflowservice.dto;

import com.nomolestar.workflowservice.enums.TaskPriority;
import com.nomolestar.workflowservice.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Integer id,
        String title,
        String description,
        Integer caseId,
        TaskStatus status,
        TaskPriority priority,
        String assignedTo,
        LocalDateTime dueDate,
        LocalDateTime createdAt
) {}
