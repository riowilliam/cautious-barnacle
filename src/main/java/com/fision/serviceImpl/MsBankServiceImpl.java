package com.fision.serviceImpl;

import com.fision.entity.primary.MsBank;
import com.fision.repository.primary.MsBankRepository;
import com.fision.service.MsBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MsBankServiceImpl implements MsBankService {
    @Autowired
    MsBankRepository msBankRepository;

    @Override
    public List<Map<String, Object>> getBankList(String bankShortName, String bankName) {
        return msBankRepository.getBankList(bankShortName, bankName);
    }

    @Override
    public MsBank getBankByName(String bankName) {
        return msBankRepository.findByBankName(bankName);
    }
}
