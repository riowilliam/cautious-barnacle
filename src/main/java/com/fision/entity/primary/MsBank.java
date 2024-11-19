package com.fision.entity.primary;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ms_bank")
@Data
public class MsBank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ms_bank_id")
    private Long msBankId;

    @Column(name = "bank_name", nullable = false, length = 250)
    private String bankName;

    @Column(name = "bank_short_name", nullable = false)
    private String bankShortName;

    @Column(name = "bank_code", nullable = false)
    private String bankCode;

    @Column(name = "created_tm", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTm;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "modified_tm", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedTm;

    @Column(name = "modified_by", nullable = false)
    private String modifiedBy;

}
