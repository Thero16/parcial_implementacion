package com.nomolestar.notificationservice.messaging;

import com.nomolestar.notificationservice.config.RabbitMQConfig;
import com.nomolestar.notificationservice.enums.NotificationType;
import com.nomolestar.notificationservice.model.NotificationEntity;
import com.nomolestar.notificationservice.service.NotificationService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final Jackson2JsonMessageConverter messageConverter;

    public NotificationEventListener(NotificationService notificationService,
                                      Jackson2JsonMessageConverter messageConverter) {
        this.notificationService = notificationService;
        this.messageConverter = messageConverter;
    }

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleEvent(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();

        NotificationEntity notification = new NotificationEntity();
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        try {
            Object converted = messageConverter.fromMessage(message);

            if ("evidence.added".equals(routingKey) && converted instanceof LinkedHashMap map) {
                notification.setType(NotificationType.EVIDENCE_ADDED);
                Object caseId = map.get("caseId");
                notification.setCaseId(caseId instanceof Integer i ? i : null);
                notification.setMessage("New evidence added to case " + caseId + ": " + map.get("description"));
            } else if ("task.assigned".equals(routingKey) && converted instanceof LinkedHashMap map) {
                notification.setType(NotificationType.TASK_ASSIGNED);
                Object caseId = map.get("caseId");
                notification.setCaseId(caseId instanceof Integer i ? i : null);
                notification.setMessage("Task '" + map.get("title") + "' assigned to " + map.get("assignedTo") + " for case " + caseId);
            } else if ("task.overdue".equals(routingKey) && converted instanceof LinkedHashMap map) {
                notification.setType(NotificationType.TASK_OVERDUE);
                Object caseId = map.get("caseId");
                notification.setCaseId(caseId instanceof Integer i ? i : null);
                notification.setMessage("Task '" + map.get("title") + "' is overdue for case " + caseId + ". Assigned to: " + map.get("assignedTo"));
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
