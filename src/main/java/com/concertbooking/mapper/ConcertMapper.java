package com.concertbooking.mapper;

import com.concertbooking.domain.model.Concert;
import com.concertbooking.domain.model.TicketCategory;
import com.concertbooking.dto.TicketCategoryDTO;
import com.concertbooking.dto.request.CreateConcertRequest;
import com.concertbooking.dto.response.ConcertResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ConcertMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Concert toEntity(CreateConcertRequest request);

    ConcertResponse toResponse(Concert concert);

    List<ConcertResponse> toResponseList(List<Concert> concerts);

    TicketCategory toEntity(TicketCategoryDTO dto);

    TicketCategoryDTO toDto(TicketCategory entity);

    List<TicketCategory> toEntityList(List<TicketCategoryDTO> dtos);

    List<TicketCategoryDTO> toDtoList(List<TicketCategory> entities);
}
