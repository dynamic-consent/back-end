package com.example.dynamicconsent.service;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.domain.model.DataShare;
import com.example.dynamicconsent.domain.model.Organization;
import com.example.dynamicconsent.domain.repository.DataShareRepository;
import com.example.dynamicconsent.domain.repository.OrganizationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShareGraphService {

    @Autowired
    private DataShareRepository dataShareRepository;
    
    @Autowired
    private OrganizationRepository organizationRepository;

    public CommonDTOs.ShareGraphResponse getShareGraph(String orgId) {
        // Query all data sharing relationships related to this organization
        List<DataShare> shares = dataShareRepository.findActiveSharesByOrgId(orgId);
        
        // Node creation (organizations)
        Set<String> orgIds = new HashSet<>();
        for (DataShare share : shares) {
            orgIds.add(share.getFromOrganization().getOrgId());
            orgIds.add(share.getToOrganization().getOrgId());
        }
        
        List<CommonDTOs.ShareNodeResponse> nodes = orgIds.stream()
                .map(orgIdStr -> {
                    Organization org = organizationRepository.findByOrgId(orgIdStr).orElse(null);
                    if (org != null) {
                        return new CommonDTOs.ShareNodeResponse(
                            org.getOrgId(),
                            org.getName(),
                            org.getCategory(),
                            org.getIsmsCertified()
                        );
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        // Edge creation (data sharing relationships)
        List<CommonDTOs.ShareEdgeResponse> edges = shares.stream()
                .map(share -> new CommonDTOs.ShareEdgeResponse(
                    share.getFromOrganization().getOrgId(),
                    share.getToOrganization().getOrgId(),
                    share.getFromOrganization().getName(),
                    share.getToOrganization().getName(),
                    share.getVolume(),
                    share.getDataTypes()
                ))
                .collect(Collectors.toList());
        
        return new CommonDTOs.ShareGraphResponse(
            nodes,
            edges,
            Instant.now() // lastUpdated
        );
    }
}
