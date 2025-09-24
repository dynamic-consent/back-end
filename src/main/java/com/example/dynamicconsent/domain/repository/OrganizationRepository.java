package com.example.dynamicconsent.domain.repository;

import com.example.dynamicconsent.domain.model.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {

    Optional<Organization> findByOrgId(String orgId);

    @Query("SELECT o FROM Organization o WHERE o.isActive = true " +
           "AND (:category IS NULL OR o.category = :category) " +
           "AND (:q IS NULL OR LOWER(o.name) LIKE LOWER(CONCAT('%', :q, '%')) " +
           "OR LOWER(o.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Organization> findActiveOrganizations(@Param("category") String category, 
                                              @Param("q") String query, 
                                              Pageable pageable);
}
