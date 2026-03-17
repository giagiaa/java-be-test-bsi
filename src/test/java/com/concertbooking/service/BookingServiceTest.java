package com.concertbooking.service;

import com.concertbooking.domain.enums.BookingStatus;
import com.concertbooking.domain.model.Booking;
import com.concertbooking.domain.model.Concert;
import com.concertbooking.domain.model.TicketCategory;
import com.concertbooking.domain.model.User;
import com.concertbooking.dto.request.CreateBookingRequest;
import com.concertbooking.dto.response.BookingResponse;
import com.concertbooking.mapper.BookingMapper;
import com.concertbooking.repository.BookingRepository;
import com.concertbooking.repository.ConcertRepository;
import com.concertbooking.repository.TicketCategoryRepository;
import com.concertbooking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ConcertRepository concertRepository;
    @Mock
    private TicketCategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private InventoryService inventoryService;
    @Mock
    private DynamicPricingService pricingService;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void testCreateBookingFlow() {
        // Setup Security Context
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user1");
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        // Setup Mocks
        UUID concertId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        User user = User.builder().id(userId).username("user1").build();
        Concert concert = Concert.builder().id(concertId).build();
        TicketCategory category = TicketCategory.builder().id(categoryId).basePrice(new BigDecimal("100")).build();

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(pricingService.calculateCurrentPrice(category)).thenReturn(new BigDecimal("110"));
        
        Booking savedBooking = Booking.builder()
                .id(UUID.randomUUID())
                .status(BookingStatus.PENDING)
                .build();
        when(bookingRepository.save(any())).thenReturn(savedBooking);
        when(bookingMapper.toResponse(any())).thenReturn(BookingResponse.builder().id(savedBooking.getId()).status(BookingStatus.PENDING).build());

        // Execute
        CreateBookingRequest request = new CreateBookingRequest();
        request.setConcertId(concertId);
        request.setCategoryId(categoryId);
        request.setQuantity(2);

        BookingResponse response = bookingService.createBooking(request, "idemp-key-1");

        // Verify
        assertNotNull(response);
        assertEquals(BookingStatus.PENDING, response.getStatus());
        verify(inventoryService, times(1)).lockTickets(categoryId, 2);
        verify(bookingRepository, times(1)).save(any());
    }
}
