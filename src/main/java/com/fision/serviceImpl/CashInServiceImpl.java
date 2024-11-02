package com.fision.serviceImpl;

import com.fision.dto.*;
import com.fision.entity.TbArInvoice;
import com.fision.entity.TbCashIn;
import com.fision.repository.TbCashInRepository;
import com.fision.service.ARInvoiceService;
import com.fision.service.CashInService;
import com.fision.service.MsBalanceService;
import com.fision.utils.ConstantsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class CashInServiceImpl implements CashInService {
    @Autowired
    TbCashInRepository tbCashInRepository;

    @Autowired
    ARInvoiceService arInvoiceService;


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
    @Transactional
    public void saveCashIn(CashInRequestDto cashInRequestDto, TbArInvoice arInvoice, String username) {
        TbCashIn tbCashIn = new TbCashIn();
        tbCashIn.setCashInStatus(cashInRequestDto.getCashInStatus());
        tbCashIn.setDeduction(cashInRequestDto.getDeduction());
        tbCashIn.setPaymentAmount(cashInRequestDto.getPaymentAmount());
        tbCashIn.setPaymentType(cashInRequestDto.getPaymentType());
        tbCashIn.setInvoiceNo(cashInRequestDto.getInvoiceNo());
        tbCashIn.setPaymentDate(new Date());
        tbCashIn.setCreatedBy(username);
        tbCashIn.setModifiedBy(username);
        tbCashInRepository.save(tbCashIn);

        if(cashInRequestDto.getCashInStatus().equalsIgnoreCase(ConstantsUtils.COMPLETED)) {
            if((cashInRequestDto.getPaymentAmount().add(cashInRequestDto.getDeduction())).compareTo(arInvoice.getTotalAmount()) == 0
                    && cashInRequestDto.getPaymentType() == 1) {
                arInvoice.setPaymentStatus(ConstantsUtils.FULLY_PAID);
            } else {
                arInvoice.setPaymentStatus(ConstantsUtils.PARTIALLY_PAYMENT);
            }
        }
        arInvoice.setDeduction(cashInRequestDto.getDeduction());
        arInvoice.setTotalAmount(arInvoice.getTotalAmount().subtract(cashInRequestDto.getDeduction()));
        arInvoice.setModifiedBy(username);
        arInvoiceService.save(arInvoice);
    }

    @Override
    public void save(TbCashIn tbCashIn) {
        tbCashInRepository.save(tbCashIn);
    }

    @Override
    public BigDecimal getTotalIncompletedCashIByInvoiceNo(String invoiceNo) {
        return tbCashInRepository.getTotalIncompletedCashIByInvoiceNo(invoiceNo);
    }

    @Override
    public TbCashIn getTbCashInById(Long id) {
        return tbCashInRepository.findByCashInId(id);
    }

    @Override
    public List<CashInDetailDto> getCashInListByInvoiceNo(String invoiceNo) {
        return tbCashInRepository.getCashInListByInvoiceNo(invoiceNo);
    }
}
