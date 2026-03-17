package com.concertbooking.dto.request;

import com.concertbooking.domain.enums.ConcertStatus;
import com.concertbooking.dto.TicketCategoryDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class CreateConcertRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String artist;

    @NotBlank
    private String venue;

    @NotNull
    private OffsetDateTime datetime;

    private String timezone = "UTC";

    @NotNull
    @Min(1)
    private Integer capacity;

    private ConcertStatus status = ConcertStatus.UPCOMING;

    @NotEmpty
    private List<TicketCategoryDTO> categories;
}
