package com.nomolestar.notificationservice.unit;

import com.nomolestar.notificationservice.dto.NotificationResponseDTO;
import com.nomolestar.notificationservice.enums.NotificationType;
import com.nomolestar.notificationservice.exceptions.ResourceNotFoundException;
import com.nomolestar.notificationservice.model.NotificationEntity;
import com.nomolestar.notificationservice.repository.NotificationRepository;
import com.nomolestar.notificationservice.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    private NotificationService notificationService;

    private NotificationEntity sampleNotification;

    @BeforeEach
    void setUp() {
        notificationService = new NotificationService(notificationRepository);

        sampleNotification = new NotificationEntity();
        sampleNotification.setId(1);
        sampleNotification.setMessage("New evidence added to case 1");
        sampleNotification.setType(NotificationType.EVIDENCE_ADDED);
        sampleNotification.setCaseId(1);
        sampleNotification.setRead(false);
        sampleNotification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void findAll_returnsAllNotifications() {
        when(notificationRepository.findAll()).thenReturn(List.of(sampleNotification));

        List<NotificationResponseDTO> result = notificationService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).message()).isEqualTo("New evidence added to case 1");
    }

    @Test
    void findAll_empty_returnsEmptyList() {
        when(notificationRepository.findAll()).thenReturn(List.of());

        List<NotificationResponseDTO> result = notificationService.findAll();

        assertThat(result).isEmpty();
    }

    @Test
    void findUnread_returnsUnreadOnly() {
        when(notificationRepository.findByRead(false)).thenReturn(List.of(sampleNotification));

        List<NotificationResponseDTO> result = notificationService.findUnread();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).read()).isFalse();
    }

    @Test
    void markAsRead_found_marksAsRead() {
        when(notificationRepository.findById(1)).thenReturn(Optional.of(sampleNotification));
        when(notificationRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        NotificationResponseDTO result = notificationService.markAsRead(1);

        assertThat(result.read()).isTrue();
        verify(notificationRepository).save(any());
    }

    @Test
    void markAsRead_notFound_throwsException() {
        when(notificationRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> notificationService.markAsRead(99))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void save_persistsNotification() {
        when(notificationRepository.save(any())).thenReturn(sampleNotification);

        NotificationEntity result = notificationService.save(sampleNotification);

        assertThat(result).isNotNull();
        assertThat(result.getMessage()).isEqualTo("New evidence added to case 1");
        verify(notificationRepository).save(sampleNotification);
    }
}
