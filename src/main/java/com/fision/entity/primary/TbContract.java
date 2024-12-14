package com.fision.entity.primary;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author LordDev
 */

@Entity
@Data
@Table(name = "tb_contract")
public class TbContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contract_id")
    private Long contractId;

    @Column(name = "contract_no", nullable = false)
    private String contractNo;

    @Column(name = "contract_name", nullable = false)
    private String contractName;

    @Column(name = "partner_name", length = 255)
    private String partnerName;

    @Column(name = "contract_date")
    @Temporal(TemporalType.DATE)
    private Date contractDate;

    @Column(name = "end_contract_date")
    @Temporal(TemporalType.DATE)
    private Date endContractDate;

    @Column(name = "addendum_date")
    @Temporal(TemporalType.DATE)
    private Date addendumDate;

    @Column(name = "revision", nullable = false)
    private int revision;

    @Column(name = "created_tm", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
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

