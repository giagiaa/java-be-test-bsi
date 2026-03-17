package com.concertbooking.service;

import com.concertbooking.domain.model.Booking;
import com.concertbooking.domain.model.Ledger;
import com.concertbooking.repository.LedgerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;

    /**
     * Records a transaction in the immutable ledger.
     * Status can be SUCCESS, REFUNDED, or FAILED.
     */
    @Transactional
    public Ledger record(Booking booking, String paymentMethod, String status) {
        Ledger entry = Ledger.builder()
                .bookingId(booking.getId())
                .userId(booking.getUser().getId())
                .amount(booking.getTotalPrice())
                .paymentMethod(paymentMethod)
                .status(status)
                .build();

        return ledgerRepository.save(entry);
    }
    
    @Transactional
    public Ledger recordRefund(UUID bookingId, UUID userId, BigDecimal amount, String paymentMethod) {
        Ledger entry = Ledger.builder()
                .bookingId(bookingId)
                .userId(userId)
                .amount(amount.negate()) // Negative amount for refund
                .paymentMethod(paymentMethod)
                .status("REFUNDED")
                .build();

        return ledgerRepository.save(entry);
    }

    @Transactional(readOnly = true)
    public java.util.List<Ledger> getAllTransactions() {
        return ledgerRepository.findAll();
    }
}
