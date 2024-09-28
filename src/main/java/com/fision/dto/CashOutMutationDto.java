package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CashOutMutationDto {
    private Long idTmpCashOut;
    private String vendorName;
    private String invoice;
    private String projectName;
    private String documentName;
    private String bankAccount;
    private String bankAccountName;
    private String bankName;
    private BigDecimal amount;
    private BigDecimal transferFee;
    private BigDecimal totalAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdDate;
    private String createdBy;

    public CashOutMutationDto(Long idTmpCashOut, String vendorName, String invoice, String projectName, String documentName, String bankAccount, String bankAccountName, String bankName, BigDecimal amount, BigDecimal transferFee, BigDecimal totalAmount, Date createdDate, String createdBy) {
        this.idTmpCashOut = idTmpCashOut;
        this.vendorName = vendorName;
        this.invoice = invoice;
        this.projectName = projectName;
        this.documentName = documentName;
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.bankName = bankName;
        this.amount = amount;
        this.transferFee = transferFee;
        this.totalAmount = totalAmount;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
    }
}
