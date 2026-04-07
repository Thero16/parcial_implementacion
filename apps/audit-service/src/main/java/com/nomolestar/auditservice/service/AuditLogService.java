package com.nomolestar.auditservice.service;

import com.nomolestar.auditservice.dto.AuditLogResponseDTO;
import com.nomolestar.auditservice.mapper.AuditLogMapper;
import com.nomolestar.auditservice.model.AuditLogEntity;
import com.nomolestar.auditservice.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLogResponseDTO> findAll() {
        return auditLogRepository.findAll()
                .stream()
                .map(AuditLogMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<AuditLogResponseDTO> findByEventType(String eventType) {
        return auditLogRepository.findByEventType(eventType)
                .stream()
                .map(AuditLogMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    public AuditLogEntity save(AuditLogEntity entity) {
        return auditLogRepository.save(entity);
    }
}
