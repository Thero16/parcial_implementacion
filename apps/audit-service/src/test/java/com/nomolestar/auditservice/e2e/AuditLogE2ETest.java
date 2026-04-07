package com.nomolestar.auditservice.e2e;

import com.nomolestar.auditservice.dto.AuditLogResponseDTO;
import com.nomolestar.auditservice.model.AuditLogEntity;
import com.nomolestar.auditservice.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuditLogE2ETest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        @Primary
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(c -> c.disable()).authorizeHttpRequests(a -> a.anyRequest().permitAll());
            return http.build();
        }
    }

    @BeforeEach
    void setUp() {
        auditLogRepository.deleteAll();
    }

    @Test
    void getAll_empty_returns200() {
        ResponseEntity<AuditLogResponseDTO[]> response = restTemplate.getForEntity("/audit-logs", AuditLogResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getAll_withData_returnsSavedLogs() {
        AuditLogEntity log = new AuditLogEntity();
        log.setEventType("case.created");
        log.setEntityId("1");
        log.setDescription("Test audit log");
        log.setTimestamp(LocalDateTime.now());
        log.setPerformedBy("system");
        auditLogRepository.save(log);

        ResponseEntity<AuditLogResponseDTO[]> response = restTemplate.getForEntity("/audit-logs", AuditLogResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].eventType()).isEqualTo("case.created");
    }

    @Test
    void getByEventType_filters_correctly() {
        AuditLogEntity log1 = new AuditLogEntity();
        log1.setEventType("case.created");
        log1.setDescription("Case event");
        log1.setTimestamp(LocalDateTime.now());
        log1.setPerformedBy("system");
        auditLogRepository.save(log1);

        AuditLogEntity log2 = new AuditLogEntity();
        log2.setEventType("evidence.added");
        log2.setDescription("Evidence event");
        log2.setTimestamp(LocalDateTime.now());
        log2.setPerformedBy("system");
        auditLogRepository.save(log2);

        ResponseEntity<AuditLogResponseDTO[]> response = restTemplate.getForEntity(
                "/audit-logs?eventType=case.created", AuditLogResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].eventType()).isEqualTo("case.created");
    }
}
