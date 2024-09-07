package com.fision.dto;

import lombok.Data;

/**
 * @author LordDev
 */
@Data
public class ChangeUsernameRequestDto {
    private String oldUsername;
    private String newUsername;
    private String password;
    private String fullName;
}
