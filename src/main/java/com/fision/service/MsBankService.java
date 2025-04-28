package com.fision.service;

import com.fision.dto.MsBankRequestDto;
import com.fision.entity.primary.MsBank;

import java.util.List;
import java.util.Map;

public interface MsBankService {
    List<Map<String, Object>> getBankList(String bankShortName, String bankName);
    MsBank getBankByName(String bankName);
    MsBank getBankById(Long id);
    void saveMsBank(String username, MsBankRequestDto requestDto);
    void updateMsBank(String username, MsBankRequestDto requestDto, MsBank msBank);
}
