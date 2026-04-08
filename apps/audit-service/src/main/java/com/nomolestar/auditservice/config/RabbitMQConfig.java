package com.nomolestar.auditservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "investigation.exchange";
    public static final String AUDIT_QUEUE = "audit.queue";

    @Bean
    public TopicExchange investigationExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue auditQueue() {
        return new Queue(AUDIT_QUEUE, true);
    }

    @Bean
    public Binding auditQueueBinding(Queue auditQueue, TopicExchange investigationExchange) {
        return BindingBuilder.bind(auditQueue).to(investigationExchange).with("#");
    }
}
