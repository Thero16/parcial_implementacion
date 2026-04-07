package com.nomolestar.auditservice.unit;

import com.nomolestar.auditservice.dto.AuditLogResponseDTO;
import com.nomolestar.auditservice.model.AuditLogEntity;
import com.nomolestar.auditservice.repository.AuditLogRepository;
import com.nomolestar.auditservice.service.AuditLogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuditLogService auditLogService;

    private AuditLogEntity sampleLog;

    @BeforeEach
    void setUp() {
        auditLogService = new AuditLogService(auditLogRepository);

        sampleLog = new AuditLogEntity();
        sampleLog.setId(1);
        sampleLog.setEventType("case.created");
        sampleLog.setEntityId("1");
        sampleLog.setDescription("Case created event received");
        sampleLog.setTimestamp(LocalDateTime.now());
        sampleLog.setPerformedBy("system");
    }

    @Test
    void findAll_returnsAllLogs() {
        when(auditLogRepository.findAll()).thenReturn(List.of(sampleLog));

        List<AuditLogResponseDTO> result = auditLogService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).eventType()).isEqualTo("case.created");
    }

    @Test
    void findAll_empty_returnsEmptyList() {
        when(auditLogRepository.findAll()).thenReturn(List.of());

        List<AuditLogResponseDTO> result = auditLogService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findByEventType_returnsMatchingLogs() {
        when(auditLogRepository.findByEventType("case.created")).thenReturn(List.of(sampleLog));

        List<AuditLogResponseDTO> result = auditLogService.findByEventType("case.created");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).eventType()).isEqualTo("case.created");
    }

    @Test
    void findByEventType_noMatch_returnsEmpty() {
        when(auditLogRepository.findByEventType("unknown.event")).thenReturn(List.of());

        List<AuditLogResponseDTO> result = auditLogService.findByEventType("unknown.event");

        assertThat(result).isEmpty();
    }

    @Test
    void save_persistsLog() {
        when(auditLogRepository.save(any())).thenReturn(sampleLog);

        AuditLogEntity result = auditLogService.save(sampleLog);

        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo("case.created");
        verify(auditLogRepository).save(sampleLog);
    }
}
