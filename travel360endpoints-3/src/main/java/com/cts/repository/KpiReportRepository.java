package com.cts.repository;

import com.cts.entity.KpiReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KpiReportRepository extends JpaRepository<KpiReport, Long> {
    java.util.Optional<com.cts.entity.KpiReport> findFirstByStartDateAndEndDate(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
