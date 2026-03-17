package com.concertbooking.service;

import com.concertbooking.domain.model.TicketCategory;
import com.concertbooking.repository.TicketCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final TicketCategoryRepository ticketCategoryRepository;

    /**
     * Atomically locks and decrements available tickets for a category.
     * Uses PESSIMISTIC_WRITE lock on the category record.
     */
    @Transactional
    public void lockTickets(UUID categoryId, int quantity) {
        TicketCategory category = ticketCategoryRepository.findByIdForUpdate(categoryId)
                .orElseThrow(() -> new RuntimeException("Ticket category not found"));

        if (category.getAvailableStock() < quantity) {
            throw new RuntimeException("Insufficient tickets available");
        }

        category.setAvailableStock(category.getAvailableStock() - quantity);
        ticketCategoryRepository.save(category);
    }

    /**
     * Restores tickets back to inventory (e.g., on booking cancellation or expiry).
     */
    @Transactional
    public void releaseTickets(UUID categoryId, int quantity) {
        TicketCategory category = ticketCategoryRepository.findByIdForUpdate(categoryId)
                .orElseThrow(() -> new RuntimeException("Ticket category not found"));

        category.setAvailableStock(category.getAvailableStock() + quantity);
        ticketCategoryRepository.save(category);
    }
}
