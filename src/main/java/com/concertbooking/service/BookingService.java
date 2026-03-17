package com.concertbooking.service;

import com.concertbooking.domain.enums.BookingStatus;
import com.concertbooking.domain.model.Booking;
import com.concertbooking.domain.model.Concert;
import com.concertbooking.domain.model.TicketCategory;
import com.concertbooking.domain.model.User;
import com.concertbooking.dto.request.CreateBookingRequest;
import com.concertbooking.dto.request.PaymentRequest;
import com.concertbooking.dto.response.BookingResponse;
import com.concertbooking.mapper.BookingMapper;
import com.concertbooking.repository.BookingRepository;
import com.concertbooking.repository.ConcertRepository;
import com.concertbooking.repository.TicketCategoryRepository;
import com.concertbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final ConcertRepository concertRepository;
    private final TicketCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final InventoryService inventoryService;
    private final DynamicPricingService pricingService;
    private final BookingMapper bookingMapper;

    @Value("${app.booking.lock-duration-minutes}")
    private int lockDurationMinutes;

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request, String idempotencyKey) {
        // 1. Check if booking already exists for this idempotency key
        return bookingRepository.findByIdempotencyKey(idempotencyKey)
                .map(bookingMapper::toResponse)
                .orElseGet(() -> {
                    // 2. Load context
                    String username = SecurityContextHolder.getContext().getAuthentication().getName();
                    User user = userRepository.findByUsername(username)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    
                    Concert concert = concertRepository.findById(request.getConcertId())
                            .orElseThrow(() -> new RuntimeException("Concert not found"));
                    
                    TicketCategory category = categoryRepository.findById(request.getCategoryId())
                            .orElseThrow(() -> new RuntimeException("Category not found"));

                    // 3. Atomically lock tickets
                    inventoryService.lockTickets(category.getId(), request.getQuantity());

                    // 4. Calculate price (Dynamic)
                    BigDecimal unitPrice = pricingService.calculateCurrentPrice(category);
                    BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(request.getQuantity()));

                    // 5. Create booking record
                    Booking booking = Booking.builder()
                            .user(user)
                            .concert(concert)
                            .category(category)
                            .quantity(request.getQuantity())
                            .unitPrice(unitPrice)
                            .totalPrice(totalPrice)
                            .status(BookingStatus.PENDING)
                            .idempotencyKey(idempotencyKey)
                            .lockedUntil(OffsetDateTime.now().plusMinutes(lockDurationMinutes))
                            .build();

                    return bookingMapper.toResponse(bookingRepository.save(booking));
                });
    }

    @Transactional
    public BookingResponse processPayment(UUID bookingId, PaymentRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Booking is not in a payable state");
        }

        if (booking.getLockedUntil().isBefore(OffsetDateTime.now())) {
            // Lock expired, should be handled by background job but check here for safety
            inventoryService.releaseTickets(booking.getCategory().getId(), booking.getQuantity());
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.save(booking);
            throw new RuntimeException("Booking lock has expired");
        }

        booking.setStatus(BookingStatus.PAID);
        booking.setPaidAt(OffsetDateTime.now());
        booking.setConfirmedAt(OffsetDateTime.now());
        
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public void cancelBooking(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.PAID || booking.getStatus() == BookingStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel a paid booking");
        }

        if (booking.getStatus() == BookingStatus.PENDING) {
            inventoryService.releaseTickets(booking.getCategory().getId(), booking.getQuantity());
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(OffsetDateTime.now());
        bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public java.util.List<BookingResponse> getUserBookings(String username) {
        return bookingRepository.findByUserUsername(username).stream()
                .map(bookingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BookingResponse getBookingById(UUID id) {
        return bookingRepository.findById(id)
                .map(bookingMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }
}
