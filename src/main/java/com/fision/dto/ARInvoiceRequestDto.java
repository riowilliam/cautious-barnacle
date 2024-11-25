package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ARInvoiceRequestDto {
    private String invoiceNo;
    private String partnerName;
    private String contractNo;
    private String projectName;
    private String bappNo;
    private BigDecimal amount;
    private BigDecimal ppn;
    private BigDecimal ppnWapu;
    private BigDecimal pph;
    private BigDecimal totalAmount;
    private String note;
    List<ItemDetailsRequestDto> itemDetails;

}
