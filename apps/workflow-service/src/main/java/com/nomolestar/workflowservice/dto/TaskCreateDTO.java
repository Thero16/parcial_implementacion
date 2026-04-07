package com.nomolestar.workflowservice.dto;

import com.nomolestar.workflowservice.enums.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskCreateDTO(
        @NotBlank String title,
        String description,
        @NotNull Integer caseId,
        @NotNull TaskPriority priority,
        String assignedTo,
        LocalDateTime dueDate
) {}
