package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashInSummaryDto {
    private BigDecimal totalFullyPayment;
    private BigDecimal totalPartialyPayment;
    private BigDecimal totalCompleted;
    private BigDecimal totalPending;

    public CashInSummaryDto(BigDecimal totalFullyPayment, BigDecimal totalPartialyPayment, BigDecimal totalCompleted, BigDecimal totalPending) {
        this.totalFullyPayment = totalFullyPayment;
        this.totalPartialyPayment = totalPartialyPayment;
        this.totalCompleted = totalCompleted;
        this.totalPending = totalPending;
    }
}
