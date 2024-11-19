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
@Table(name = "tmp_cash_out")
public class TmpCashOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cash_out_id")
    private Long cashOutId;

    @Column(name = "vendor_name", nullable = false)
    private String vendorName;

    @Column(name = "amount", precision = 20, scale = 3)
    private BigDecimal amount;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "invoice_title")
    private String invoiceTitle;

    @Column(name = "transfer_fee", precision = 20, scale = 3)
    private BigDecimal transferFee;

    @Column(name = "total", precision = 20, scale = 3, columnDefinition = "DECIMAL(20,3) COMMENT 'amount + transfer_fee'")
    private BigDecimal total;

    @Column(name = "document_cash_out_name")
    private String documentCashOutName;

    @Column(name = "created_tm", nullable = true, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTm;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "modified_tm", nullable = true, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTm;

    @Column(name = "modified_by", length = 255)
    private String modifiedBy;

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

