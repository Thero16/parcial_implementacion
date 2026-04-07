package com.nomolestar.caseservice.messaging;

import com.nomolestar.caseservice.config.RabbitMQConfig;
import com.nomolestar.caseservice.events.CaseCreatedEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CaseEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public CaseEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCaseCreated(CaseCreatedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "case.created", event);
    }
}
