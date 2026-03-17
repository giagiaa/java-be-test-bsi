package com.concertbooking.mapper;

import com.concertbooking.domain.model.Concert;
import com.concertbooking.domain.model.TicketCategory;
import com.concertbooking.dto.TicketCategoryDTO;
import com.concertbooking.dto.request.CreateConcertRequest;
import com.concertbooking.dto.response.ConcertResponse;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-17T12:26:55+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class ConcertMapperImpl implements ConcertMapper {

    @Override
    public Concert toEntity(CreateConcertRequest request) {
        if ( request == null ) {
            return null;
        }

        Concert.ConcertBuilder concert = Concert.builder();

        concert.artist( request.getArtist() );
        concert.capacity( request.getCapacity() );
        concert.datetime( request.getDatetime() );
        concert.name( request.getName() );
        concert.status( request.getStatus() );
        concert.timezone( request.getTimezone() );
        concert.venue( request.getVenue() );

        return concert.build();
    }

    @Override
    public ConcertResponse toResponse(Concert concert) {
        if ( concert == null ) {
            return null;
        }

        ConcertResponse.ConcertResponseBuilder concertResponse = ConcertResponse.builder();

        concertResponse.artist( concert.getArtist() );
        concertResponse.capacity( concert.getCapacity() );
        concertResponse.categories( toDtoList( concert.getCategories() ) );
        concertResponse.createdAt( concert.getCreatedAt() );
        concertResponse.datetime( concert.getDatetime() );
        concertResponse.id( concert.getId() );
        concertResponse.name( concert.getName() );
        concertResponse.status( concert.getStatus() );
        concertResponse.timezone( concert.getTimezone() );
        concertResponse.venue( concert.getVenue() );

        return concertResponse.build();
    }

    @Override
    public List<ConcertResponse> toResponseList(List<Concert> concerts) {
        if ( concerts == null ) {
            return null;
        }

        List<ConcertResponse> list = new ArrayList<ConcertResponse>( concerts.size() );
        for ( Concert concert : concerts ) {
            list.add( toResponse( concert ) );
        }

        return list;
    }

    @Override
    public TicketCategory toEntity(TicketCategoryDTO dto) {
        if ( dto == null ) {
            return null;
        }

        TicketCategory.TicketCategoryBuilder ticketCategory = TicketCategory.builder();

        ticketCategory.availableStock( dto.getAvailableStock() );
        ticketCategory.basePrice( dto.getBasePrice() );
        ticketCategory.capacity( dto.getCapacity() );
        ticketCategory.id( dto.getId() );
        ticketCategory.name( dto.getName() );

        return ticketCategory.build();
    }

    @Override
    public TicketCategoryDTO toDto(TicketCategory entity) {
        if ( entity == null ) {
            return null;
        }

        TicketCategoryDTO.TicketCategoryDTOBuilder ticketCategoryDTO = TicketCategoryDTO.builder();

        ticketCategoryDTO.availableStock( entity.getAvailableStock() );
        ticketCategoryDTO.basePrice( entity.getBasePrice() );
        ticketCategoryDTO.capacity( entity.getCapacity() );
        ticketCategoryDTO.id( entity.getId() );
        ticketCategoryDTO.name( entity.getName() );

        return ticketCategoryDTO.build();
    }

    @Override
    public List<TicketCategory> toEntityList(List<TicketCategoryDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<TicketCategory> list = new ArrayList<TicketCategory>( dtos.size() );
        for ( TicketCategoryDTO ticketCategoryDTO : dtos ) {
            list.add( toEntity( ticketCategoryDTO ) );
        }

        return list;
    }

    @Override
    public List<TicketCategoryDTO> toDtoList(List<TicketCategory> entities) {
        if ( entities == null ) {
            return null;
        }

        List<TicketCategoryDTO> list = new ArrayList<TicketCategoryDTO>( entities.size() );
        for ( TicketCategory ticketCategory : entities ) {
            list.add( toDto( ticketCategory ) );
        }

        return list;
    }
}
