package com.fision.serviceImpl;

import com.fision.dto.*;
import com.fision.entity.primary.TbArInvoice;
import com.fision.entity.primary.TbCashIn;
import com.fision.repository.primary.TbCashInRepository;
import com.fision.service.ARInvoiceService;
import com.fision.service.CashInService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
        tbCashIn.setInterestDeduction(cashInRequestDto.getInterestDeduction());
        tbCashIn.setOtherDeduction(cashInRequestDto.getOtherDeduction());
        tbCashIn.setPaymentAmount(cashInRequestDto.getPaymentAmount());
        tbCashIn.setPaymentType(cashInRequestDto.getPaymentType());
        tbCashIn.setInvoiceNo(cashInRequestDto.getInvoiceNo());
        tbCashIn.setPaymentDate(new Date());
        tbCashIn.setCreatedBy(username);
        tbCashIn.setModifiedBy(username);
        tbCashIn.setPaymentBankCode(cashInRequestDto.getPaymentBankCode());
        BigDecimal totalCompleted = getTotalCompletedCashIByInvoiceNo(arInvoice.getInvoiceNo());
        Integer countCashIn = getCountCashInByInvoice(arInvoice.getInvoiceNo());
        tbCashIn.setPaymentProgressNum(countCashIn + 1);

        if(cashInRequestDto.getCashInStatus().equalsIgnoreCase(ConstantsUtils.COMPLETED)) {
            if((cashInRequestDto.getPaymentAmount().add(cashInRequestDto.getInterestDeduction().add(cashInRequestDto.getOtherDeduction())).add(totalCompleted)).compareTo(arInvoice.getTotalAmount()) == 0
                    && cashInRequestDto.getPaymentType() == 1) {
                arInvoice.setPaymentStatus(ConstantsUtils.FULLY_PAID);
            } else {
                arInvoice.setPaymentStatus(ConstantsUtils.PARTIALLY_PAID);
            }
        }

        arInvoice.setDeduction(arInvoice.getDeduction() != null ? arInvoice.getDeduction().add(cashInRequestDto.getInterestDeduction().add(cashInRequestDto.getOtherDeduction())) : cashInRequestDto.getInterestDeduction().add(cashInRequestDto.getOtherDeduction()));
        arInvoice.setTotalAmount(arInvoice.getTotalAmount().subtract(cashInRequestDto.getInterestDeduction().add(cashInRequestDto.getOtherDeduction())));
        arInvoice.setModifiedBy(username);
        tbCashInRepository.save(tbCashIn);
        arInvoiceService.save(arInvoice);
    }

    @Override
    @Transactional
    public void saveCashInWithoutInvoiceAndContract(CashInRequestDto cashInRequestDto, String username) {
        String invoiceNo = ConstantsUtils.NO_INVOICE_PREFIX + DateTimeHelper.nowToString();
        TbCashIn tbCashIn = new TbCashIn();
        tbCashIn.setInvoiceNo(invoiceNo);
        tbCashIn.setCashInStatus(cashInRequestDto.getCashInStatus());
        tbCashIn.setInterestDeduction(cashInRequestDto.getInterestDeduction());
        tbCashIn.setOtherDeduction(cashInRequestDto.getOtherDeduction());
        tbCashIn.setPaymentAmount(cashInRequestDto.getPaymentAmount());
        tbCashIn.setPaymentType(cashInRequestDto.getPaymentType());
        tbCashIn.setPaymentDate(new Date());
        tbCashIn.setCreatedBy(username);
        tbCashIn.setModifiedBy(username);
        tbCashIn.setPaymentBankCode(cashInRequestDto.getPaymentBankCode());
        save(tbCashIn);

        TbArInvoice tbArInvoice = new TbArInvoice();
        tbArInvoice.setInvoiceNo(invoiceNo);
        tbArInvoice.setPartnerName(cashInRequestDto.getPartnerName());
        tbArInvoice.setDeduction(cashInRequestDto.getInterestDeduction().add(cashInRequestDto.getOtherDeduction()));
        tbArInvoice.setProjectName(cashInRequestDto.getProjectName());
        tbArInvoice.setTotalAmount(cashInRequestDto.getPaymentAmount());
        arInvoiceService.save(tbArInvoice);
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
    public BigDecimal getTotalCompletedCashIByInvoiceNo(String invoiceNo) {
        return tbCashInRepository.getTotalCompletedCashIByInvoiceNo(invoiceNo);
    }

    @Override
    public TbCashIn getTbCashInById(Long id) {
        return tbCashInRepository.findByCashInId(id);
    }

    @Override
    public List<CashInDetailDto> getCashInListByInvoiceNo(String invoiceNo) {
        return tbCashInRepository.getCashInListByInvoiceNo(invoiceNo);
    }

    @Override
    public CashInAmountsDto getCashInAmounts(String invoiceNo, int progressNum) {
        return tbCashInRepository.getCashInAmounts(invoiceNo, progressNum);
    }

    @Override
    public Integer getCountCashInByInvoice(String invoiceNo) {
        return tbCashInRepository.getCountCashInByInvoiceNo(invoiceNo);
    }
}
