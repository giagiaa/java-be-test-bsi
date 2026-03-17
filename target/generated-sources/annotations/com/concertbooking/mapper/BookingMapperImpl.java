package com.concertbooking.mapper;

import com.concertbooking.domain.model.Booking;
import com.concertbooking.domain.model.Concert;
import com.concertbooking.domain.model.TicketCategory;
import com.concertbooking.domain.model.User;
import com.concertbooking.dto.response.BookingResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-03-17T12:26:55+0700",
    comments = "version: 1.5.5.Final, compiler: Eclipse JDT (IDE) 3.45.0.v20260128-0750, environment: Java 21.0.9 (Eclipse Adoptium)"
)
@Component
public class BookingMapperImpl implements BookingMapper {

    @Override
    public BookingResponse toResponse(Booking booking) {
        if ( booking == null ) {
            return null;
        }

        BookingResponse.BookingResponseBuilder bookingResponse = BookingResponse.builder();

        bookingResponse.userId( bookingUserId( booking ) );
        bookingResponse.concertId( bookingConcertId( booking ) );
        bookingResponse.categoryId( bookingCategoryId( booking ) );
        bookingResponse.createdAt( booking.getCreatedAt() );
        bookingResponse.id( booking.getId() );
        bookingResponse.lockedUntil( booking.getLockedUntil() );
        bookingResponse.quantity( booking.getQuantity() );
        bookingResponse.status( booking.getStatus() );
        bookingResponse.totalPrice( booking.getTotalPrice() );
        bookingResponse.unitPrice( booking.getUnitPrice() );

        return bookingResponse.build();
    }

    @Override
    public List<BookingResponse> toResponseList(List<Booking> bookings) {
        if ( bookings == null ) {
            return null;
        }

        List<BookingResponse> list = new ArrayList<BookingResponse>( bookings.size() );
        for ( Booking booking : bookings ) {
            list.add( toResponse( booking ) );
        }

        return list;
    }

    private UUID bookingUserId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        User user = booking.getUser();
        if ( user == null ) {
            return null;
        }
        UUID id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID bookingConcertId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        Concert concert = booking.getConcert();
        if ( concert == null ) {
            return null;
        }
        UUID id = concert.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private UUID bookingCategoryId(Booking booking) {
        if ( booking == null ) {
            return null;
        }
        TicketCategory category = booking.getCategory();
        if ( category == null ) {
            return null;
        }
        UUID id = category.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
