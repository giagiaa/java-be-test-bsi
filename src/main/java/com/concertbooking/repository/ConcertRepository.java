package com.concertbooking.repository;

import com.concertbooking.domain.model.Concert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConcertRepository extends JpaRepository<Concert, UUID> {
    
    @Query("""
           SELECT c FROM Concert c 
           WHERE (:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')))
           AND (:artist IS NULL OR LOWER(c.artist) LIKE LOWER(CONCAT('%', :artist, '%')))
           AND (:venue IS NULL OR LOWER(c.venue) LIKE LOWER(CONCAT('%', :venue, '%')))
           AND (:startDate IS NULL OR c.datetime >= :startDate)
           AND (:endDate IS NULL OR c.datetime <= :endDate)
           AND (:minCapacity IS NULL OR c.capacity >= :minCapacity)
           """)
    List<Concert> searchConcerts(
            @Param("name") String name,
            @Param("artist") String artist,
            @Param("venue") String venue,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            @Param("minCapacity") Integer minCapacity
    );
}
