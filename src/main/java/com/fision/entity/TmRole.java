package com.fision.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author LordDev
 */
@Entity
@Table(name = "tm_role")
@Data
public class TmRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "role_id")
    private Integer roleId;

    @Column (name = "role_code")
    private String roleCode;

    @Column (name = "role_name")
    private String roleName;

    @Column (name = "status")
    private Integer status;

    @Column (name = "created_by")
    private String createdBy;

    @Column (name = "created_tm")
    private Date createdTm;

    @Column (name = "modified_by")
    private String modifiedBy;

    @Column (name = "modified_tm")
    private Date modifiedTm;

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
