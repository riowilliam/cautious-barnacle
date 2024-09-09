package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CashOutMutationListDto {
    List<CashOutMutationDto> cashOutDetailList;
    private BigDecimal subTotal;

    public CashOutMutationListDto(List<CashOutMutationDto> cashOutDetailList, BigDecimal subTotal) {
        this.cashOutDetailList = cashOutDetailList;
        this.subTotal = subTotal;
    }
}
