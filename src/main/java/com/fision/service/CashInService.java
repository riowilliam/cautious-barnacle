package com.fision.service;

import com.fision.dto.ARInvoiceRequestDto;

public interface CashInService {
    void saveArInvoice(String username, ARInvoiceRequestDto arInvoiceRequestDto);
}
