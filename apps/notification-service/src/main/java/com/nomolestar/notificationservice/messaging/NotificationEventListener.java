package com.nomolestar.notificationservice.messaging;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nomolestar.notificationservice.config.RabbitMQConfig;
import com.nomolestar.notificationservice.enums.NotificationType;
import com.nomolestar.notificationservice.model.NotificationEntity;
import com.nomolestar.notificationservice.service.NotificationService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleEvent(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        NotificationEntity notification = new NotificationEntity();
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        try {
            Map<String, Object> converted = objectMapper.readValue(
                new String(message.getBody()), new TypeReference<Map<String, Object>>() {});

            Object rawCaseId = converted.get("caseId");
            Integer caseId = rawCaseId instanceof Number n ? n.intValue() : null;

            if ("evidence.added".equals(routingKey)) {
                notification.setType(NotificationType.EVIDENCE_ADDED);
                notification.setCaseId(caseId);
                notification.setMessage("New evidence added to case " + caseId + " — type: " + converted.get("evidenceType") + ", collected by: " + converted.get("collectedBy"));
            } else if ("task.assigned".equals(routingKey)) {
                notification.setType(NotificationType.TASK_ASSIGNED);
                notification.setCaseId(caseId);
                notification.setMessage("Task '" + converted.get("title") + "' assigned to " + converted.get("assignedTo") + " for case " + caseId);
            } else if ("task.overdue".equals(routingKey)) {
                notification.setType(NotificationType.TASK_OVERDUE);
                notification.setCaseId(caseId);
                notification.setMessage("Task '" + converted.get("title") + "' is overdue for case " + caseId + ". Assigned to: " + converted.get("assignedTo"));
            } else {
                notification.setType(NotificationType.TASK_ASSIGNED);
                notification.setMessage("Event received: " + routingKey);
            }
        } catch (Exception e) {
            notification.setType(NotificationType.TASK_ASSIGNED);
            notification.setMessage("Event received: " + routingKey);
        }

        notificationService.save(notification);
    }
}
