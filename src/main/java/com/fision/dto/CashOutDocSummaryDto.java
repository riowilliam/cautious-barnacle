package com.fision.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CashOutDocSummaryDto {
    private BigDecimal totalAmountApprove;
    private BigDecimal totalAmountNotApprove;
    private BigDecimal totalAmountRejected;
    private Integer totalCountApprove;
    private Integer totalCountNotApprove;
    private Integer totalCountRejected;

    public CashOutDocSummaryDto(BigDecimal totalAmountApprove, BigDecimal totalAmountNotApprove, BigDecimal totalAmountRejected, Integer totalCountApprove, Integer totalCountNotApprove, Integer totalCountRejected) {
        this.totalAmountApprove = totalAmountApprove;
        this.totalAmountNotApprove = totalAmountNotApprove;
        this.totalAmountRejected = totalAmountRejected;
        this.totalCountApprove = totalCountApprove;
        this.totalCountNotApprove = totalCountNotApprove;
        this.totalCountRejected = totalCountRejected;
    }
}
