package com.concertbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettlementResponse {
    private UUID concertId;
    private String concertName;
    private long totalTicketsSold;
    private BigDecimal totalRevenue;
    private Map<String, BigDecimal> paymentBreakdown;
    // Simple timeline placeholder (e.g., tickets sold per day could be added here)
}
