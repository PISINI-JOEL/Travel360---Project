package com.cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.TravelPackage;
import com.cts.enums.TravelPackageCategory;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {

    List<TravelPackage> findByCategory(TravelPackageCategory category);
}