package com.fision.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author LordDev
 */
@Data
public class StatisticsDto {
    List<StatisticsDetailsDto> statisticsDetailsDtoList;
    BigDecimal totalOverallCashIn;
    BigDecimal totalOverallCashOut;
    BigDecimal startingBalance;
    BigDecimal endingBalance;
    List<DashboardCardDetailsDto> cardDetails;
    List<BalanceSummaryDetails> balanceSummaryDetails;
}
