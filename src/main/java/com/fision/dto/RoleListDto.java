package com.fision.dto;

import lombok.Data;

/**
 * @author LordDev
 */
@Data
public class RoleListDto {
    private String roleCode;
    private String roleName;

    public RoleListDto(String roleCode, String roleName) {
        this.roleCode = roleCode;
        this.roleName = roleName;
    }
}
