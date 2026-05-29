package com.cts.dto;

import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerDTO {
@NotBlank(message = "name cannot be blank")
    private String name;

    private PartnerType type;

    private PartnerStatus status;
}


