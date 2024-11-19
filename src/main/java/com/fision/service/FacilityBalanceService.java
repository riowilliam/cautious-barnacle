package com.fision.service;

import com.fision.dto.CashInDetailDto;
import com.fision.dto.CashInListDto;
import com.fision.dto.FacilityListDto;
import com.fision.dto.FacilityTransactionDto;
import org.springframework.data.domain.Page;

import java.util.Date;

public interface FacilityBalanceService {
    Page<FacilityListDto> getFacilityTransactionPaging(String vendorName, String facilityType,
                                                       String transactionType, boolean tenorDateOnWeekend, Date startDate, Date endDate,
                                                       int pageNo, int pageSize, String sortBy, String sortOrder);
}
