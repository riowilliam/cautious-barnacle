package com.fision.serviceImpl;

import com.fision.dto.*;
import com.fision.repository.TbCashInRepository;
import com.fision.service.CashInService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
public class CashInServiceImpl implements CashInService {
    @Autowired
    TbCashInRepository tbCashInRepository;


    @Override
    public Page<CashInDetailDto> getCashInDetailsPaging(int pageNo, int pageSize, String sortBy, String sortOrder, String partnerName, String projectName, Integer paymentType, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return tbCashInRepository.getCashInPaging(partnerName, projectName, paymentType, startDate, endDate, pageable);
    }

    @Override
    public Page<CashInListDto> getCashInListPaging(int pageNo, int pageSize, String sortBy, String sortOrder, String partnerName, String projectName, Integer paymentType, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        Page<CashInDetailDto> cashInDetailDtoPage = tbCashInRepository.getCashInPaging(partnerName, projectName, paymentType, startDate, endDate, pageable);
        CashInSummaryDto cashInSummaryDto = tbCashInRepository.getSummaryCashIn(partnerName, projectName, paymentType, startDate, endDate);

        CashInListDto cashInList = new CashInListDto(cashInDetailDtoPage.getContent(), cashInSummaryDto);
        return new PageImpl<>(Collections.singletonList(cashInList), pageable, cashInDetailDtoPage.getTotalElements());
    }

    @Override
    public void saveCashIn(CashInRequestDto cashInRequestDto, String username) {

    }
}
