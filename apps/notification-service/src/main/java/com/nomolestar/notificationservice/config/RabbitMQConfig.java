package com.nomolestar.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "investigation.exchange";
    public static final String NOTIFICATION_QUEUE = "notification.queue";

    @Bean
    public TopicExchange investigationExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public Binding notificationEvidenceBinding(Queue notificationQueue, TopicExchange investigationExchange) {
        return BindingBuilder.bind(notificationQueue).to(investigationExchange).with("evidence.added");
    }

    @Bean
    public Binding notificationTaskBinding(Queue notificationQueue, TopicExchange investigationExchange) {
        return BindingBuilder.bind(notificationQueue).to(investigationExchange).with("task.assigned");
    }

    @Bean
    public Binding notificationTaskOverdueBinding(Queue notificationQueue, TopicExchange investigationExchange) {
        return BindingBuilder.bind(notificationQueue).to(investigationExchange).with("task.overdue");
    }

}
