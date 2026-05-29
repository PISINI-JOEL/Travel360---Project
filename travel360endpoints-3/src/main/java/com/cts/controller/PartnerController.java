package com.cts.controller;


import com.cts.dto.PartnerDTO;
import com.cts.dto.PartnerResponseDTO;
import com.cts.enums.PartnerType;
import com.cts.service.PartnerService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partners")
@AllArgsConstructor
public class PartnerController {

    private final PartnerService partnerService;

    // ✅ CREATE
    @PostMapping
    public PartnerResponseDTO createPartner(@RequestBody PartnerDTO dto) {
        return partnerService.createPartner(dto);
    }

    // ✅ UPDATE
    @PutMapping("/{id}")
    public PartnerResponseDTO updatePartner(@PathVariable Long id,
                                            @RequestBody PartnerDTO dto) {
        return partnerService.updatePartner(id, dto);
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public String deletePartner(@PathVariable Long id) {
        partnerService.deletePartner(id);
        return "Partner deleted successfully";
    }

    // ✅ GET BY CATEGORY (type)
    @GetMapping("/category/{type}")
    public List<PartnerResponseDTO> getPartnerByCategory(@PathVariable PartnerType type) {
        return partnerService.getPartnerByCategory(type);
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public PartnerResponseDTO getPartnerById(@PathVariable Long id) {
        return partnerService.getPartnerById(id);
    }
}
