package com.cts.serviceimpl;

import com.cts.dto.KpiReportResponseDTO;
import com.cts.entity.KpiReport;
import com.cts.enums.BookingType;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.BookingRepository;
import com.cts.repository.KpiReportRepository;
import com.cts.service.KpiReportService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class KpiReportServiceImpl implements KpiReportService {

    private final BookingRepository bookingRepo;
    private final KpiReportRepository kpiRepo;

    @Override
    @Transactional
    public KpiReportResponseDTO generateMonthlyReport(int month, int year) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusNanos(1);
        String label = "Monthly - " + year + "-" + String.format("%02d", month);
        return calculateAndSaveKpiReport(start, end, label);
    }

    @Override
    @Transactional
    public KpiReportResponseDTO generateCustomReport(LocalDateTime start, LocalDateTime end) {
        return calculateAndSaveKpiReport(start, end, "Custom Range");
    }

    @Override
    @Transactional
    public List<KpiReportResponseDTO> generateYearlyReport(int year) {
        log.info("Generating yearly breakdown for {} using high-performance conditional aggregation", year);

        // 1. Fetch ALL stats for the whole year in a SINGLE query
        List<com.cts.dto.MonthlyKpiStatsDTO> allStats = bookingRepo.getMonthlyStats(year);

        List<KpiReportResponseDTO> yearlyData = new ArrayList<>();

        for (int month = 1; month <= 12; month++) {
            int currentMonth = month;
            LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
            LocalDateTime end = start.plusMonths(1).minusNanos(1);
            String label = "Monthly - " + year + "-" + String.format("%02d", month);

            // Find the stats for this specific month
            com.cts.dto.MonthlyKpiStatsDTO stats = allStats.stream()
                    .filter(s -> s.getMonth() == currentMonth)
                    .findFirst()
                    .orElse(new com.cts.dto.MonthlyKpiStatsDTO(currentMonth, BigDecimal.ZERO, 0L, 0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO));

            yearlyData.add(saveReport(start, end, label,
                                    stats.getTotalRevenue(),
                                    stats.getTotalBookings(),
                                    stats.getTotalCancellations(),
                                    stats.getFlightRevenue(),
                                    stats.getHotelRevenue(),
                                    stats.getTransportRevenue(),
                                    stats.getPackageRevenue(),
                                    stats.getCancelledRevenue()));
        }
        return yearlyData;
    }

    @Override
    public List<KpiReportResponseDTO> getAllReports() {
        return kpiRepo.findAll().stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public KpiReportResponseDTO getReportById(Long id) {
        KpiReport report = kpiRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));
        return mapToDTO(report);
    }

    private KpiReportResponseDTO calculateAndSaveKpiReport(LocalDateTime start, LocalDateTime end, String label) {
        log.info("Calculating KPI for period {} to {} with label {}", start, end, label);

        com.cts.dto.KpiStatsDTO stats = bookingRepo.getStats(start, end);

        if (stats == null) {
            stats = new com.cts.dto.KpiStatsDTO(BigDecimal.ZERO, 0L, 0L, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        return saveReport(start, end, label,
                        stats.getTotalRevenue(),
                        stats.getTotalBookings(),
                        stats.getTotalCancellations(),
                        stats.getFlightRevenue(),
                        stats.getHotelRevenue(),
                        stats.getTransportRevenue(),
                        stats.getPackageRevenue(),
                        stats.getCancelledRevenue());
    }

    private KpiReportResponseDTO saveReport(LocalDateTime start, LocalDateTime end, String label,
                                         BigDecimal totalRev, long totalB, long cancelled,
                                         BigDecimal fRev, BigDecimal hRev, BigDecimal tRev, BigDecimal pRev, BigDecimal cRev) {

        BigDecimal avgValue = BigDecimal.ZERO;
        if (totalB > 0) {
            avgValue = totalRev.divide(BigDecimal.valueOf(totalB), 2, RoundingMode.HALF_UP);
        }

        Double cancelRate = 0.0;
        if (totalB > 0) {
            cancelRate = ((double) cancelled / totalB) * 100;
        }

        KpiReport report = kpiRepo.findFirstByStartDateAndEndDate(start, end)
                .orElse(KpiReport.builder().build());

        report.setGeneratedAt(LocalDateTime.now());
        report.setStartDate(start);
        report.setEndDate(end);
        report.setReportLabel(label);
        report.setTotalRevenue(totalRev);
        report.setTotalBookings(totalB);
        report.setTotalCancellations(cancelled);
        report.setCancellationRate(cancelRate);
        report.setAverageBookingValue(avgValue);
        report.setFlightRevenue(fRev);
        report.setHotelRevenue(hRev);
        report.setTransportRevenue(tRev);
        report.setPackageRevenue(pRev);
        report.setCancelledRevenue(cRev);

        return mapToDTO(kpiRepo.save(report));
    }

    private KpiReportResponseDTO mapToDTO(KpiReport report) {
        return KpiReportResponseDTO.builder()
                .reportId(report.getReportId())
                .generatedAt(report.getGeneratedAt())
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .reportLabel(report.getReportLabel())
                .totalRevenue(report.getTotalRevenue())
                .totalBookings(report.getTotalBookings())
                .totalCancellations(report.getTotalCancellations())
                .cancellationRate(report.getCancellationRate())
                .averageBookingValue(report.getAverageBookingValue())
                .flightRevenue(report.getFlightRevenue())
                .hotelRevenue(report.getHotelRevenue())
                .transportRevenue(report.getTransportRevenue())
                .packageRevenue(report.getPackageRevenue())
                .cancelledRevenue(report.getCancelledRevenue())
                .build();
    }
}
