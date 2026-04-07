package com.nomolestar.workflowservice.messaging;

import com.nomolestar.workflowservice.config.RabbitMQConfig;
import com.nomolestar.workflowservice.events.CaseCreatedEvent;
import com.nomolestar.workflowservice.service.TaskService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CaseCreatedEventListener {

    private final TaskService taskService;

    public CaseCreatedEventListener(TaskService taskService) {
        this.taskService = taskService;
    }

    @RabbitListener(queues = RabbitMQConfig.WORKFLOW_QUEUE)
    public void handleCaseCreated(CaseCreatedEvent event) {
        taskService.createDefaultTasksForCase(event);
    }
}
