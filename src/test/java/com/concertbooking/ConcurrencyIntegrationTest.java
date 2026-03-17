package com.concertbooking;

import com.concertbooking.domain.model.Concert;
import com.concertbooking.domain.model.Role;
import com.concertbooking.domain.model.TicketCategory;
import com.concertbooking.domain.model.User;
import com.concertbooking.dto.request.CreateBookingRequest;
import com.concertbooking.repository.*;
import com.concertbooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class ConcurrencyIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private BookingService bookingService;
    @Autowired
    private TicketCategoryRepository categoryRepository;
    @Autowired
    private ConcertRepository concertRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BookingRepository bookingRepository;

    private UUID categoryId;
    private UUID concertId;

    @BeforeEach
    void setUp() {
        bookingRepository.deleteAll();
        categoryRepository.deleteAll();
        concertRepository.deleteAll();
        userRepository.deleteAll();

        // Seed 1 user
        User user = User.builder()
                .username("testuser")
                .password("pass")
                .email("test@example.com")
                .enabled(true)
                .roles(new HashSet<>())
                .build();
        userRepository.save(user);

        // Set Auth
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("testuser", "pass");
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Seed Concert and Category
        Concert concert = Concert.builder()
                .name("Rock Night")
                .artist("The Band")
                .venue("Arena")
                .datetime(OffsetDateTime.now().plusDays(10))
                .capacity(100)
                .build();
        concertId = concertRepository.save(concert).getId();

        TicketCategory category = TicketCategory.builder()
                .concert(concert)
                .name("VIP")
                .basePrice(new BigDecimal("100"))
                .capacity(50)
                .availableStock(10) // Only 10 tickets for the concurrency test
                .build();
        categoryId = categoryRepository.save(category).getId();
    }

    @Test
    void testConcurrencyPessimisticLocking() throws InterruptedException {
        int threadCount = 50; // More threads than tickets
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            executorService.execute(() -> {
                try {
                    // Refresh security context for each thread
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken("testuser", "pass"));
                    
                    CreateBookingRequest request = new CreateBookingRequest();
                    request.setConcertId(concertId);
                    request.setCategoryId(categoryId);
                    request.setQuantity(1);
                    
                    bookingService.createBooking(request, "idemp-key-concurrency-" + index);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        // Check Results
        assertEquals(10, successCount.get(), "Exactly 10 bookings should have succeeded");
        assertEquals(40, failureCount.get(), "Remaining 40 requests should have failed due to insufficient stock");

        TicketCategory updatedCategory = categoryRepository.findById(categoryId).get();
        assertEquals(0, updatedCategory.getAvailableStock(), "Stock should be exactly zero");
    }
}
