package com.example.dynamicconsent.domain.repository;

import com.example.dynamicconsent.domain.model.Consent;
import com.example.dynamicconsent.domain.model.enums.ConsentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ConsentRepository extends JpaRepository<Consent, Long> {

    @Query("SELECT c FROM Consent c WHERE c.user.userId = :userId " +
           "AND (:status IS NULL OR c.status = :status) " +
           "AND (:orgId IS NULL OR c.organization.orgId = :orgId)")
    Page<Consent> findByUserIdAndFilters(@Param("userId") String userId,
                                        @Param("status") ConsentStatus status,
                                        @Param("orgId") String orgId,
                                        Pageable pageable);

    @Query("SELECT c FROM Consent c WHERE c.user.userId = :userId " +
           "AND c.organization.orgId = :orgId " +
           "AND (:status IS NULL OR c.status = :status)")
    Page<Consent> findByUserIdAndOrgId(@Param("userId") String userId,
                                      @Param("orgId") String orgId,
                                      @Param("status") ConsentStatus status,
                                      Pageable pageable);

    @Query("SELECT c FROM Consent c WHERE c.user.userId = :userId " +
           "AND c.validUntil IS NOT NULL " +
           "AND c.validUntil BETWEEN :from AND :to")
    List<Consent> findExpiringConsents(@Param("userId") String userId,
                                       @Param("from") Instant from,
                                       @Param("to") Instant to);

    @Query("SELECT COUNT(c) FROM Consent c WHERE c.user.userId = :userId AND c.status = 'ACTIVE'")
    long countActiveConsentsByUserId(@Param("userId") String userId);

    @Query("SELECT c FROM Consent c WHERE c.user.userId = :userId")
    List<Consent> findByUserId(@Param("userId") String userId);

    @Query("SELECT c FROM Consent c WHERE c.user.userId = :userId")
    Page<Consent> findByUserId(@Param("userId") String userId, Pageable pageable);
}
