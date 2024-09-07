package com.fision.serviceImpl;

import com.fision.dto.VendorRequestDto;
import com.fision.entity.TbVendor;
import com.fision.repository.TbVendorRepository;
import com.fision.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class VendorServiceImpl implements VendorService {
    @Autowired
    TbVendorRepository tbVendorRepository;

    @Override
    public Page<TbVendor> getVendorListPaging(int pageNo, int pageSize, String sortBy, String sortOrder, String vendorName, String bankName, String bankAccount, String bankAccountName) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return tbVendorRepository.getVendorListPaging(vendorName, bankName, bankAccount, bankAccountName, pageable);
    }

    @Override
    public TbVendor getVendorByVendorName(String vendorName) {
        return tbVendorRepository.findByVendorName(vendorName).get();
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
        tbVendor.setVendorName(vendorRequestDto.getVendorName());
        tbVendor.setBankAccount(vendorRequestDto.getBankAccount());
        tbVendor.setBankName(vendorRequestDto.getBankName());
        tbVendor.setBankAccountName(vendorRequestDto.getBankAccountName());
        tbVendor.setBankCode(vendorRequestDto.getBankCode());
        tbVendor.setModifiedBy(username);
        tbVendorRepository.save(tbVendor);
    }

    @Override
    public boolean vendorDataCheck(VendorRequestDto vendorRequestDto, TbVendor tbVendor) {
        if (tbVendor != null &&  tbVendor.getVendorName().equalsIgnoreCase(vendorRequestDto.getVendorName())) { //Update
            return false;
        }
        return tbVendorRepository.findByVendorName(vendorRequestDto.getVendorName()).isPresent();
    }

    @Override
    public List<TbVendor> getVendorList(String vendorName) {
        return tbVendorRepository.getVendorList(vendorName);
    }
}
