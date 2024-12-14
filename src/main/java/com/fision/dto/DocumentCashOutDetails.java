package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class DocumentCashOutDetails {
    private Long documentCashOutId;
    private String documentName;
    private BigDecimal totalAmount;
    private int status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date createdTm;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date modifiedTm;
    private String modifiedBy;
    private String paymentBank;

    public DocumentCashOutDetails(Long documentCashOutId, String documentName, BigDecimal totalAmount, int status, Date createdTm, String createdBy, Date modifiedTm, String modifiedBy, String paymentBank) {
        this.documentCashOutId = documentCashOutId;
        this.documentName = documentName;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdTm = createdTm;
        this.createdBy = createdBy;
        this.modifiedTm = modifiedTm;
        this.modifiedBy = modifiedBy;
        this.paymentBank = paymentBank;
    }
}
