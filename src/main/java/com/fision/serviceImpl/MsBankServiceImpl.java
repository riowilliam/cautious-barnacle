package com.fision.serviceImpl;

import com.fision.dto.MsBankRequestDto;
import com.fision.entity.primary.MsBank;
import com.fision.repository.primary.MsBankRepository;
import com.fision.service.MsBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
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

    @Override
    public MsBank getBankById(Long id) {
        return msBankRepository.findById(id).get();
    }

    @Override
    public Page<MsBank> getBankListPaging(String bankName, Pageable pageable) {
        return msBankRepository.getMsBankListPaging(bankName, pageable);
    }

    @Override
    public void saveMsBank(String username, MsBankRequestDto requestDto) {
        MsBank bank = new MsBank();
        bank.setBankName(requestDto.getBankName());
        bank.setBankShortName(requestDto.getBankShortName());
        bank.setBankCode(requestDto.getBankCode());
        bank.setCreatedBy(username);
        bank.setModifiedBy(username);
        msBankRepository.save(bank);
    }

    @Override
    public void updateMsBank(String username, MsBankRequestDto bankRequestDto, MsBank msBank) {
        msBank.setBankName(bankRequestDto.getBankName());
        msBank.setBankShortName(bankRequestDto.getBankShortName());
        msBank.setBankCode(bankRequestDto.getBankCode());
        msBank.setCreatedBy(username);
        msBank.setModifiedBy(username);
        msBankRepository.save(msBank);
    }
}
