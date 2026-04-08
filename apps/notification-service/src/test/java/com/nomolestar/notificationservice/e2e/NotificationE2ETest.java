package com.nomolestar.notificationservice.e2e;

import com.nomolestar.notificationservice.dto.NotificationResponseDTO;
import com.nomolestar.notificationservice.enums.NotificationType;
import com.nomolestar.notificationservice.model.NotificationEntity;
import com.nomolestar.notificationservice.repository.NotificationRepository;
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
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class NotificationE2ETest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

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

    @BeforeEach
    void setUp() {
        notificationRepository.deleteAll();
    }

    @Test
    void getAll_empty_returns200() {
        ResponseEntity<NotificationResponseDTO[]> response = restTemplate.getForEntity(
                "/notifications", NotificationResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getUnread_returnsOnlyUnread() {
        NotificationEntity unread = new NotificationEntity();
        unread.setMessage("Unread notification");
        unread.setType(NotificationType.EVIDENCE_ADDED);
        unread.setCaseId(1);
        unread.setRead(false);
        unread.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(unread);

        NotificationEntity read = new NotificationEntity();
        read.setMessage("Read notification");
        read.setType(NotificationType.TASK_ASSIGNED);
        read.setCaseId(2);
        read.setRead(true);
        read.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(read);

        ResponseEntity<NotificationResponseDTO[]> response = restTemplate.getForEntity(
                "/notifications/unread", NotificationResponseDTO[].class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody()[0].read()).isFalse();
    }

    @Test
    void markAsRead_updatesNotification() {
        NotificationEntity notification = new NotificationEntity();
        notification.setMessage("Task assigned to detective");
        notification.setType(NotificationType.TASK_ASSIGNED);
        notification.setCaseId(1);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        NotificationEntity saved = notificationRepository.save(notification);

        ResponseEntity<NotificationResponseDTO> response = restTemplate.exchange(
                "/notifications/" + saved.getId() + "/read",
                HttpMethod.PUT,
                null,
                NotificationResponseDTO.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().read()).isTrue();
    }

    @Test
    void markAsRead_notFound_returns404() {
        ResponseEntity<String> response = restTemplate.exchange(
                "/notifications/99999/read",
                HttpMethod.PUT,
                null,
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
