package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author LordDev
 */
@Data
public class StatisticsDetailsDto {
    private String statsHeader;
    private BigDecimal totalCashIn;
    private BigDecimal totalCashOut;

    public StatisticsDetailsDto(String statsHeader, BigDecimal totalCashIn, BigDecimal totalCashOut) {
        this.statsHeader = statsHeader;
        this.totalCashIn = totalCashIn;
        this.totalCashOut = totalCashOut;
    }
}
