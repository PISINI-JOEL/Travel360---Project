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

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
@AllArgsConstructor
@Slf4j
public class PartnerController {

    private final PartnerService partnerService;
    private final AuthenticatedUserProvider authUser;
    private final AuditLogService auditLogService;

    // ✅ CREATE
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
    @GetMapping("/category/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE_OFFICER')")
    public List<PartnerResponseDTO> getPartnerByCategory(@PathVariable PartnerType type) {

        log.info("Fetching partners by category: {}", type);

        List<PartnerResponseDTO> list = partnerService.getPartnerByCategory(type);

        log.info("Found {} partners for category: {}", list.size(), type);

        return list;
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','COMPLIANCE_OFFICER')")
    public PartnerResponseDTO getPartnerById(@PathVariable Long id) {

        log.info("Fetching partner with ID: {}", id);

        PartnerResponseDTO response = partnerService.getPartnerById(id);

        log.info("Partner fetched successfully with ID: {}", id);

        return response;
    }
}