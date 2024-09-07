package com.fision.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * @author LordDev
 */
@Entity
@Table(name = "tb_user")
@Data
public class TbUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "password")
    private String password;

    @Column(name = "role_code")
    private String roleCode;

    @Column(name = "contact")
    private String contact;

    @Column(name = "created_tm", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTm;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_tm")
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
