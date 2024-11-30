package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author LordDev
 */

@Data
public class BalanceSummaryDetails {
    private String bankName;
    private BigDecimal totalCashInValue;
    private BigDecimal totalCashOutValue;
    private BigDecimal totalBalance;

    public BalanceSummaryDetails(String bankName, BigDecimal totalCashInValue, BigDecimal totalCashOutValue, BigDecimal totalBalance) {
        this.bankName = bankName;
        this.totalCashInValue = totalCashInValue;
        this.totalCashOutValue = totalCashOutValue;
        this.totalBalance = totalBalance;
    }
}
