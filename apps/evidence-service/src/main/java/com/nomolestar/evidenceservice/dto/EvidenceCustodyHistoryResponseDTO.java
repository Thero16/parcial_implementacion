package com.nomolestar.evidenceservice.dto;

import java.time.LocalDateTime;

public record EvidenceCustodyHistoryResponseDTO(

        Integer historyId,
        Integer evidenceId,
        String previousCustodian,
        String newCustodian,
        String reason,
        LocalDateTime transferredAt
) {
}
