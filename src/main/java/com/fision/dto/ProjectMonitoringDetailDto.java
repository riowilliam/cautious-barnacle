package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author LordDev
 */
@Data
public class ProjectMonitoringDetailDto {
    String projectName;
    BigDecimal cashInValue;
    BigDecimal cashOutValue;

    public ProjectMonitoringDetailDto(String projectName, BigDecimal cashInValue, BigDecimal cashOutValue) {
        this.projectName = projectName;
        this.cashInValue = cashInValue;
        this.cashOutValue = cashOutValue;
    }
}
