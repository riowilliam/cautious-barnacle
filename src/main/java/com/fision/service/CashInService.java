package com.fision.service;

import com.fision.dto.CashInDetailDto;
import com.fision.dto.CashInListDto;
import com.fision.dto.CashInRequestDto;
import com.fision.entity.primary.TbArInvoice;
import com.fision.entity.primary.TbCashIn;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface CashInService {
    Page<CashInDetailDto> getCashInDetailsPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                                   String partnerName, String projectName, Integer paymentType, Date startDate, Date endDate);
    Page<CashInListDto> getCashInListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                            String partnerName, String projectName, Integer paymentType, Date startDate, Date endDate);
    void saveCashIn(CashInRequestDto cashInRequestDto, TbArInvoice arInvoice, String username);
    void save(TbCashIn tbCashIn);
    BigDecimal getTotalIncompletedCashIByInvoiceNo(String invoiceNo);
    BigDecimal getTotalCompletedCashIByInvoiceNo(String invoiceNo);
    TbCashIn getTbCashInById(Long id);
    List<CashInDetailDto> getCashInListByInvoiceNo(String invoiceNo);
}
