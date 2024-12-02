package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class CashInRequestDto {
    private String invoiceNo;
    private String partnerName;
    private BigDecimal invoiceAmount;
    private String projectName;
    private BigDecimal interestDeduction;
    private BigDecimal otherDeduction;
    private BigDecimal paymentAmount;
    private Integer paymentType;
    private String cashInStatus;
    private String paymentBank;
}
