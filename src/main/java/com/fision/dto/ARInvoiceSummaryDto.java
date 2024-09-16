package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author LordDev
 */
@Data
public class ARInvoiceSummaryDto {
    private BigDecimal totalAmountApprove;
    private BigDecimal totalAmountNotApprove;
    private BigDecimal totalAmountRejected;
    private BigDecimal totalPaymentAmountPaid;
    private BigDecimal totalPaymentAmountUnpaid;

    public ARInvoiceSummaryDto(BigDecimal totalAmountApprove, BigDecimal totalAmountNotApprove, BigDecimal totalAmountRejected, BigDecimal totalPaymentAmountPaid, BigDecimal totalPaymentAmountUnpaid) {
        this.totalAmountApprove = totalAmountApprove;
        this.totalAmountNotApprove = totalAmountNotApprove;
        this.totalAmountRejected = totalAmountRejected;
        this.totalPaymentAmountPaid = totalPaymentAmountPaid;
        this.totalPaymentAmountUnpaid = totalPaymentAmountUnpaid;
    }
}
