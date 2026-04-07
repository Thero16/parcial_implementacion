package com.nomolestar.workflowservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "investigation.exchange";
    public static final String WORKFLOW_QUEUE = "workflow.queue";

    @Bean
    public TopicExchange investigationExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue workflowQueue() {
        return new Queue(WORKFLOW_QUEUE, true);
    }

    @Bean
    public Binding workflowQueueBinding(Queue workflowQueue, TopicExchange investigationExchange) {
        return BindingBuilder.bind(workflowQueue).to(investigationExchange).with("case.created");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
