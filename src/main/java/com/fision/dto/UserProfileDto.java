package com.fision.dto;

import lombok.Data;

@Data
public class UserProfileDto {
    private String username;
    private String fullName;
    private String email;
    private String contact;
    private String roleCode;
}
