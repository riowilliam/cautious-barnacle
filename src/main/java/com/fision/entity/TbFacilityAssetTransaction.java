package com.fision.entity;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

/**
 * @author LordDev
 */

@Entity
@Data
@Table(name = "tb_facility_asset_transaction")
public class TbFacilityAssetTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_asset_transaction_id")
    private Long facilityAssetTransactionId;

    @Column(name = "vendor_name", length = 100)
    private String vendorName;

    @Column(name = "transaction_date")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Column(name = "amount", length = 100)
    private String amount;

    @Column(name = "bank_approval_date", length = 100)
    private String bankApprovalDate;

    @Column(name = "tenor_date")
    @Temporal(TemporalType.DATE)
    private Date tenorDate;

    @Column(name = "transaction_type", length = 100)
    private String transactionType;

    @Column(name = "facility_type", length = 100)
    private String facilityType;

    @Column(name = "is_tenor_date_on_weekend")
    private Boolean isTenorDateOnWeekend;

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

