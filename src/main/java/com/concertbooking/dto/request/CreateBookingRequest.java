package com.concertbooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateBookingRequest {
    @NotNull
    private UUID concertId;

    @NotNull
    private UUID categoryId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
