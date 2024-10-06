package com.fision.service;

import com.fision.dto.CashInDetailDto;
import com.fision.dto.CashInListDto;
import com.fision.dto.CashInRequestDto;
import org.springframework.data.domain.Page;

import java.util.Date;

public interface CashInService {
   Page<CashInDetailDto> getCashInDetailsPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                                   String partnerName, String projectName, Integer paymentType, Date startDate, Date endDate);
    Page<CashInListDto> getCashInListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                            String partnerName, String projectName, Integer paymentType, Date startDate, Date endDate);
    void saveCashIn(CashInRequestDto cashInRequestDto, String username);
}
