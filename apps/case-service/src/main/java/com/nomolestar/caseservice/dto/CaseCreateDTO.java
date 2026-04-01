package com.nomolestar.caseservice.dto;

import com.nomolestar.caseservice.enums.CasePriority;
import com.nomolestar.caseservice.enums.InvestigationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CaseCreateDTO(

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Description is required")
        String description,

        @NotNull(message = "Status is required")
        InvestigationStatus status,

        @NotNull(message = "Priority is required")
        CasePriority priority,

        @NotBlank(message = "Assigned detective is required")
        String assignedDetective,

        @NotNull(message = "Created date is required")
        LocalDateTime createdAt

) {
}
