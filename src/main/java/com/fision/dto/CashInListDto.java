package com.fision.dto;

import lombok.Data;

import java.util.List;
@Data
public class CashInListDto {
    List<CashInDetailDto> cashInDetailList;
    CashInSummaryDto cashInSummary;

    public CashInListDto(List<CashInDetailDto> cashInDetailList, CashInSummaryDto cashInSummary) {
        this.cashInDetailList = cashInDetailList;
        this.cashInSummary = cashInSummary;
    }
}
