package com.nomolestar.auditservice.repository;

import com.nomolestar.auditservice.model.AuditLogEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AuditLogRepositoryTest {

    @Autowired
    private AuditLogRepository auditLogRepository;

    private AuditLogEntity buildLog(String eventType) {
        return AuditLogEntity.builder()
                .eventType(eventType)
                .entityId("1")
                .description("Test description")
                .timestamp(LocalDateTime.now())
                .performedBy("system")
                .build();
    }

    @Test
    void save_andFindById_works() {
        AuditLogEntity saved = auditLogRepository.save(buildLog("case.created"));
        Optional<AuditLogEntity> found = auditLogRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getEventType()).isEqualTo("case.created");
    }

    @Test
    void findAll_returnsAllLogs() {
        auditLogRepository.save(buildLog("case.created"));
        auditLogRepository.save(buildLog("evidence.added"));
        List<AuditLogEntity> all = auditLogRepository.findAll();
        assertThat(all).hasSize(2);
    }

    @Test
    void findByEventType_returnsMatchingLogs() {
        auditLogRepository.save(buildLog("case.created"));
        auditLogRepository.save(buildLog("case.created"));
        auditLogRepository.save(buildLog("evidence.added"));

        List<AuditLogEntity> result = auditLogRepository.findByEventType("case.created");
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(l -> l.getEventType().equals("case.created"));
    }

    @Test
    void findByEventType_noMatch_returnsEmpty() {
        auditLogRepository.save(buildLog("case.created"));
        List<AuditLogEntity> result = auditLogRepository.findByEventType("nonexistent.event");
        assertThat(result).isEmpty();
    }

    @Test
    void deleteById_removesLog() {
        AuditLogEntity saved = auditLogRepository.save(buildLog("case.closed"));
        auditLogRepository.deleteById(saved.getId());
        assertThat(auditLogRepository.findById(saved.getId())).isEmpty();
    }
}
