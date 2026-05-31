package com.cts.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Partner;
import com.cts.entity.TravelPackage;
import com.cts.enums.TravelPackageCategory;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {

    Page<TravelPackage> findByCategory(TravelPackageCategory category, Pageable pageable);

    List<TravelPackage> findByPartner(Partner partner);
}