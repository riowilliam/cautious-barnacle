package com.fision.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@Data
public class FacilityListDto {
    List<FacilityTransactionDto> facilityTransactionList;
    Map<String, Object> facilitySummary;

    public FacilityListDto(List<FacilityTransactionDto> facilityTransactionList, Map<String, Object> facilitySummary) {
        this.facilityTransactionList = facilityTransactionList;
        this.facilitySummary = facilitySummary;
    }
}
