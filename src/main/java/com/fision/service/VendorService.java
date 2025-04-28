package com.fision.service;

import com.fision.dto.VendorRequestDto;
import com.fision.entity.primary.TbVendor;
import org.springframework.data.domain.Page;

import java.util.List;

public interface VendorService {
    Page<TbVendor> getVendorListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                             String vendorName, String bankName, String bankAccount, String bankAccountName);
    TbVendor getVendorByVendorName(String vendorName);
    TbVendor getVendorById(Long id);

    void saveVendor(String username, VendorRequestDto vendorRequestDto);
    void updateVendor(String username, TbVendor tbVendor, VendorRequestDto vendorRequestDto);

    boolean vendorDataCheck(VendorRequestDto vendorRequestDto);
    boolean vendorDataCheckForUpdate(VendorRequestDto vendorRequestDto, TbVendor tbVendor);
    List<TbVendor> getVendorList(String vendorName);
}
