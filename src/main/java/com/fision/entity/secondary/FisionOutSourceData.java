package com.fision.entity.secondary;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "fision_data")
public class FisionOutSourceData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "bank_approval_date")
    private Date bankApprovalDate;

    @Column(name = "tenor_date")
    private Date tenorDate;

    @Column(name = "facility_type")
    private String facilityType;

    @Column(name = "status")
    private Integer status;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "debit_advice")
    private String debitAdvice;
}
