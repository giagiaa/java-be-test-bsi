package com.concertbooking.dto.event;

import com.concertbooking.domain.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent implements Serializable {
    private UUID bookingId;
    private UUID userId;
    private UUID concertId;
    private UUID categoryId;
    private Integer quantity;
    private BookingStatus status;
    private String eventType; // e.g., PAID, EXPIRED, CANCELLED
}
