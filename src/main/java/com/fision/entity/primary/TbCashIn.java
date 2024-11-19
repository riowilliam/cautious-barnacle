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
@Table(name = "tb_cash_in")
public class TbCashIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cash_in_id")
    private Long cashInId;

    @Column(name = "invoice_no", nullable = false)
    private String invoiceNo;

    @Column(name = "payment_amount", precision = 20, scale = 3)
    private BigDecimal paymentAmount;

    @Column(name = "payment_type", columnDefinition = "int COMMENT '1 = Fully Payment, 2 = Partially Payment'")
    private Integer paymentType;

    @Column(name = "deduction", precision = 20, scale = 3)
    private BigDecimal deduction;

    @Column(name = "cash_in_status", columnDefinition = "varchar(100) COMMENT 'Completed, Incompleted'")
    private String cashInStatus;

    @Column(name = "payment_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;

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

