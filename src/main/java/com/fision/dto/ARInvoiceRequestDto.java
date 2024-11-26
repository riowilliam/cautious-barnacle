package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class ARInvoiceRequestDto {
    private String invoiceNo;
    private String partnerName;
    private String contractNo;
    private String projectName;
    private String taxInvoiceNumber;
    private String bappNo;
    private Date bappDate;
    private Date invoiceDate;
    private BigDecimal amount;
    private BigDecimal retention;
    private BigDecimal downPayment;
    private BigDecimal progress;
    private BigDecimal ppn;
    private BigDecimal ppnWapu;
    private BigDecimal pph;
    private BigDecimal totalAmount;
    private String note;
    List<ItemDetailsRequestDto> itemDetails;
}
