package com.fision.dto;

import lombok.Data;

@Data
public class VendorRequestDto {
    private Long vendorId;
    private String vendorName;
    private String bankName;
    private String bankAccount;
    private String bankAccountName;
    private String bankCode;
}
