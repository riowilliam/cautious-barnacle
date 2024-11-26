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
@Table(name = "tb_ar_invoice")
public class TbArInvoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_invoice_id")
    private Long arInvoiceId;

    @Column(name = "invoice_no", nullable = false)
    private String invoiceNo;

    @Column(name = "partner_name", nullable = false)
    private String partnerName;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    @Column(name = "dpp_amount", nullable = false, precision = 20, scale = 3)
    private BigDecimal dppAmount;

    @Column(name = "retention", precision = 20, scale = 3)
    private BigDecimal retention;

    @Column(name = "down_payment", precision = 20, scale = 3)
    private BigDecimal downPayment;

    @Column(name = "progress", precision = 20, scale = 3)
    private BigDecimal progress;

    @Column(name = "ppn_amount", nullable = false, precision = 20, scale = 3)
    private BigDecimal ppnAmount;

    @Column(name = "pph_amount", nullable = false, precision = 20, scale = 3)
    private BigDecimal pphAmount;

    @Column(name = "deduction", nullable = false, precision = 20, scale = 3, columnDefinition = "decimal(20,3) default '0.000'")
    private BigDecimal deduction;

    @Column(name = "total_amount", precision = 20, scale = 3)
    private BigDecimal totalAmount;

    @Column(name = "contract_no")
    private String contractNo;

    @Column(name = "paid_item_details")
    private String paidItemDetails;

    @Column(name = "bapp_no")
    private String bappNo;

    @Column(name = "invoice_status", columnDefinition = "int COMMENT '0 = Not Approved, 1 = Approved, 2 = Rejected'")
    private Integer invoiceStatus;

    @Column(name = "payment_status", columnDefinition = "varchar(100) COMMENT 'Fully Paid if SUM of cash in from this invoice on tb_cash_in = Total Amount, Partially Paid if SUM of cash in from this invoice on tb_cash_in < Total Amount'")
    private String paymentStatus;

    @Column(name = "created_tm", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTm;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "modified_tm", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTm;

    @Column(name = "modified_by", length = 255)
    private String modifiedBy;

    @Column(name = "invoice_date")
    private Date invoiceDate;

    @Column(name = "bapp_date")
    private Date bappDate;

    @Column(name = "tax_invoice_number")
    private String taxInvoiceNumber;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdTm = now;
        this.modifiedTm = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.modifiedTm = new Date();
    }

}

