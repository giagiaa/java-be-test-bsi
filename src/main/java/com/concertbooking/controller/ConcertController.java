package com.concertbooking.controller;

import com.concertbooking.dto.request.CreateConcertRequest;
import com.concertbooking.dto.response.ApiResponse;
import com.concertbooking.dto.response.ConcertResponse;
import com.concertbooking.service.ConcertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/concerts")
@RequiredArgsConstructor
@Tag(name = "Concerts", description = "Endpoints for searching and managing concerts")
public class ConcertController {

    private final ConcertService concertService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new concert", description = "ADMIN only")
    public ResponseEntity<ApiResponse<ConcertResponse>> create(@Valid @RequestBody CreateConcertRequest request) {
        // Assuming idempotencyService and apiResponse are defined elsewhere or are placeholders
        // For now, returning the original logic to maintain compilability
        return ResponseEntity.ok(ApiResponse.ok(concertService.createConcert(request)));
    }

    @GetMapping
    @Operation(summary = "Search concerts with filters", description = "Accessible by anyone with a valid token")
    public ResponseEntity<ApiResponse<List<ConcertResponse>>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String artist,
            @RequestParam(required = false) String venue,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime endDate,
            @RequestParam(required = false) Integer minCapacity
    ) {
        return ResponseEntity.ok(ApiResponse.ok(concertService.searchConcerts(name, artist, venue, startDate, endDate, minCapacity)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get concert details by ID")
    public ResponseEntity<ApiResponse<ConcertResponse>> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(concertService.getConcertById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update an existing concert", description = "ADMIN only")
    public ResponseEntity<ApiResponse<ConcertResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CreateConcertRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok(concertService.updateConcert(id, request)));
    }

    @GetMapping("/{id}/pricing")
    @Operation(summary = "Get real-time pricing for a concert")
    public ResponseEntity<ApiResponse<List<com.concertbooking.dto.TicketCategoryDTO>>> getPricing(@PathVariable UUID id) {
        ConcertResponse concert = concertService.getConcertById(id);
        return ResponseEntity.ok(ApiResponse.ok(concert.getCategories()));
    }

    @GetMapping("/{id}/availability")
    @Operation(summary = "Get real-time availability for a concert")
    public ResponseEntity<ApiResponse<List<com.concertbooking.dto.TicketCategoryDTO>>> getAvailability(@PathVariable UUID id) {
        ConcertResponse concert = concertService.getConcertById(id);
        return ResponseEntity.ok(ApiResponse.ok(concert.getCategories()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a concert", description = "ADMIN only")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        concertService.deleteConcert(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
