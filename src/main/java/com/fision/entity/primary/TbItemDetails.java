package com.fision.entity.primary;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author LordDev
 */

@Entity
@Data
@Table(name = "tb_item_details")
public class TbItemDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_details_id")
    private Long itemDetailsId;

    @Column(name = "contract_code")
    private String contractCode;

    @Column(name = "item_name")
    private String itemName;

    @Column(name = "total_quantity")
    private Integer totalQuantity;

    @Column(name = "remaining_quantity")
    private Integer remainingQuantity;

    @Column(name = "revision", nullable = false)
    private int revision;

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

