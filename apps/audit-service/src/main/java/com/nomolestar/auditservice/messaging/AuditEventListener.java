package com.nomolestar.auditservice.messaging;

import com.nomolestar.auditservice.config.RabbitMQConfig;
import com.nomolestar.auditservice.model.AuditLogEntity;
import com.nomolestar.auditservice.service.AuditLogService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AuditEventListener {

    private final AuditLogService auditLogService;

    public AuditEventListener(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @RabbitListener(queues = RabbitMQConfig.AUDIT_QUEUE)
    public void handleAuditEvent(Message message) {
        String routingKey = message.getMessageProperties().getReceivedRoutingKey();
        String body = new String(message.getBody());

        AuditLogEntity log = new AuditLogEntity();
        log.setEventType(routingKey);
        log.setEntityId(null);
        log.setDescription("Event received: " + body);
        log.setTimestamp(LocalDateTime.now());
        log.setPerformedBy("system");

        auditLogService.save(log);
    }
}
