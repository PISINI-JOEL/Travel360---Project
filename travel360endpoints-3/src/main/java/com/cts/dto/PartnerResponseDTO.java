package com.cts.dto;


import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerResponseDTO {

    private Long partnerId;
    private String name;
    private PartnerType type;
    private PartnerStatus status;
}