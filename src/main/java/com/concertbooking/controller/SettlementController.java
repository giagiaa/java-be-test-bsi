package com.concertbooking.controller;

import com.concertbooking.dto.response.ApiResponse;
import com.concertbooking.dto.response.SettlementResponse;
import com.concertbooking.service.SettlementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Settlement", description = "Endpoints for financial settlement reports and transaction logs")
public class SettlementController {

    private final SettlementService settlementService;
    private final com.concertbooking.service.LedgerService ledgerService;

    @GetMapping("/concerts/{id}/settlement")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    @Operation(summary = "Get settlement report for a concert", description = "ADMIN or VIEWER roles only")
    public ResponseEntity<ApiResponse<SettlementResponse>> getSettlement(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(settlementService.getSettlementByConcertId(id)));
    }

    @GetMapping("/transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'VIEWER')")
    @Operation(summary = "List all transactions", description = "ADMIN or VIEWER roles only")
    public ResponseEntity<ApiResponse<java.util.List<com.concertbooking.domain.model.Ledger>>> listTransactions() {
        return ResponseEntity.ok(ApiResponse.ok(ledgerService.getAllTransactions()));
    }
}
