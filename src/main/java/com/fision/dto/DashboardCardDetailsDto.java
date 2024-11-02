package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardCardDetailsDto {
    String cardTitle;
    BigDecimal totalApprovedAmountCreated;
    Integer totalApprovedCountCreated;
    Integer totalPendingCountCreated;

    public DashboardCardDetailsDto(String cardTitle, BigDecimal totalApprovedAmountCreated, Integer totalApprovedCountCreated, Integer totalPendingCountCreated) {
        this.cardTitle = cardTitle;
        this.totalApprovedAmountCreated = totalApprovedAmountCreated != null ? totalApprovedAmountCreated : BigDecimal.ZERO;
        this.totalApprovedCountCreated = totalApprovedCountCreated != null ? totalApprovedCountCreated : 0;
        this.totalPendingCountCreated = totalPendingCountCreated != null ? totalPendingCountCreated : 0;
    }
}
