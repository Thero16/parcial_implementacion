package com.nomolestar.caseservice.dto;

import com.nomolestar.caseservice.enums.CasePriority;
import com.nomolestar.caseservice.enums.InvestigationStatus;

import java.time.LocalDateTime;

public record CaseResponseDTO(

        Integer id,
        String title,
        String description,
        InvestigationStatus status,
        CasePriority priority,
        String assignedDetective,
        LocalDateTime createdAt

) {
}
