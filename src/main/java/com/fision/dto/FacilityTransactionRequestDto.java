package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FacilityTransactionRequestDto {
    private String partnerName;
    private String projectName;
    private Date transactionDate;
    private BigDecimal amount;
    private Date coverStartDate;
    private Date coverEndDate;
    private String facilityType;
    private String debitAdvice;
    private BigDecimal downPayment;
    private BigDecimal quote;
    private BigDecimal implementation;
    private BigDecimal maintenance;
}

