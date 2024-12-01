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
    private String contractNo;
    private String projectName;
    private String bappNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date bappDate;
    private BigDecimal amount;
    private BigDecimal retention;
    private BigDecimal downPayment;
    private BigDecimal progress;
    private BigDecimal paidAmount;
    private BigDecimal ppn;
    private BigDecimal pph;
    private BigDecimal deduction;
    private BigDecimal totalAmount;
    private Integer invoiceStatus;
    private String paymentStatus;
    private String taxInvoiceNumber;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date invoiceDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date createdDate;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date modifiedDate;
    private String modifiedBy;
    private List<ItemDetailsRequestDto> itemDetails;

    public ARInvoiceDetailDto(String invoiceNo, String partnerName, String contractNo, String projectName, String bappNo, Date bappDate, BigDecimal amount, BigDecimal retention, BigDecimal downPayment, BigDecimal progress, BigDecimal paidAmount, BigDecimal ppn, BigDecimal pph, BigDecimal deduction, BigDecimal totalAmount, Integer invoiceStatus, String paymentStatus, String taxInvoiceNumber, Date invoiceDate, Date createdDate, String createdBy, Date modifiedDate, String modifiedBy, String itemDetails) throws JsonProcessingException {
        this.invoiceNo = invoiceNo;
        this.partnerName = partnerName;
        this.contractNo = contractNo;
        this.projectName = projectName;
        this.bappNo = bappNo;
        this.bappDate = bappDate;
        this.amount = amount;
        this.retention = retention;
        this.downPayment = downPayment;
        this.progress = progress;
        this.paidAmount = paidAmount;
        this.ppn = ppn;
        this.pph = pph;
        this.deduction = deduction;
        this.totalAmount = totalAmount;
        this.invoiceStatus = invoiceStatus;
        this.paymentStatus = paymentStatus;
        this.taxInvoiceNumber = taxInvoiceNumber;
        this.invoiceDate = invoiceDate;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
        ObjectMapper objectMapper = new ObjectMapper();
        this.itemDetails = objectMapper.readValue(itemDetails, List.class);
    }
}
