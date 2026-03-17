package com.concertbooking.repository;

import com.concertbooking.domain.model.Ledger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface LedgerRepository extends JpaRepository<Ledger, UUID> {
    
    List<Ledger> findByBookingId(UUID bookingId);
    
    @Query("SELECT SUM(l.amount) FROM Ledger l WHERE l.bookingId IN (SELECT b.id FROM Booking b WHERE b.concert.id = :concertId) AND l.status = 'SUCCESS'")
    BigDecimal sumRevenueByConcertId(@Param("concertId") UUID concertId);

    @Query("SELECT l.paymentMethod, SUM(l.amount) FROM Ledger l WHERE l.bookingId IN (SELECT b.id FROM Booking b WHERE b.concert.id = :concertId) AND l.status = 'SUCCESS' GROUP BY l.paymentMethod")
    List<Object[]> sumRevenueByPaymentMethod(@Param("concertId") UUID concertId);
}
