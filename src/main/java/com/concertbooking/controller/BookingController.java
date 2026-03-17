package com.concertbooking.controller;

import com.concertbooking.domain.model.IdempotencyKey;
import com.concertbooking.dto.request.CreateBookingRequest;
import com.concertbooking.dto.request.PaymentRequest;
import com.concertbooking.dto.response.ApiResponse;
import com.concertbooking.dto.response.BookingResponse;
import com.concertbooking.service.BookingService;
import com.concertbooking.service.IdempotencyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Endpoints for ticket reservations and payments with idempotency")
public class BookingController {

    private final BookingService bookingService;
    private final IdempotencyService idempotencyService;
    private final ObjectMapper objectMapper;

    @PostMapping
    @Operation(summary = "Create a new booking reservation", description = "Locks tickets for 5 minutes. Requires Idempotency-Key header.")
    public ResponseEntity<?> create(
            @Valid @RequestBody CreateBookingRequest request,
            @Parameter(description = "Unique key for idempotency", required = true)
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        // Idempotency check
        Optional<IdempotencyKey> cached = idempotencyService.getResponse(idempotencyKey);
        if (cached.isPresent()) {
            try {
                BookingResponse data = objectMapper.readValue(cached.get().getResponseBody(), BookingResponse.class);
                return ResponseEntity.status(cached.get().getStatusCode()).body(ApiResponse.ok(data));
            } catch (Exception e) {
                // Fallback if mapping fails
            }
        }

        BookingResponse response = bookingService.createBooking(request, idempotencyKey);
        ApiResponse<BookingResponse> apiResponse = ApiResponse.ok(response);
        
        idempotencyService.saveResponse(idempotencyKey, response, 200);
        
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    @Operation(summary = "List current user's bookings")
    public ResponseEntity<ApiResponse<java.util.List<BookingResponse>>> listMyBookings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getUserBookings(username)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking details by ID")
    public ResponseEntity<ApiResponse<BookingResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.getBookingById(id)));
    }

    @PostMapping("/{id}/pay")
    @Operation(summary = "Process payment for a booking", description = "Finalizes inventory deduction and writes ledger entry")
    public ResponseEntity<ApiResponse<BookingResponse>> pay(
            @PathVariable UUID id,
            @Valid @RequestBody PaymentRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(bookingService.processPayment(id, request)));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel a booking reservation")
    public ResponseEntity<ApiResponse<Void>> cancel(@PathVariable UUID id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
