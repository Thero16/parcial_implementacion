package com.nomolestar.auditservice.controller;

import com.nomolestar.auditservice.dto.AuditLogResponseDTO;
import com.nomolestar.auditservice.service.AuditLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<AuditLogResponseDTO> findAll(@RequestParam(required = false) String eventType) {
        if (eventType != null) {
            return auditLogService.findByEventType(eventType);
        }
        return auditLogService.findAll();
    }
}
