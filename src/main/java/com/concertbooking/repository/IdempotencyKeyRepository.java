package com.concertbooking.repository;

import com.concertbooking.domain.model.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, String> {
    void deleteByCreatedAtBefore(OffsetDateTime time);
}
