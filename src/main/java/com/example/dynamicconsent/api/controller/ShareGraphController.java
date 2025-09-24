package com.example.dynamicconsent.api.controller;

import com.example.dynamicconsent.api.dto.CommonDTOs;
import com.example.dynamicconsent.service.ShareGraphService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shares")
@Tag(name = "Data Sharing", description = "Data sharing graph related API")
public class ShareGraphController {

    @Autowired
    private ShareGraphService shareGraphService;

    @GetMapping("/graph")
    @Operation(summary = "Third-Party Sharing Graph", description = "Returns {nodes[],edges[],lastUpdated} structure")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Authentication Failed")
    })
    public ResponseEntity<CommonDTOs.ShareGraphResponse> getShareGraph(
            @Parameter(description = "Organization ID", example = "ORG001")
            @RequestParam String orgId,
            Authentication authentication) {
        // userId is not directly used in this mock graph generation but would be for authorization
        // String userId = authentication.getName();
        CommonDTOs.ShareGraphResponse graph = shareGraphService.getShareGraph(orgId);
        return ResponseEntity.ok(graph);
    }
}