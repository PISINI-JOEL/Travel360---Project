package com.cts.entity;

import com.cts.enums.TravelPackageCategory;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TravelPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;

    private String packageName;

    private String destination;

    private double price;

    private int durationDays;

    @Enumerated(EnumType.STRING)
    private TravelPackageCategory category;
}

