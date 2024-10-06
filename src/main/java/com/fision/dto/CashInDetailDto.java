package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fision.utils.ConstantsUtils;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CashInDetailDto {
    private String projectName;
    private String invoiceNo;
    private String partnerName;
    private String contractName;
    private BigDecimal paymentAmount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date paymentDate;
    private String paymentType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdDate;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date modifiedDate;
    private String modifiedBy;
    private String CashInStatus;

    public CashInDetailDto(String projectName, String invoiceNo, String partnerName, String contractName, BigDecimal paymentAmount, Date paymentDate, Integer paymentType, Date createdDate, String createdBy, Date modifiedDate, String modifiedBy, String cashInStatus) {
        this.projectName = projectName;
        this.invoiceNo = invoiceNo;
        this.partnerName = partnerName;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.paymentType = paymentType == 1 ? ConstantsUtils.FULLY_PAYMENT : ConstantsUtils.PARTIALLY_PAYMENT;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
        CashInStatus = cashInStatus;
    }
}
