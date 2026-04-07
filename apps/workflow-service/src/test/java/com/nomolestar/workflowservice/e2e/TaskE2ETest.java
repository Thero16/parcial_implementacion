package com.nomolestar.workflowservice.e2e;

import com.nomolestar.workflowservice.dto.TaskCreateDTO;
import com.nomolestar.workflowservice.dto.TaskResponseDTO;
import com.nomolestar.workflowservice.dto.TaskUpdateDTO;
import com.nomolestar.workflowservice.enums.TaskPriority;
import com.nomolestar.workflowservice.enums.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskE2ETest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        @Primary
        SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(c -> c.disable()).authorizeHttpRequests(a -> a.anyRequest().permitAll());
            return http.build();
        }
    }

    @Test
    void createReadUpdateDelete_lifecycle() {
        // Create
        TaskCreateDTO createDTO = new TaskCreateDTO("E2E Task", "E2E Description", 1, TaskPriority.MEDIUM, null, null);
        ResponseEntity<TaskResponseDTO> createResponse = restTemplate.postForEntity("/tasks", createDTO, TaskResponseDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        Integer id = createResponse.getBody().id();
        assertThat(createResponse.getBody().title()).isEqualTo("E2E Task");

        // Read
        ResponseEntity<TaskResponseDTO> getResponse = restTemplate.getForEntity("/tasks/" + id, TaskResponseDTO.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody().title()).isEqualTo("E2E Task");

        // Update
        TaskUpdateDTO updateDTO = new TaskUpdateDTO("Updated E2E Task", null, TaskStatus.IN_PROGRESS, null, null, null);
        ResponseEntity<TaskResponseDTO> updateResponse = restTemplate.exchange(
                "/tasks/" + id, HttpMethod.PUT, new HttpEntity<>(updateDTO), TaskResponseDTO.class);
        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(updateResponse.getBody().status()).isEqualTo(TaskStatus.IN_PROGRESS);

        // Delete
        restTemplate.delete("/tasks/" + id);
        ResponseEntity<String> getAfterDelete = restTemplate.getForEntity("/tasks/" + id, String.class);
        assertThat(getAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAll_returns200() {
        ResponseEntity<TaskResponseDTO[]> response = restTemplate.getForEntity("/tasks", TaskResponseDTO[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getById_notFound_returns404() {
        ResponseEntity<String> response = restTemplate.getForEntity("/tasks/99999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
