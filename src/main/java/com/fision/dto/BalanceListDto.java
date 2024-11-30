package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author LordDev
 */
@Data
public class BalanceListDto {
    private String bankName;
    private String bankAccount;
    private String bankAccountName;
    private String bankCodeInternal;

    public BalanceListDto(String bankName, String bankAccount, String bankAccountName, String bankCodeInternal) {
        this.bankName = bankName;
        this.bankAccount = bankAccount;
        this.bankAccountName = bankAccountName;
        this.bankCodeInternal = bankCodeInternal;
    }
}
