package com.nomolestar.workflowservice.messaging;

import com.nomolestar.workflowservice.config.RabbitMQConfig;
import com.nomolestar.workflowservice.events.TaskAssignedEvent;
import com.nomolestar.workflowservice.events.TaskOverdueEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public TaskEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishTaskAssigned(TaskAssignedEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "task.assigned", event);
    }

    public void publishTaskOverdue(TaskOverdueEvent event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE, "task.overdue", event);
    }
}
