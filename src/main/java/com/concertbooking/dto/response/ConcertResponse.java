package com.concertbooking.dto.response;

import com.concertbooking.domain.enums.ConcertStatus;
import com.concertbooking.dto.TicketCategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConcertResponse {
    private UUID id;
    private String name;
    private String artist;
    private String venue;
    private OffsetDateTime datetime;
    private String timezone;
    private Integer capacity;
    private ConcertStatus status;
    private List<TicketCategoryDTO> categories;
    private OffsetDateTime createdAt;
}
