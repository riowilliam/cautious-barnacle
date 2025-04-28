package com.fision.serviceImpl;

import com.fision.dto.VendorRequestDto;
import com.fision.entity.primary.MsBank;
import com.fision.entity.primary.TbCashOut;
import com.fision.entity.primary.TbVendor;
import com.fision.entity.primary.TmpCashOut;
import com.fision.repository.primary.TbCashOutRepository;
import com.fision.repository.primary.TbVendorRepository;
import com.fision.repository.primary.TmpCashOutRepository;
import com.fision.service.MsBankService;
import com.fision.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {
    @Autowired
    TbVendorRepository tbVendorRepository;

    @Autowired
    MsBankService msBankService;

    @Autowired
    TmpCashOutRepository tmpCashOutRepository;

    @Autowired
    TbCashOutRepository tbCashOutRepository;

    @Override
    public Page<TbVendor> getVendorListPaging(int pageNo, int pageSize, String sortBy, String sortOrder, String vendorName, String bankName, String bankAccount, String bankAccountName) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        String bankShortName = null;
        if (bankName != null && !bankName.isEmpty()) {
            MsBank msBank = msBankService.getBankByName(bankName);
            bankShortName = msBank.getBankShortName();
        }
        return tbVendorRepository.getVendorListPaging(vendorName, bankShortName, bankAccount, bankAccountName, pageable);
    }

    @Override
    public TbVendor getVendorByVendorName(String vendorName) {
        return tbVendorRepository.findByVendorNameIgnoreCase(vendorName);
    }

    @Override
    public TbVendor getVendorById(Long id) {
        return tbVendorRepository.findById(id).get();
    }

    @Override
    public void saveVendor(String username, VendorRequestDto vendorRequestDto) {
        TbVendor tbVendor = new TbVendor();
        tbVendor.setVendorName(vendorRequestDto.getVendorName());
        tbVendor.setBankAccount(vendorRequestDto.getBankAccount());
        tbVendor.setBankName(vendorRequestDto.getBankName());
        tbVendor.setBankAccountName(vendorRequestDto.getBankAccountName());
        tbVendor.setBankCode(vendorRequestDto.getBankCode());
        tbVendor.setCreatedBy(username);
        tbVendor.setModifiedBy(username);
        tbVendorRepository.save(tbVendor);
    }

    @Override
    public void updateVendor(String username, TbVendor tbVendor, VendorRequestDto vendorRequestDto) {
        String oldVendorName = tbVendor.getVendorName();
        tbVendor.setVendorName(vendorRequestDto.getVendorName());
        tbVendor.setBankAccount(vendorRequestDto.getBankAccount());
        tbVendor.setBankName(vendorRequestDto.getBankName());
        tbVendor.setBankAccountName(vendorRequestDto.getBankAccountName());
        tbVendor.setBankCode(vendorRequestDto.getBankCode());
        tbVendor.setModifiedBy(username);
        tbVendorRepository.save(tbVendor);

        if(!oldVendorName.equalsIgnoreCase(vendorRequestDto.getVendorName())) {
           tmpCashOutRepository.updateVendorName(oldVendorName, vendorRequestDto.getVendorName(), username);
          tbCashOutRepository.updateVendorName(oldVendorName, vendorRequestDto.getVendorName(), username);
        }
    }

    @Override
    public boolean vendorDataCheck(VendorRequestDto vendorRequestDto) {
        return tbVendorRepository.findByBankAccount(vendorRequestDto.getBankAccount()).isPresent();
    }

    @Override
    public boolean vendorDataCheckForUpdate(VendorRequestDto vendorRequestDto, TbVendor tbVendor) {
        if (tbVendor != null &&  tbVendor.getVendorName().equalsIgnoreCase(vendorRequestDto.getVendorName())) { //Update
            return false;
        } else if(vendorRequestDto.getBankAccount().equalsIgnoreCase(tbVendor.getBankAccount())){
            return false;
        }
        return tbVendorRepository.findByBankAccount(vendorRequestDto.getBankAccount()).isPresent();
    }

    @Override
    public List<TbVendor> getVendorList(String vendorName) {
        return tbVendorRepository.getVendorList(vendorName);
    }
}
