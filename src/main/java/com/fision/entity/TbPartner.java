package com.fision.entity;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author LordDev
 */

@Entity
@Data
@Table(name = "tb_partner")
public class TbPartner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "partner_name", length = 255)
    private String partnerName;

    @Column(name = "valid_contract_date")
    @Temporal(TemporalType.DATE)
    private Date validContractDate;

    @Column(name = "invalid_contract_date")
    @Temporal(TemporalType.DATE)
    private Date invalidContractDate;

    @Column(name = "active_project")
    private String activeProject;

    @Column(name = "is_ppn_wapu", nullable = false)
    private Integer isPpnWapu;

    @Column(name = "document_tracking", nullable = false)
    private Integer documentTracking;

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

