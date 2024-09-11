package com.fision.service;

import com.fision.dto.ARInvoiceRequestDto;
import com.fision.entity.TbArInvoice;

/**
 * @author LordDev
 */
public interface ARInvoiceService {
    void saveArInvoice(String username, ARInvoiceRequestDto arInvoiceRequestDto);
    void editArInvoice(String username, TbArInvoice tbArInvoice, ARInvoiceRequestDto arInvoiceRequestDto);
}
