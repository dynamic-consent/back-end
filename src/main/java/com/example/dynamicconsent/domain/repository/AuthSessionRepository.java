package com.example.dynamicconsent.domain.repository;

import com.example.dynamicconsent.domain.model.AuthSession;
import com.example.dynamicconsent.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthSessionRepository extends JpaRepository<AuthSession, Long> {

    Optional<AuthSession> findByAccessTokenAndIsActiveTrue(String accessToken);

    Optional<AuthSession> findByRefreshTokenAndIsActiveTrue(String refreshToken);

    List<AuthSession> findByUserAndIsActiveTrue(User user);

    @Modifying
    @Query("UPDATE AuthSession a SET a.isActive = false WHERE a.user = :user")
    int deactivateAllByUser(@Param("user") User user);

    @Modifying
    @Query("UPDATE AuthSession a SET a.isActive = false WHERE a.expiresAt < :now")
    int deactivateExpiredSessions(@Param("now") Instant now);
}
