package com.cts.controller;

import com.cts.dto.PartnerDTO;
import com.cts.dto.PartnerResponseDTO;
import com.cts.enums.PartnerType;
import com.cts.service.PartnerService;

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

    // ✅ CREATE
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public PartnerResponseDTO createPartner(@RequestBody PartnerDTO dto) {

        log.info("Received request to create partner");

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

        PartnerResponseDTO response = partnerService.updatePartner(id, dto);

        log.info("Partner updated successfully with ID: {}", id);

        return response;
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deletePartner(@PathVariable Long id) {

        log.info("Received request to delete partner with ID: {}", id);

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