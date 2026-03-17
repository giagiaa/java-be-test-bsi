package com.concertbooking.service;

import com.concertbooking.domain.model.Concert;
import com.concertbooking.dto.response.AnalyticsDashboardResponse;
import com.concertbooking.repository.BookingRepository;
import com.concertbooking.repository.ConcertRepository;
import com.concertbooking.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final BookingRepository bookingRepository;
    private final LedgerRepository ledgerRepository;
    private final ConcertRepository concertRepository;

    public AnalyticsDashboardResponse getDashboardMetrics() {
        long totalBookings = bookingRepository.count();
        BigDecimal totalRevenue = ledgerRepository.sumRevenueByConcertId(null); // NULL here usually means all

        List<Concert> concerts = concertRepository.findAll();
        double avgOccupancy = 0;
        if (!concerts.isEmpty()) {
            double totalOccupancy = 0;
            for (Concert c : concerts) {
                Long sold = bookingRepository.sumSoldQuantityByConcertId(c.getId());
                double rate = (sold != null && c.getCapacity() > 0) 
                        ? (double) sold / c.getCapacity() 
                        : 0;
                totalOccupancy += rate;
            }
            avgOccupancy = totalOccupancy / concerts.size();
        }

        return AnalyticsDashboardResponse.builder()
                .totalBookings(totalBookings)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .averageOccupancyRate(avgOccupancy)
                .build();
    }
}
