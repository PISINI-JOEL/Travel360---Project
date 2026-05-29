package com.cts.serviceimpl;

import com.cts.dto.PartnerDTO;
import com.cts.dto.PartnerResponseDTO;
import com.cts.entity.Partner;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;
import com.cts.exception.PartnerNotFoundException;
import com.cts.repository.PartnerRepository;
import com.cts.service.PartnerService;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class PartnerServiceImpl implements PartnerService {

    private final PartnerRepository partnerRepo;

    // ✅ GET BY ID
    @Override
    public PartnerResponseDTO getPartnerById(Long id) {

        Partner partner = partnerRepo.findById(id).orElse(null);

        if (partner == null) {
            throw new PartnerNotFoundException("Partner not found");
        }

        return mapToDTO(partner);
    }

    // ✅ UPDATE
    @Override
    public PartnerResponseDTO updatePartner(Long id, PartnerDTO dto) {

        Partner partner = partnerRepo.findById(id).orElse(null);

        if (partner == null) {
            throw new PartnerNotFoundException("Partner not found");
        }

        partner.setName(dto.getName());
        partner.setType(dto.getType());
        partner.setStatus(dto.getStatus());

        partner = partnerRepo.save(partner);

        return mapToDTO(partner);
    }

    // ✅ DELETE
    @Override
    public void deletePartner(Long id) {

        Partner partner = partnerRepo.findById(id).orElse(null);

        if (partner == null) {
            throw new PartnerNotFoundException("Partner not found");
        }

        partner.setStatus(PartnerStatus.INACTIVE);
        partnerRepo.save(partner);
    }

    // ✅ GET BY CATEGORY
    @Override
    public List<PartnerResponseDTO> getPartnerByCategory(PartnerType type) {

        List<Partner> list = partnerRepo.findByType(type);

        return list.stream()
                .map(this::mapToDTO)
                .toList();
    }

    // ✅ COMMON MAPPER
    private PartnerResponseDTO mapToDTO(Partner partner) {

        return PartnerResponseDTO.builder()
                .partnerId(partner.getPartnerId())
                .name(partner.getName())
                .type(partner.getType())
                .status(PartnerStatus.ACTIVE)
                .build();
    }

	
    

    @Override
     public PartnerResponseDTO createPartner(PartnerDTO dto) {
    Partner partner = Partner.builder()
            .name(dto.getName())
            .type(dto.getType())
            .status(dto.getStatus())
            .build();

    partner = partnerRepo.save(partner);

    return mapToDTO(partner);
}

}
