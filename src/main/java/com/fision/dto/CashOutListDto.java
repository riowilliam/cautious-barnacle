package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CashOutListDto {
    List<CashOutDetailDto> cashOutDetailList;
    private String documentName;
    private BigDecimal subTotal;

    public CashOutListDto(List<CashOutDetailDto> cashOutDetailList, BigDecimal subTotal, String documentName) {
        this.cashOutDetailList = cashOutDetailList;
        this.subTotal = subTotal;
        this.documentName = documentName;
    }
}
