package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FacilityTransactionRequestDto {
    private String vendorName;
    private String projectName;
    private Date transactionDate;
    private BigDecimal amount;
    private Date bankApprovalDate;
    private Date tenorDate;
    private String facilityType;
}
