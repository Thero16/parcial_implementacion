package com.nomolestar.notificationservice.repository;

import com.nomolestar.notificationservice.enums.NotificationType;
import com.nomolestar.notificationservice.model.NotificationEntity;
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
class NotificationRepositoryTest {

    @Autowired
    private NotificationRepository notificationRepository;

    private NotificationEntity buildNotification(NotificationType type, Integer caseId, boolean read) {
        return NotificationEntity.builder()
                .message("Test notification message")
                .type(type)
                .caseId(caseId)
                .read(read)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void save_andFindById_works() {
        NotificationEntity saved = notificationRepository.save(
                buildNotification(NotificationType.EVIDENCE_ADDED, 1, false));
        Optional<NotificationEntity> found = notificationRepository.findById(saved.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getType()).isEqualTo(NotificationType.EVIDENCE_ADDED);
    }

    @Test
    void findAll_returnsAllNotifications() {
        notificationRepository.save(buildNotification(NotificationType.EVIDENCE_ADDED, 1, false));
        notificationRepository.save(buildNotification(NotificationType.TASK_ASSIGNED, 2, true));
        assertThat(notificationRepository.findAll()).hasSize(2);
    }

    @Test
    void findByRead_false_returnsOnlyUnread() {
        notificationRepository.save(buildNotification(NotificationType.EVIDENCE_ADDED, 1, false));
        notificationRepository.save(buildNotification(NotificationType.TASK_ASSIGNED, 2, true));
        notificationRepository.save(buildNotification(NotificationType.TASK_ASSIGNED, 3, false));

        List<NotificationEntity> unread = notificationRepository.findByRead(false);
        assertThat(unread).hasSize(2);
        assertThat(unread).allMatch(n -> !n.isRead());
    }

    @Test
    void findByRead_true_returnsOnlyRead() {
        notificationRepository.save(buildNotification(NotificationType.EVIDENCE_ADDED, 1, false));
        notificationRepository.save(buildNotification(NotificationType.TASK_ASSIGNED, 2, true));

        List<NotificationEntity> read = notificationRepository.findByRead(true);
        assertThat(read).hasSize(1);
        assertThat(read.get(0).isRead()).isTrue();
    }

    @Test
    void deleteById_removesNotification() {
        NotificationEntity saved = notificationRepository.save(
                buildNotification(NotificationType.EVIDENCE_ADDED, 1, false));
        notificationRepository.deleteById(saved.getId());
        assertThat(notificationRepository.findById(saved.getId())).isEmpty();
    }
}
