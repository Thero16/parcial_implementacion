package com.nomolestar.evidenceservice.messaging;

import com.nomolestar.evidenceservice.config.RabbitMQConfig;
import com.nomolestar.evidenceservice.events.EvidenceAddedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class EvidenceEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public EvidenceEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishEvidenceAdded(EvidenceAddedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "evidence.added", event);
    }
}
