package com.concertbooking.service;

import com.concertbooking.domain.model.TicketCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DynamicPricingServiceTest {

    private DynamicPricingService pricingService;

    @BeforeEach
    void setUp() {
        pricingService = new DynamicPricingService();
    }

    @Test
    void testPriceAtFullCapacity() {
        TicketCategory category = TicketCategory.builder()
                .id(UUID.randomUUID())
                .basePrice(new BigDecimal("100.00"))
                .capacity(100)
                .availableStock(100) // 100% available
                .build();

        BigDecimal price = pricingService.calculateCurrentPrice(category);
        
        // availabilityRatio = 1.0
        // demandMultiplier = max(0.1, 2.5 - (1.0 * 2.4)) = 0.1
        // price = 100 * (1 + 0.1) = 110.00
        assertEquals(new BigDecimal("110.00"), price);
    }

    @Test
    void testPriceAtZeroCapacity() {
        TicketCategory category = TicketCategory.builder()
                .id(UUID.randomUUID())
                .basePrice(new BigDecimal("100.00"))
                .capacity(100)
                .availableStock(0) // 0% available
                .build();

        BigDecimal price = pricingService.calculateCurrentPrice(category);
        
        // availabilityRatio = 0.0
        // demandMultiplier = max(0.1, 2.5 - (0.0 * 2.4)) = 2.5
        // price = 100 * (1 + 2.5) = 350.00
        assertEquals(new BigDecimal("350.00"), price);
    }

    @Test
    void testPriceAtHalfCapacity() {
        TicketCategory category = TicketCategory.builder()
                .id(UUID.randomUUID())
                .basePrice(new BigDecimal("100.00"))
                .capacity(100)
                .availableStock(50) // 50% available
                .build();

        BigDecimal price = pricingService.calculateCurrentPrice(category);
        
        // availabilityRatio = 0.5
        // demandMultiplier = max(0.1, 2.5 - (0.5 * 2.4)) = 2.5 - 1.2 = 1.3
        // price = 100 * (1 + 1.3) = 230.00
        assertEquals(new BigDecimal("230.00"), price);
    }
}
