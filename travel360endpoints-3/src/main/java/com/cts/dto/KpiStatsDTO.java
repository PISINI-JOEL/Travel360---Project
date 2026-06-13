package com.cts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KpiStatsDTO {
    private BigDecimal totalRevenue;
    private Long totalBookings;
    private Long totalCancellations;
    private BigDecimal flightRevenue;
    private BigDecimal hotelRevenue;
    private BigDecimal transportRevenue;
    private BigDecimal packageRevenue;
    private BigDecimal cancelledRevenue;
}