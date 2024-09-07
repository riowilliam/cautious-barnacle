package com.fision.service;

import com.fision.dto.ProjectListDto;
import com.fision.dto.VendorRequestDto;
import com.fision.entity.TbVendor;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface VendorService {
    Page<TbVendor> getVendorListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                             String vendorName, String bankName, String bankAccount, String bankAccountName);
    TbVendor getVendorByVendorName(String vendorName);
    TbVendor getVendorById(Long id);

    void saveVendor(String username, VendorRequestDto vendorRequestDto);
    void updateVendor(String username, TbVendor tbVendor, VendorRequestDto vendorRequestDto);

    boolean vendorDataCheck(VendorRequestDto vendorRequestDto, TbVendor tbVendor);
    List<TbVendor> getVendorList(String vendorName);
}
