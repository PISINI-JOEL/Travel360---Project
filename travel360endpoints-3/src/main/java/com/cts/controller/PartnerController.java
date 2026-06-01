package com.cts.controller;

import com.cts.dto.PartnerDTO;
import com.cts.dto.PartnerResponseDTO;
import com.cts.enums.PartnerType;
import com.cts.service.PartnerService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
    public PartnerResponseDTO createPartner(@RequestBody PartnerDTO dto) {

        log.info("Received request to create partner");

        PartnerResponseDTO response = partnerService.createPartner(dto);

        log.info("Partner created successfully with ID: {}", response.getPartnerId());

        return response;
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public PartnerResponseDTO updatePartner(@PathVariable Long id,
                                            @RequestBody PartnerDTO dto) {

        log.info("Received request to update partner with ID: {}", id);

        PartnerResponseDTO response = partnerService.updatePartner(id, dto);

        log.info("Partner updated successfully with ID: {}", id);

        return response;
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public String deletePartner(@PathVariable Long id) {

        log.info("Received request to delete partner with ID: {}", id);

        partnerService.deletePartner(id);

        log.info("Partner deleted successfully with ID: {}", id);

        return "Partner deleted successfully";
    }

    // ✅ GET BY CATEGORY (type)
    @GetMapping("/category/{type}")
    public List<PartnerResponseDTO> getPartnerByCategory(@PathVariable PartnerType type) {

        log.info("Fetching partners by category: {}", type);

        List<PartnerResponseDTO> list = partnerService.getPartnerByCategory(type);

        log.info("Found {} partners for category: {}", list.size(), type);

        return list;
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public PartnerResponseDTO getPartnerById(@PathVariable Long id) {

        log.info("Fetching partner with ID: {}", id);

        PartnerResponseDTO response = partnerService.getPartnerById(id);

        log.info("Partner fetched successfully with ID: {}", id);

        return response;
    }
}