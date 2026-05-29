package com.cts.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.entity.Partner;
import com.cts.enums.PartnerType;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

        List<Partner> findByType(PartnerType type);
}

