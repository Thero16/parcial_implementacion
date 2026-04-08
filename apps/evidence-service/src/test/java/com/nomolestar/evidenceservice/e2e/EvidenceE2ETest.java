package com.nomolestar.evidenceservice.e2e;

import com.nomolestar.evidenceservice.dto.EvidenceCreateDTO;
import com.nomolestar.evidenceservice.dto.EvidenceResponseDTO;
import com.nomolestar.evidenceservice.dto.EvidenceUpdateDTO;
import com.nomolestar.evidenceservice.enums.CustodyStatus;
import com.nomolestar.evidenceservice.enums.EvidenceType;
import com.nomolestar.evidenceservice.messaging.EvidenceEventPublisher;
import com.nomolestar.evidenceservice.model.EvidenceEntity;
import com.nomolestar.evidenceservice.repository.EvidenceCustodyHistoryRepository;
import com.nomolestar.evidenceservice.repository.EvidenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EvidenceE2ETest {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private EvidenceRepository evidenceRepository;
    @Autowired private EvidenceCustodyHistoryRepository custodyHistoryRepository;
    @MockBean private EvidenceEventPublisher evidenceEventPublisher;

    @BeforeEach
    void setUp() {
        custodyHistoryRepository.deleteAll();
        evidenceRepository.deleteAll();
        doNothing().when(evidenceEventPublisher).publishEvidenceAdded(any());
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

    @TestConfiguration
    static class TestWebClientConfig {
        @Bean
        @Primary
        @SuppressWarnings("unchecked")
        WebClient.Builder webClientBuilder() {
            WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec =
                    mock(WebClient.RequestHeadersUriSpec.class);
            WebClient.RequestHeadersSpec<?> requestHeadersSpec =
                    mock(WebClient.RequestHeadersSpec.class);
            WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
            WebClient mockClient = mock(WebClient.class);
            WebClient.Builder mockBuilder = mock(WebClient.Builder.class);

            when(mockBuilder.build()).thenReturn(mockClient);
            // use doReturn(...) to avoid Mockito generics mismatch with WebClient fluent interfaces
            doReturn(requestHeadersUriSpec).when(mockClient).get();
            doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
            doReturn(requestHeadersSpec).when(requestHeadersSpec).header(anyString(), any());
            doReturn(responseSpec).when(requestHeadersSpec).retrieve();
            when(responseSpec.toBodilessEntity())
                    .thenReturn(Mono.just(ResponseEntity.ok().build()));

            return mockBuilder;
        }
    }

    private EvidenceEntity seedEvidence() {
        return evidenceRepository.save(EvidenceEntity.builder()
                .caseId(10)
                .evidenceType(EvidenceType.PHOTO)
                .description("Test photo evidence")
                .locationFound("Crime scene A")
                .dateCollected(LocalDateTime.now())
                .collectedBy("Officer Smith")
                .custodyStatus(CustodyStatus.STORED)
                .currentCustodian("Lab A")
                .build());
    }

    @Test
    void getAll_returns200() {
        seedEvidence();
        ResponseEntity<EvidenceResponseDTO[]> response = restTemplate.getForEntity(
                "/evidences", EvidenceResponseDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void getById_found_returns200() {
        EvidenceEntity saved = seedEvidence();
        ResponseEntity<EvidenceResponseDTO> response = restTemplate.getForEntity(
                "/evidences/" + saved.getEvidenceId(), EvidenceResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().evidenceId()).isEqualTo(saved.getEvidenceId());
    }

    @Test
    void getById_notFound_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/evidences/9999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getByCaseId_returns200() {
        seedEvidence();
        ResponseEntity<EvidenceResponseDTO[]> response = restTemplate.getForEntity(
                "/evidences/case/10", EvidenceResponseDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void createEvidence_returns201AndBody() {
        EvidenceCreateDTO dto = new EvidenceCreateDTO(
                10, EvidenceType.DOCUMENT, "E2E document evidence",
                "Location B", LocalDateTime.now(), "Officer Jones",
                null, CustodyStatus.COLLECTED, "Detective HQ");

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer test-token");
        ResponseEntity<EvidenceResponseDTO> response = restTemplate.exchange(
                "/evidences", HttpMethod.POST,
                new HttpEntity<>(dto, headers), EvidenceResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().description()).isEqualTo("E2E document evidence");
    }

    @Test
    void updateEvidence_found_returns200() {
        EvidenceEntity saved = seedEvidence();
        EvidenceUpdateDTO updateDTO = new EvidenceUpdateDTO(
                EvidenceType.PHOTO, "Updated description", "New location",
                LocalDateTime.now(), "Officer Updated", null,
                CustodyStatus.IN_ANALYSIS, "Lab B", "Transfer reason");

        ResponseEntity<EvidenceResponseDTO> response = restTemplate.exchange(
                "/evidences/" + saved.getEvidenceId(), HttpMethod.PUT,
                new HttpEntity<>(updateDTO), EvidenceResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void deleteEvidence_found_returns204() {
        EvidenceEntity saved = seedEvidence();
        ResponseEntity<Void> response = restTemplate.exchange(
                "/evidences/" + saved.getEvidenceId(), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                "/evidences/" + saved.getEvidenceId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
