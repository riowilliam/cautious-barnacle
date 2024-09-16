package com.fision.dto;

import lombok.Data;

import java.util.List;

/**
 * @author LordDev
 */
@Data
public class ARInvoiceListDto {
    List<ARInvoiceDetailDto> arInvoiceDetailList;
    ARInvoiceSummaryDto arInvoiceSummaryDto;

    public ARInvoiceListDto(List<ARInvoiceDetailDto> arInvoiceDetailList, ARInvoiceSummaryDto arInvoiceSummaryDto) {
        this.arInvoiceDetailList = arInvoiceDetailList;
        this.arInvoiceSummaryDto = arInvoiceSummaryDto;
    }
}
