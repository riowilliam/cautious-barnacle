package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author LordDev
 */
@Data
public class ARInvoiceDetailDto {
    private String invoiceNo;
    private String partnerName;
    private String contractName;
    private String projectName;
    private String bappNo;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal ppn;
    private BigDecimal pph;
    private BigDecimal deduction;
    private BigDecimal totalAmount;
    private String documentTracking;
    private Integer invoiceStatus;
    private String paymentStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdDate;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date modifiedDate;
    private String modifiedBy;
    private List<ItemDetailsRequestDto> itemDetails;

    public ARInvoiceDetailDto(String invoiceNo, String partnerName, String contractName, String projectName, String bappNo, BigDecimal amount, BigDecimal paidAmount, BigDecimal ppn, BigDecimal pph, BigDecimal deduction, BigDecimal totalAmount, String documentTracking, Integer invoiceStatus, String paymentStatus, Date createdDate, String createdBy, Date modifiedDate, String modifiedBy, String itemDetails) throws JsonProcessingException {
        this.invoiceNo = invoiceNo;
        this.partnerName = partnerName;
        this.contractName = contractName;
        this.projectName = projectName;
        this.bappNo = bappNo;
        this.amount = amount;
        this.paidAmount = paidAmount;
        this.ppn = ppn;
        this.pph = pph;
        this.deduction = deduction;
        this.totalAmount = totalAmount;
        this.documentTracking = documentTracking;
        this.invoiceStatus = invoiceStatus;
        this.paymentStatus = paymentStatus;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
        ObjectMapper objectMapper = new ObjectMapper();
        this.itemDetails = objectMapper.readValue(itemDetails, List.class);
    }
}
