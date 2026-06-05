package com.cts.controller;

import com.cts.config.AuthenticatedUserProvider;
import com.cts.constants.AuditActions;
import com.cts.dto.PartnerDTO;
import com.cts.dto.PartnerResponseDTO;
import com.cts.enums.AuditEntity;
import com.cts.enums.LogType;
import com.cts.enums.PartnerType;
import com.cts.service.AuditLogService;
import com.cts.service.PartnerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
@AllArgsConstructor
@Tag(name = "Partner Controller", description = "Onboard, update, and deactivate inventory partners (airlines, hotels, bus operators, package providers)")
@Slf4j
public class PartnerController {

    private final PartnerService partnerService;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    // ✅ CREATE
    @Operation(summary = "Onboard a new inventory partner")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PartnerResponseDTO createPartner(@RequestBody @Valid PartnerDTO dto) {

        log.info("Received request to create partner");
        auditLogService.logAction(AuditActions.CREATE_PARTNER, AuditEntity.PARTNER, null, authUser.currentOrNull(), LogType.INFO);

        PartnerResponseDTO response = partnerService.createPartner(dto);

        log.info("Partner created successfully with ID: {}", response.getPartnerId());

        return response;
    }

    // ✅ UPDATE
    @Operation(summary = "Update a partner by ID; deactivating cascades to their inventory")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public PartnerResponseDTO updatePartner(@PathVariable Long id,
                                            @RequestBody PartnerDTO dto) {

        log.info("Received request to update partner with ID: {}", id);
        auditLogService.logAction(AuditActions.UPDATE_PARTNER, AuditEntity.PARTNER, id, authUser.currentOrNull(), LogType.INFO);

        PartnerResponseDTO response = partnerService.updatePartner(id, dto);

        log.info("Partner updated successfully with ID: {}", id);

        return response;
    }

    // ✅ DELETE
    @Operation(summary = "Deactivate (soft-delete) a partner and all their listed inventory")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePartner(@PathVariable Long id) {

        log.info("Received request to delete partner with ID: {}", id);
        auditLogService.logAction(AuditActions.DELETE_PARTNER, AuditEntity.PARTNER, id, authUser.currentOrNull(), LogType.WARN);

        partnerService.deletePartner(id);

        log.info("Partner deleted successfully with ID: {}", id);

        return "Partner deleted successfully";
    }

    // ✅ GET BY CATEGORY (type)
    @Operation(summary = "Get partners by category (FLIGHT, HOTEL, BUS, PACKAGE)")
    @GetMapping("/category/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE_OFFICER')")
    public List<PartnerResponseDTO> getPartnerByCategory(@PathVariable PartnerType type) {

        log.info("Fetching partners by category: {}", type);

        List<PartnerResponseDTO> list = partnerService.getPartnerByCategory(type);

        log.info("Found {} partners for category: {}", list.size(), type);

        return list;
    }

    // ✅ GET BY ID
    @Operation(summary = "Get a partner by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE_OFFICER')")
    public PartnerResponseDTO getPartnerById(@PathVariable Long id) {

        log.info("Fetching partner with ID: {}", id);

        PartnerResponseDTO response = partnerService.getPartnerById(id);

        log.info("Partner fetched successfully with ID: {}", id);

        return response;
    }
}