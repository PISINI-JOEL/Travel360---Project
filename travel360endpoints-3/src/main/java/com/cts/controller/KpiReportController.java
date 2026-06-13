package com.cts.controller;

import com.cts.dto.KpiReportResponseDTO;
import com.cts.entity.KpiReport;
import com.cts.service.KpiReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
@AllArgsConstructor
@Tag(name = "KPI Report Controller", description = "Generate and manage business KPI reports")
@Slf4j
public class KpiReportController {

    private final KpiReportService kpiService;

    @Operation(summary = "Generate a monthly KPI report")
    @PostMapping("/kpi/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KpiReportResponseDTO> generateReport(
            @RequestParam int month,
            @RequestParam int year) {

        log.info("Request to generate monthly KPI report for {}/{}", month, year);
        KpiReportResponseDTO report = kpiService.generateMonthlyReport(month, year);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    @Operation(summary = "Generate a custom date range KPI report")
    @PostMapping("/kpi/generate-custom")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KpiReportResponseDTO> generateCustomReport(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {

        log.info("Request to generate custom KPI report from {} to {}", startDate, endDate);
        KpiReportResponseDTO report = kpiService.generateCustomReport(startDate, endDate);
        return new ResponseEntity<>(report, HttpStatus.CREATED);
    }

    @Operation(summary = "Get yearly breakdown (monthly reports) for a specific year")
    @GetMapping("/kpi/yearly/{year}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KpiReportResponseDTO>> getYearlyReport(@PathVariable int year) {
        log.info("Fetching yearly breakdown for {}", year);
        return ResponseEntity.ok(kpiService.generateYearlyReport(year));
    }

    @Operation(summary = "Get all generated KPI reports")
    @GetMapping("/kpi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<KpiReportResponseDTO>> getAllReports() {
        log.info("Fetching all KPI reports");
        return ResponseEntity.ok(kpiService.getAllReports());
    }

    @Operation(summary = "Get specific KPI report by ID")
    @GetMapping("/kpi/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KpiReportResponseDTO> getReport(@PathVariable Long id) {
        log.info("Fetching KPI report with id: {}", id);
        return ResponseEntity.ok(kpiService.getReportById(id));
    }
}
