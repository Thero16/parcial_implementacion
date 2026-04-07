package com.nomolestar.workflowservice.dto;

import com.nomolestar.workflowservice.enums.TaskPriority;
import com.nomolestar.workflowservice.enums.TaskStatus;

import java.time.LocalDateTime;

public record TaskUpdateDTO(
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        String assignedTo,
        LocalDateTime dueDate
) {}
