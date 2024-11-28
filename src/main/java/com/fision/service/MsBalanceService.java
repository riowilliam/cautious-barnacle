package com.fision.service;

import com.fision.dto.BalanceListDto;
import com.fision.entity.primary.MsBalance;

import java.util.List;

/**
 * @author LordDev
 */
public interface MsBalanceService {
    void save(MsBalance msBalance);
    List<BalanceListDto> getBalanceList(String bankName);
    String getBankDescFromBalance(String bankCode);
    MsBalance getMsBalanceByBankCode(String bankCode);
}
