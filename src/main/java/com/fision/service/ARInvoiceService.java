package com.fision.service;

import com.fision.dto.ARInvoiceListDto;
import com.fision.dto.ARInvoiceRequestDto;
import com.fision.dto.CashOutMutationListDto;
import com.fision.entity.TbArInvoice;
import org.springframework.data.domain.Page;

import java.util.Date;

/**
 * @author LordDev
 */
public interface ARInvoiceService {
    void saveArInvoice(String username, ARInvoiceRequestDto arInvoiceRequestDto);
    void editArInvoice(String username, TbArInvoice tbArInvoice, ARInvoiceRequestDto arInvoiceRequestDto);
    void approvalInvoice(String username, Integer status, TbArInvoice arInvoice);
    TbArInvoice getInvoiceByInvoiceNo(String invoiceNo);
    Page<ARInvoiceListDto> getArInvoiceListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                                  String partnerName, String projectName, Integer invoiceStatus, Date startDate, Date endDate);
}
