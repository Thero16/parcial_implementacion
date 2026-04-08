package com.nomolestar.peopleservice.e2e;

import com.nomolestar.peopleservice.dto.PersonCreateDTO;
import com.nomolestar.peopleservice.dto.PersonResponseDTO;
import com.nomolestar.peopleservice.dto.PersonUpdateDTO;
import com.nomolestar.peopleservice.enums.PersonRole;
import com.nomolestar.peopleservice.model.PersonEntity;
import com.nomolestar.peopleservice.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class PersonE2ETest {

    @Autowired private TestRestTemplate restTemplate;
    @Autowired private PersonRepository personRepository;

    @BeforeEach
    void setUp() {
        personRepository.deleteAll();
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
            doReturn(requestHeadersUriSpec).when(mockClient).get();
            doReturn(requestHeadersSpec).when(requestHeadersUriSpec).uri(anyString());
            doReturn(requestHeadersSpec).when(requestHeadersSpec).header(anyString(), any());
            doReturn(responseSpec).when(requestHeadersSpec).retrieve();
            when(responseSpec.toBodilessEntity())
                    .thenReturn(Mono.just(ResponseEntity.ok().build()));

            return mockBuilder;
        }
    }

    private PersonEntity seedPerson() {
        return personRepository.save(PersonEntity.builder()
                .caseId(10)
                .fullName("John Doe")
                .role(PersonRole.DETECTIVE)
                .age(35)
                .description("Test detective")
                .build());
    }

    @Test
    void getAll_returns200() {
        seedPerson();
        ResponseEntity<PersonResponseDTO[]> response = restTemplate.getForEntity(
                "/people", PersonResponseDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void getById_found_returns200() {
        PersonEntity saved = seedPerson();
        ResponseEntity<PersonResponseDTO> response = restTemplate.getForEntity(
                "/people/" + saved.getId(), PersonResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().fullName()).isEqualTo("John Doe");
    }

    @Test
    void getById_notFound_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/people/9999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getByCaseId_returns200() {
        seedPerson();
        ResponseEntity<PersonResponseDTO[]> response = restTemplate.getForEntity(
                "/people/case/10", PersonResponseDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void createPerson_returns201AndBody() {
        PersonCreateDTO dto = new PersonCreateDTO(10, "Jane Smith", PersonRole.SUSPECT, 28, "E2E suspect");

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "Bearer test-token");
        ResponseEntity<PersonResponseDTO> response = restTemplate.exchange(
                "/people", HttpMethod.POST,
                new HttpEntity<>(dto, headers), PersonResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().fullName()).isEqualTo("Jane Smith");
    }

    @Test
    void updatePerson_found_returns200() {
        PersonEntity saved = seedPerson();
        PersonUpdateDTO updateDTO = new PersonUpdateDTO("John Updated", PersonRole.WITNESS, 36, "Updated desc");

        ResponseEntity<PersonResponseDTO> response = restTemplate.exchange(
                "/people/" + saved.getId(), HttpMethod.PUT,
                new HttpEntity<>(updateDTO), PersonResponseDTO.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().fullName()).isEqualTo("John Updated");
    }

    @Test
    void deletePerson_found_returns204() {
        PersonEntity saved = seedPerson();
        ResponseEntity<Void> response = restTemplate.exchange(
                "/people/" + saved.getId(), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                "/people/" + saved.getId(), String.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
