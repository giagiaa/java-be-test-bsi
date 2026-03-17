package com.concertbooking.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.queue.paid}")
    private String paidQueue;

    @Value("${app.rabbitmq.routing.paid}")
    private String paidRoutingKey;

    @Value("${app.rabbitmq.queue.expired}")
    private String expiredQueue;

    @Value("${app.rabbitmq.routing.expired}")
    private String expiredRoutingKey;

    @Bean
    public DirectExchange bookingExchange() {
        return new DirectExchange(exchange);
    }

    @Bean
    public Queue paidQueue() {
        return new Queue(paidQueue);
    }

    @Bean
    public Binding paidBinding(Queue paidQueue, DirectExchange bookingExchange) {
        return BindingBuilder.bind(paidQueue).to(bookingExchange).with(paidRoutingKey);
    }

    @Bean
    public Queue expiredQueue() {
        return new Queue(expiredQueue);
    }

    @Bean
    public Binding expiredBinding(Queue expiredQueue, DirectExchange bookingExchange) {
        return BindingBuilder.bind(expiredQueue).to(bookingExchange).with(expiredRoutingKey);
    }
}
