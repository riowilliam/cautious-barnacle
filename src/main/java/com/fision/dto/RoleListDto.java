package com.fision.dto;

import lombok.Data;

/**
 * @author LordDev
 */
@Data
public class RoleListDto {
    private String roleCode;
    private String roleName;
    private String[] permissions;

    public RoleListDto(String roleCode, String roleName, String permissions) {
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.permissions = permissions.split(",");
    }
}
