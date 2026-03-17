package com.concertbooking.messaging;

import com.concertbooking.dto.event.BookingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing.paid}")
    private String paidRoutingKey;

    @Value("${app.rabbitmq.routing.expired}")
    private String expiredRoutingKey;

    public void publishPaidEvent(BookingEvent event) {
        rabbitTemplate.convertAndSend(exchange, paidRoutingKey, event);
    }

    public void publishExpiredEvent(BookingEvent event) {
        rabbitTemplate.convertAndSend(exchange, expiredRoutingKey, event);
    }
}
