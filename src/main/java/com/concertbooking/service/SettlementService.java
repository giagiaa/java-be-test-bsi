package com.concertbooking.service;

import com.concertbooking.domain.model.Concert;
import com.concertbooking.dto.response.SettlementResponse;
import com.concertbooking.repository.BookingRepository;
import com.concertbooking.repository.ConcertRepository;
import com.concertbooking.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final ConcertRepository concertRepository;
    private final BookingRepository bookingRepository;
    private final LedgerRepository ledgerRepository;

    public SettlementResponse getSettlementByConcertId(UUID concertId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("Concert not found"));

        Long totalTicketsSold = bookingRepository.sumSoldQuantityByConcertId(concertId);
        BigDecimal totalRevenue = ledgerRepository.sumRevenueByConcertId(concertId);
        List<Object[]> RawBreakdown = ledgerRepository.sumRevenueByPaymentMethod(concertId);

        Map<String, BigDecimal> breakdown = new HashMap<>();
        if (RawBreakdown != null) {
            for (Object[] row : RawBreakdown) {
                breakdown.put((String) row[0], (BigDecimal) row[1]);
            }
        }

        return SettlementResponse.builder()
                .concertId(concert.getId())
                .concertName(concert.getName())
                .totalTicketsSold(totalTicketsSold != null ? totalTicketsSold : 0)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .paymentBreakdown(breakdown)
                .build();
    }
}
