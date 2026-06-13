package com.cts.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KpiReportResponseDTO {
    private Long reportId;
    private LocalDateTime generatedAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reportLabel;
    private BigDecimal totalRevenue;
    private Long totalBookings;
    private Long totalCancellations;
    private Double cancellationRate;
    private BigDecimal averageBookingValue;
    private BigDecimal flightRevenue;
    private BigDecimal hotelRevenue;
    private BigDecimal transportRevenue;
    private BigDecimal packageRevenue;
    private BigDecimal cancelledRevenue;
}
