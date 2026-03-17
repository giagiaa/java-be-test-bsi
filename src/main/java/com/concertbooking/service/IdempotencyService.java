package com.concertbooking.service;

import com.concertbooking.domain.model.IdempotencyKey;
import com.concertbooking.repository.IdempotencyKeyRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyKeyRepository repository;
    private final ObjectMapper objectMapper;

    public Optional<IdempotencyKey> getResponse(String key) {
        return repository.findById(key);
    }

    @Transactional
    public void saveResponse(String key, Object response, int statusCode) {
        try {
            String body = objectMapper.writeValueAsString(response);
            IdempotencyKey idempotencyKey = IdempotencyKey.builder()
                    .key(key)
                    .responseBody(body)
                    .statusCode(statusCode)
                    .build();
            repository.save(idempotencyKey);
        } catch (Exception e) {
            // Log error
        }
    }
}
