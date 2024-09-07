package com.fision.dto;

import lombok.Data;

/**
 * @author LordDev
 */
@Data
public class LoginResponseDto {
    private Boolean isValid;
    private String jwtToken;
    private String username;
    private String fullName;
    private String userRole;
}
