package com.fision.dto;

import lombok.Data;

/**
 * @author LordDev
 */
@Data
public class ChangePasswordRequestDto {
    private String username;
    private String newPassword;
    private String oldPassword;
}
