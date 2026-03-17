package com.concertbooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCategoryDTO {
    private UUID id;

    @NotBlank
    private String name;

    @NotNull
    @Min(0)
    private BigDecimal basePrice;

    @NotNull
    @Min(1)
    private Integer capacity;

    private Integer availableStock;
}
