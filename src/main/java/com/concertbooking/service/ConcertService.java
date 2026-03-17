package com.concertbooking.service;

import com.concertbooking.domain.model.Concert;
import com.concertbooking.domain.model.TicketCategory;
import com.concertbooking.domain.model.User;
import com.concertbooking.dto.request.CreateConcertRequest;
import com.concertbooking.dto.response.ConcertResponse;
import com.concertbooking.mapper.ConcertMapper;
import com.concertbooking.repository.ConcertRepository;
import com.concertbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final ConcertMapper concertMapper;

    @Transactional
    public ConcertResponse createConcert(CreateConcertRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found"));

        Concert concert = concertMapper.toEntity(request);
        concert.setCreatedBy(currentUser);

        List<TicketCategory> categories = request.getCategories().stream()
                .map(dto -> {
                    TicketCategory category = concertMapper.toEntity(dto);
                    category.setConcert(concert);
                    category.setAvailableStock(category.getCapacity());
                    return category;
                }).toList();

        concert.setCategories(categories);
        Concert savedConcert = concertRepository.save(concert);
        return concertMapper.toResponse(savedConcert);
    }

    public List<ConcertResponse> searchConcerts(
            String name, String artist, String venue,
            OffsetDateTime startDate, OffsetDateTime endDate, Integer minCapacity
    ) {
        List<Concert> concerts = concertRepository.searchConcerts(name, artist, venue, startDate, endDate, minCapacity);
        return concertMapper.toResponseList(concerts);
    }

    public ConcertResponse getConcertById(UUID id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concert not found"));
        return concertMapper.toResponse(concert);
    }

    @Transactional
    public ConcertResponse updateConcert(UUID id, CreateConcertRequest request) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concert not found"));
        
        concert.setName(request.getName());
        concert.setArtist(request.getArtist());
        concert.setVenue(request.getVenue());
        concert.setDatetime(request.getDatetime());
        concert.setTimezone(request.getTimezone());
        concert.setCapacity(request.getCapacity());
        concert.setStatus(request.getStatus());

        // Simple strategy: Clear and re-add categories for this assignment
        concert.getCategories().clear();
        List<TicketCategory> categories = request.getCategories().stream()
                .map(dto -> {
                    TicketCategory category = concertMapper.toEntity(dto);
                    category.setConcert(concert);
                    category.setAvailableStock(dto.getAvailableStock() != null ? dto.getAvailableStock() : category.getCapacity());
                    return category;
                }).toList();
        concert.getCategories().addAll(categories);

        return concertMapper.toResponse(concertRepository.save(concert));
    }

    @Transactional
    public void deleteConcert(UUID id) {
        concertRepository.deleteById(id);
    }
}
