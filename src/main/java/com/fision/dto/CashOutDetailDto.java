package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CashOutDetailDto {
    private Long idTmpCashOut;
    private String vendorName;
    private String invoice;
    private String projectName;
    private String bankAccount;
    private String bankAccountName;
    private String bankName;
    private BigDecimal transferAmount;
    private BigDecimal transferFee;
    private BigDecimal paymentAmount;

    public CashOutDetailDto(Long idTmpCashOut, String vendorName, String invoice, String projectName, String bankAccount, String bankAccountName, String bankName, BigDecimal transferAmount, BigDecimal transferFee, BigDecimal paymentAmount) {
        this.idTmpCashOut = idTmpCashOut;
        this.vendorName = vendorName;
        this.invoice = invoice;
        this.projectName = projectName;
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.bankName = bankName;
        this.transferAmount = transferAmount;
        this.transferFee = transferFee;
        this.paymentAmount = paymentAmount;
    }
}
