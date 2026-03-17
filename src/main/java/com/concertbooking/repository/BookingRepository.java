package com.concertbooking.repository;

import com.concertbooking.domain.enums.BookingStatus;
import com.concertbooking.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    
    Optional<Booking> findByIdempotencyKey(String idempotencyKey);
    
    List<Booking> findByUserId(UUID userId);

    List<Booking> findByUserUsername(String username);
    
    List<Booking> findByStatusAndLockedUntilBefore(BookingStatus status, OffsetDateTime time);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.concert.id = :concertId AND b.status IN ('PAID', 'DELIVERED')")
    long countSoldTicketsByConcertId(@Param("concertId") UUID concertId);

    @Query("SELECT SUM(b.quantity) FROM Booking b WHERE b.concert.id = :concertId AND b.status IN ('PAID', 'DELIVERED')")
    Long sumSoldQuantityByConcertId(@Param("concertId") UUID concertId);
}
