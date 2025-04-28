package com.fision.service;

import com.fision.dto.MsBankRequestDto;
import com.fision.entity.primary.MsBank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface MsBankService {
    List<Map<String, Object>> getBankList(String bankShortName, String bankName);
    MsBank getBankByName(String bankName);
    MsBank getBankById(Long id);
    Page<MsBank> getBankListPaging(String bankName, Pageable pageable);
    void saveMsBank(String username, MsBankRequestDto requestDto);
    void updateMsBank(String username, MsBankRequestDto requestDto, MsBank msBank);
}
