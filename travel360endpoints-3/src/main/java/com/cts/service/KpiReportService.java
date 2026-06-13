package com.cts.service;

import java.time.LocalDateTime;
import java.util.List;

import com.cts.dto.KpiReportResponseDTO;

public interface KpiReportService {
    KpiReportResponseDTO generateMonthlyReport(int month, int year);
    KpiReportResponseDTO generateCustomReport(LocalDateTime start, LocalDateTime end);
    List<KpiReportResponseDTO> generateYearlyReport(int year);
    List<KpiReportResponseDTO> getAllReports();
    KpiReportResponseDTO getReportById(Long id);
}
