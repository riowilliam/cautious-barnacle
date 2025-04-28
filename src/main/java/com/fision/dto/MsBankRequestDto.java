package com.fision.dto;

import lombok.Data;

@Data
public class MsBankRequestDto {
    private Long id;
    private String bankName;
    private String bankShortName;
    private String bankCode;
}
