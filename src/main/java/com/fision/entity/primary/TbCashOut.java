package com.fision.entity.primary;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author LordDev
 */

@Entity
@Data
@Table(name = "tb_cash_out")
public class TbCashOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cash_out_id")
    private Long cashOutId;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "amount", precision = 20, scale = 3)
    private BigDecimal amount;

    @Column(name = "invoice_title")
    private String invoiceTitle;

    @Column(name = "transfer_fee", precision = 20, scale = 3)
    private BigDecimal transferFee;

    @Column(name = "total", precision = 20, scale = 3, columnDefinition = "decimal(20,3) COMMENT 'amount + transfer_fee'")
    private BigDecimal total;

    @Column(name = "document_cash_out_name")
    private String documentCashOutName;

    @Column(name = "payment_bank_code")
    private String paymentBankCode;

    @Column(name = "created_tm", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTm;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "modified_tm", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTm;

    @Column(name = "modified_by", length = 255)
    private String modifiedBy;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdTm = now;
        this.modifiedTm = now;
        if (transferFee == null) this.transferFee = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedTm = new Date();
    }

}

