package com.fision.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "tb_document_cash_out")
@Data
public class TbDocumentCashOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_cash_out_id")
    private Long documentCashOutId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Column(name = "total_amount", nullable = false, precision = 20, scale = 3)
    private BigDecimal totalAmount;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "created_tm", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTm;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_tm", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTm;

    @Column(name = "modified_by")
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

