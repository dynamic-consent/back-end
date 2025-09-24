package com.example.dynamicconsent.domain.repository;

import com.example.dynamicconsent.domain.model.ConsentEvent;
import com.example.dynamicconsent.domain.model.enums.ConsentEventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ConsentEventRepository extends JpaRepository<ConsentEvent, Long> {

    @Query("SELECT ce FROM ConsentEvent ce WHERE ce.consent.user.userId = :userId " +
           "AND (:from IS NULL OR ce.createdAt >= :from) " +
           "AND (:to IS NULL OR ce.createdAt <= :to) " +
           "AND (:type IS NULL OR ce.type = :type) " +
           "ORDER BY ce.createdAt DESC")
    Page<ConsentEvent> findByUserIdAndFilters(@Param("userId") String userId,
                                              @Param("from") Instant from,
                                              @Param("to") Instant to,
                                              @Param("type") ConsentEventType type,
                                              Pageable pageable);

    @Query("SELECT ce FROM ConsentEvent ce WHERE ce.consent.id = :consentId " +
           "ORDER BY ce.createdAt DESC")
    Page<ConsentEvent> findByConsentId(@Param("consentId") Long consentId, Pageable pageable);

    @Query("SELECT ce FROM ConsentEvent ce WHERE ce.consent = :consent " +
           "ORDER BY ce.createdAt DESC")
    List<ConsentEvent> findByConsentOrderByCreatedAtDesc(@Param("consent") com.example.dynamicconsent.domain.model.Consent consent);
}
