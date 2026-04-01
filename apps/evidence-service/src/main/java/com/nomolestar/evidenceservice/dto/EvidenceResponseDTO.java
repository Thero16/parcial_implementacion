package com.nomolestar.evidenceservice.dto;

import com.nomolestar.evidenceservice.enums.CustodyStatus;
import com.nomolestar.evidenceservice.enums.EvidenceType;

import java.time.LocalDateTime;

public record EvidenceResponseDTO(

        Integer evidenceId,
        Integer caseId,
        EvidenceType evidenceType,
        String description,
        String locationFound,
        LocalDateTime dateCollected,
        String collectedBy,
        String fileUrl,
        CustodyStatus custodyStatus,
        String currentCustodian
) {
}
