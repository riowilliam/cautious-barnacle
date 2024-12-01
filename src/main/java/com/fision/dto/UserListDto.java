package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author LordDev
 */

@Data
public class UserListDto {
    private String username;
    private String fullName;
    private String email;
    private String roleName;
    private String contact;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date createdDate;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date modifiedDate;
    private String modifiedBy;

    public UserListDto(String username, String fullName, String email, String roleName, String contact, Date createdDate, String createdBy, Date modifiedDate, String modifiedBy) {
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.roleName = roleName;
        this.contact = contact;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
    }
}
