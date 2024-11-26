package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CashOutListDto {
    List<CashOutDetailDto> cashOutDetailList;
    private String documentName;
    private BigDecimal subTotal;
    private String bankCode;

    public CashOutListDto(List<CashOutDetailDto> cashOutDetailList, BigDecimal subTotal, String documentName,  String bankCode) {
        this.cashOutDetailList = cashOutDetailList;
        this.documentName = documentName;
        this.subTotal = subTotal;
        this.bankCode = bankCode;
    }
}
