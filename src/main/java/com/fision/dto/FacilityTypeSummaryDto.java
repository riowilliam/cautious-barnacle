package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FacilityTypeSummaryDto {
    private String facilityType;
    private BigDecimal sumPaymentAmount;

    public FacilityTypeSummaryDto(String facilityType, BigDecimal sumPaymentAmount) {
        this.facilityType = facilityType;
        this.sumPaymentAmount = sumPaymentAmount;
    }
}
