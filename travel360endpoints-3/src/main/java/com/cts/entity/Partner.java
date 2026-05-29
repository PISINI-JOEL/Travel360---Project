package com.cts.entity;
import com.cts.enums.PartnerStatus;
import com.cts.enums.PartnerType;

import jakarta.persistence.*;
import lombok.*;
	

	@Entity
	@Table(name = "Partner")
	@Data   	
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public class Partner {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long partnerId;

	    @Column(length = 255)
	    private String name;

	    @Enumerated(EnumType.STRING)
	    private PartnerType type;

	    
	    private PartnerStatus status;
	
}
