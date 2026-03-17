package com.concertbooking.mapper;

import com.concertbooking.domain.model.Booking;
import com.concertbooking.dto.response.BookingResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "concertId", source = "concert.id")
    @Mapping(target = "categoryId", source = "category.id")
    BookingResponse toResponse(Booking booking);

    List<BookingResponse> toResponseList(List<Booking> bookings);
}
