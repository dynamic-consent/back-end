package com.example.dynamicconsent.domain.repository;

import com.example.dynamicconsent.domain.model.DataShare;
import com.example.dynamicconsent.domain.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataShareRepository extends JpaRepository<DataShare, Long> {

    @Query("SELECT ds FROM DataShare ds WHERE ds.isActive = true " +
           "AND (ds.fromOrganization.orgId = :orgId OR ds.toOrganization.orgId = :orgId)")
    List<DataShare> findActiveSharesByOrgId(@Param("orgId") String orgId);

    @Query("SELECT ds FROM DataShare ds WHERE ds.isActive = true " +
           "AND ds.fromOrganization = :fromOrg AND ds.toOrganization = :toOrg")
    DataShare findActiveShareBetweenOrgs(@Param("fromOrg") Organization fromOrg,
                                        @Param("toOrg") Organization toOrg);
}
