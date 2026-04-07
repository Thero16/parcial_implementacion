package com.nomolestar.auditservice.dto;

import java.time.LocalDateTime;

public record AuditLogResponseDTO(
        Integer id,
        String eventType,
        String entityId,
        String description,
        LocalDateTime timestamp,
        String performedBy
) {}
