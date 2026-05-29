package com.cts.service;

import com.cts.dto.PartnerDTO;
import com.cts.dto.PartnerResponseDTO;
import com.cts.enums.PartnerType;

import java.util.List;

public interface PartnerService {

    PartnerResponseDTO createPartner(PartnerDTO dto);

    PartnerResponseDTO updatePartner(Long id, PartnerDTO dto);

    void deletePartner(Long id);

    List<PartnerResponseDTO> getPartnerByCategory(PartnerType type);

    PartnerResponseDTO getPartnerById(Long id);
}