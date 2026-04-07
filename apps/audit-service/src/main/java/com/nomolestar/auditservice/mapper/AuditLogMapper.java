package com.nomolestar.auditservice.mapper;

import com.nomolestar.auditservice.dto.AuditLogResponseDTO;
import com.nomolestar.auditservice.model.AuditLogEntity;

public class AuditLogMapper {

    private AuditLogMapper() {}

    public static AuditLogResponseDTO toResponseDTO(AuditLogEntity entity) {
        return new AuditLogResponseDTO(
                entity.getId(),
                entity.getEventType(),
                entity.getEntityId(),
                entity.getDescription(),
                entity.getTimestamp(),
                entity.getPerformedBy()
        );
    }
}
