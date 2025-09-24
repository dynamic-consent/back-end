package com.example.dynamicconsent.domain.repository;

import com.example.dynamicconsent.domain.model.Notification;
import com.example.dynamicconsent.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.isRead = false")
    long countUnreadByUser(@Param("user") User user);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user = :user")
    int markAllAsReadByUser(@Param("user") User user);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.user = :user")
    int markAsReadByIdAndUser(@Param("id") Long id, @Param("user") User user);

    @Query("SELECT n FROM Notification n WHERE n.user = :user " +
           "AND n.createdAt > :since " +
           "ORDER BY n.createdAt DESC")
    Page<Notification> findByUserAndSince(@Param("user") User user,
                                          @Param("since") Instant since,
                                          Pageable pageable);
}
