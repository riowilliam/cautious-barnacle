package com.fision.entity;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author LordDev
 */

@Entity
@Data
@Table(name = "tb_facility_balance", uniqueConstraints = {@UniqueConstraint(name = "tb_facility_balance_unique", columnNames = "facility_type")})
public class TbFacilityBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_balance_id")
    private Long facilityBalanceId;

    @Column(name = "facility_type", nullable = false)
    private String facilityType;

    @Column(name = "amount", precision = 20, scale = 3)
    private BigDecimal amount;

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

