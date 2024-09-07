package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CashOutDetailDto {
    private Long idTmpCashOut;
    private String vendorName;
    private String invoice;
    private String projectName;
    private String bankAccount;
    private String bankName;
    private BigDecimal amount;
    private BigDecimal transferFee;
    private BigDecimal totalAmount;

    public CashOutDetailDto(Long idTmpCashOut, String vendorName, String invoice, String projectName, String bankAccount, String bankName, BigDecimal amount, BigDecimal transferFee, BigDecimal totalAmount) {
        this.idTmpCashOut = idTmpCashOut;
        this.vendorName = vendorName;
        this.invoice = invoice;
        this.projectName = projectName;
        this.bankAccount = bankAccount;
        this.bankName = bankName;
        this.amount = amount;
        this.transferFee = transferFee;
        this.totalAmount = totalAmount;
    }
}
