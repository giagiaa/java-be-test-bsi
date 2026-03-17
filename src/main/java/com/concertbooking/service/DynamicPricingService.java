package com.concertbooking.service;

import com.concertbooking.domain.model.TicketCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class DynamicPricingService {

    /**
     * Calculates the current price based on demand.
     * Higher demand (lower stock ratio) leads to higher prices.
     */
    @Cacheable(value = "dynamicPricing", key = "#category.id", unless = "#result == null")
    public BigDecimal calculateCurrentPrice(TicketCategory category) {
        BigDecimal basePrice = category.getBasePrice();
        double total = category.getCapacity();
        double available = category.getAvailableStock();
        
        // Ratio of available tickets (1.0 = full, 0.0 = empty)
        double availabilityRatio = available / total;
        
        // Demand multiplier: 0.1 to 2.5
        // At 100% capacity (availRatio=1.0), multiplier is close to 0.1
        // At 0% capacity (availRatio=0.0), multiplier is 2.5
        double demandMultiplier = Math.max(0.1, 2.5 - (availabilityRatio * 2.4));
        
        BigDecimal multiplier = BigDecimal.valueOf(1 + demandMultiplier);
        return basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP);
    }
}
