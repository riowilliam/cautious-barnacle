package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FacilityTransactionDto {
    private String vendorName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date transactionDate;
    private BigDecimal amount;
    private String facilityType;
    private String transactionType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date approvalDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date tenorDate;

    public FacilityTransactionDto(String vendorName, Date transactionDate, BigDecimal amount, String facilityType, String transactionType, Date approvalDate, Date tenorDate) {
        this.vendorName = vendorName;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.facilityType = facilityType;
        this.transactionType = transactionType;
        this.approvalDate = approvalDate;
        this.tenorDate = tenorDate;
    }
}
