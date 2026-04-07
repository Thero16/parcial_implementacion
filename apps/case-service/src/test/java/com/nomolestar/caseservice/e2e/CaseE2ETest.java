package com.nomolestar.caseservice.e2e;

import com.nomolestar.caseservice.dto.CaseCreateDTO;
import com.nomolestar.caseservice.dto.CaseResponseDTO;
import com.nomolestar.caseservice.dto.CaseUpdateDTO;
import com.nomolestar.caseservice.enums.CasePriority;
import com.nomolestar.caseservice.enums.InvestigationStatus;
import com.nomolestar.caseservice.messaging.CaseEventPublisher;
import com.nomolestar.caseservice.repository.CaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CaseE2ETest {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private CaseRepository caseRepository;
    @MockBean private CaseEventPublisher caseEventPublisher;

    @BeforeEach
    void setUp() {
        caseRepository.deleteAll();
        doNothing().when(caseEventPublisher).publishCaseCreated(any());
    }

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        @Primary
        @Order(1)
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(c -> c.disable())
                    .anonymous(a -> a.principal("testUser")
                            .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .authorizeHttpRequests(a -> a.anyRequest().permitAll());
            return http.build();
        }
    }

    private CaseCreateDTO sampleCreateDTO() {
        return new CaseCreateDTO(
                "E2E Test Case", "E2E Description",
                InvestigationStatus.OPEN, CasePriority.HIGH,
                "Detective E2E", LocalDateTime.now());
    }

    @Test
    void createCase_returns201AndBody() {
        ResponseEntity<CaseResponseDTO> response = restTemplate.postForEntity(
                "/cases", sampleCreateDTO(), CaseResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("E2E Test Case");
    }

    @Test
    void getAll_returns200WithData() {
        restTemplate.postForEntity("/cases", sampleCreateDTO(), CaseResponseDTO.class);
        ResponseEntity<CaseResponseDTO[]> response = restTemplate.getForEntity(
                "/cases", CaseResponseDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void getById_found_returns200() {
        CaseResponseDTO created = restTemplate.postForEntity(
                "/cases", sampleCreateDTO(), CaseResponseDTO.class).getBody();
        assertThat(created).isNotNull();

        ResponseEntity<CaseResponseDTO> response = restTemplate.getForEntity(
                "/cases/" + created.id(), CaseResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().id()).isEqualTo(created.id());
    }

    @Test
    void getById_notFound_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cases/9999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateCase_found_returns200() {
        CaseResponseDTO created = restTemplate.postForEntity(
                "/cases", sampleCreateDTO(), CaseResponseDTO.class).getBody();
        assertThat(created).isNotNull();

        CaseUpdateDTO updateDTO = new CaseUpdateDTO(
                "Updated Title", "Updated Desc",
                InvestigationStatus.IN_PROGRESS, CasePriority.MEDIUM, "Det Updated");
        ResponseEntity<CaseResponseDTO> response = restTemplate.exchange(
                "/cases/" + created.id(), HttpMethod.PUT,
                new HttpEntity<>(updateDTO), CaseResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().title()).isEqualTo("Updated Title");
    }

    @Test
    void deleteCase_found_returns204() {
        CaseResponseDTO created = restTemplate.postForEntity(
                "/cases", sampleCreateDTO(), CaseResponseDTO.class).getBody();
        assertThat(created).isNotNull();

        ResponseEntity<Void> response = restTemplate.exchange(
                "/cases/" + created.id(), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                "/cases/" + created.id(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
