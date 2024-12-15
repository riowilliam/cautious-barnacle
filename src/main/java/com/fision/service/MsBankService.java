package com.fision.service;

import com.fision.entity.primary.MsBank;

import java.util.List;
import java.util.Map;

public interface MsBankService {
    List<Map<String, Object>> getBankList(String bankShortName, String bankName);
    MsBank getBankByName(String bankName);
}
