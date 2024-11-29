package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashInAmountsDto {
    private BigDecimal totalAmount;
    private BigDecimal totalInterestAmount;
    private BigDecimal totalOtherDeduction;

    public CashInAmountsDto(BigDecimal totalAmount, BigDecimal totalInterestAmount, BigDecimal totalOtherDeduction) {
        this.totalAmount = totalAmount;
        this.totalInterestAmount = totalInterestAmount;
        this.totalOtherDeduction = totalOtherDeduction;
    }
}
