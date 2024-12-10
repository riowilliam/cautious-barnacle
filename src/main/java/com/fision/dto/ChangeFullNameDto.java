package com.fision.dto;

import lombok.Data;

@Data
public class ChangeFullNameDto {
    private String username;
    private String newFullName;
    private String password;
}
