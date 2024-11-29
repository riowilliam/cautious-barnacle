package com.fision.entity.primary;
import com.fision.utils.DateTimeHelper;
import lombok.Data;
import org.exolab.castor.types.DateTime;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;
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

    @Column(name = "vendor_name")
    private String vendorName;

    @Column(name = "transaction_date")
    @Temporal(TemporalType.DATE)
    private Date transactionDate;

    @Column(name = "amount", precision = 20, scale = 3)
    private BigDecimal amount;

    @Column(name = "bank_approval_date")
    @Temporal(TemporalType.DATE)
    private Date bankApprovalDate;

    @Column(name = "tenor_date")
    @Temporal(TemporalType.DATE)
    private Date tenorDate;

    @Column(name = "calculate_date")
    @Temporal(TemporalType.DATE)
    private Date calculateDate;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "facility_type")
    private String facilityType;

    @Column(name = "is_tenor_date_on_weekend")
    private Boolean isTenorDateOnWeekend;

    @Column(name = "created_tm", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTm;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_tm", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTm;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "added_to_cash_out")
    private Boolean isAddedToCashOut;

    @Column(name = "tenor_date_config")
    private Integer tenorDateConfig;

    @Column(name = "project_name")
    private String projectName;

    // Method to check if a date falls on a weekend (Saturday or Sunday)
    private Boolean isWeekend(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY);
    }

    // Set timestamps and check if tenorDate is on a weekend before persisting
    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        this.createdTm = now;
        this.modifiedTm = now;
        this.isAddedToCashOut = false;

        // Set isTenorDateOnWeekend based on tenorDate
        if (this.tenorDate != null) {
            this.isTenorDateOnWeekend = isWeekend(this.tenorDate);
            this.calculateDate = DateTimeHelper.adjustToNextMondayIfWeekend(this.tenorDate);
        }
    }

    // Update modified timestamp and check if tenorDate is on a weekend before updating
    @PreUpdate
    protected void onUpdate() {
        this.modifiedTm = new Date();
    }
}
