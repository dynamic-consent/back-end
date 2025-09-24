package com.example.dynamicconsent.domain.repository;

import com.example.dynamicconsent.domain.model.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("SELECT n FROM Notice n WHERE n.isActive = true ORDER BY n.priority DESC, n.createdAt DESC")
    List<Notice> findActiveNoticesOrderByPriority(Pageable pageable);

    @Query("SELECT n FROM Notice n WHERE n.isActive = true ORDER BY n.createdAt DESC")
    Page<Notice> findActiveNotices(Pageable pageable);

    @Query("SELECT n FROM Notice n WHERE n.isActive = true AND n.category = :category ORDER BY n.createdAt DESC")
    Page<Notice> findActiveNoticesByCategory(String category, Pageable pageable);
}
