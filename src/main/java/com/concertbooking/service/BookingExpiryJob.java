package com.concertbooking.service;

import com.concertbooking.domain.enums.BookingStatus;
import com.concertbooking.domain.model.Booking;
import com.concertbooking.dto.event.BookingEvent;
import com.concertbooking.messaging.BookingEventPublisher;
import com.concertbooking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingExpiryJob {

    private final BookingRepository bookingRepository;
    private final BookingEventPublisher eventPublisher;

    /**
     * Finds PENDING bookings where the lock has expired and triggers the expiry event.
     * Runs every 30 seconds.
     */
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void expireOldBookings() {
        OffsetDateTime now = OffsetDateTime.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusAndLockedUntilBefore(BookingStatus.PENDING, now);

        if (!expiredBookings.isEmpty()) {
            log.info("Found {} expired bookings to process", expiredBookings.size());
            for (Booking booking : expiredBookings) {
                // Publish event to RabbitMQ for async processing (inventory release and status update)
                eventPublisher.publishExpiredEvent(BookingEvent.builder()
                        .bookingId(booking.getId())
                        .concertId(booking.getConcert().getId())
                        .categoryId(booking.getCategory().getId())
                        .quantity(booking.getQuantity())
                        .status(BookingStatus.CANCELLED)
                        .eventType("EXPIRED")
                        .build());
                
                // Mark as CANCELLED immediately in DB to prevent multiple processing
                booking.setStatus(BookingStatus.CANCELLED);
                bookingRepository.save(booking);
            }
        }
    }
}
