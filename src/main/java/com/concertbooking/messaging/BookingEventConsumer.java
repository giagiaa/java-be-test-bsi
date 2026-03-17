package com.concertbooking.messaging;

import com.concertbooking.domain.enums.BookingStatus;
import com.concertbooking.dto.event.BookingEvent;
import com.concertbooking.service.InventoryService;
import com.concertbooking.repository.BookingRepository;
import com.concertbooking.domain.model.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventConsumer {

    private final InventoryService inventoryService;
    private final BookingRepository bookingRepository;

    @RabbitListener(queues = "${app.rabbitmq.queue.paid}")
    @Transactional
    public void handleBookingPaid(BookingEvent event) {
        log.info("Processing paid booking: {}", event.getBookingId());
        // Logic for post-payment (e.g., ticket delivery)
        bookingRepository.findById(event.getBookingId()).ifPresent(booking -> {
            booking.setStatus(BookingStatus.DELIVERED);
            bookingRepository.save(booking);
        });
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.expired}")
    @Transactional
    public void handleBookingExpired(BookingEvent event) {
        log.info("Processing expired booking: {}", event.getBookingId());
        bookingRepository.findById(event.getBookingId()).ifPresent(booking -> {
            if (booking.getStatus() == BookingStatus.PENDING) {
                inventoryService.releaseTickets(booking.getCategory().getId(), booking.getQuantity());
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            }
        });
    }
}
