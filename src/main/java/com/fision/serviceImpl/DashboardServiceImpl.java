package com.fision.serviceImpl;

import com.fision.dto.BalanceSummaryDetails;
import com.fision.dto.DashboardCardDetailsDto;
import com.fision.dto.StatisticsDetailsDto;
import com.fision.dto.StatisticsDto;
import com.fision.repository.primary.MsBalanceRepository;
import com.fision.repository.primary.TbArInvoiceRepository;
import com.fision.repository.primary.TbCashInRepository;
import com.fision.repository.primary.TbDocumentCashOutRepository;
import com.fision.service.DashboardService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LordDev
 */
@Service
public class DashboardServiceImpl implements DashboardService {
    @Autowired
    MsBalanceRepository msBalanceRepository;

    @Autowired
    TbArInvoiceRepository tbArInvoiceRepository;

    @Autowired
    TbCashInRepository tbCashInRepository;

    @Autowired
    TbDocumentCashOutRepository tbDocumentCashOutRepository;

    @Override
    public StatisticsDto getDashboardStats(String filterType, Date startDate, Date endDate) {
        StatisticsDetailsDto statsDetails = null;
        List<StatisticsDetailsDto> detailsList = null;
        List<Map<String, Object>> results;
        BigDecimal totalOverallCashIn = BigDecimal.ZERO;
        BigDecimal totalOverallCashOut = BigDecimal.ZERO;
        BigDecimal startCashIn = msBalanceRepository.getTotalCashInBeforeDate(startDate);
        BigDecimal startCashOut = msBalanceRepository.getTotalCashOutBeforeDate(startDate);
        BigDecimal startCashDifference = startCashIn.subtract(startCashOut);
        BigDecimal currentCashDifference;
        BigDecimal startingBalance = msBalanceRepository.getTotalBalanceInitBalance(ConstantsUtils.INIT_BALANCE);
        BigDecimal endingBalance;
        Date addOneDay;
        switch (filterType) {
            case ConstantsUtils.DAILY:
                addOneDay = DateTimeHelper.addOneDay(endDate);
                BigDecimal cashInToday = msBalanceRepository.getTotalCashInToday(startDate, addOneDay);
                BigDecimal cashOutToday = msBalanceRepository.getTotalCashOutToday(startDate, addOneDay);
                statsDetails = new StatisticsDetailsDto(ConstantsUtils.DAILY, cashInToday, cashOutToday);
                detailsList = new ArrayList<>();
                detailsList.add(statsDetails);
                totalOverallCashIn = statsDetails.getTotalCashIn();
                totalOverallCashOut = statsDetails.getTotalCashOut();
                break;
            case ConstantsUtils.WEEKLY:
                addOneDay = DateTimeHelper.addOneDay(new Date());
                results = msBalanceRepository.getWeeklyStats(startDate);
                detailsList = results.stream()
                        .map(result -> new StatisticsDetailsDto(
                                (String) result.get("statsHeader"),
                                (BigDecimal) result.get("totalCashIn"),
                                (BigDecimal) result.get("totalCashOut")
                        ))
                        .collect(Collectors.toList());

                totalOverallCashIn = detailsList.stream()
                        .map(StatisticsDetailsDto::getTotalCashIn)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalOverallCashOut = detailsList.stream()
                        .map(StatisticsDetailsDto::getTotalCashOut)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            case ConstantsUtils.MONTHLY:
                addOneDay = DateTimeHelper.addOneDay(new Date());
                results = msBalanceRepository.getMonthlyStats(startDate);
                detailsList = results.stream()
                        .map(result -> new StatisticsDetailsDto(
                                (String) result.get("statsHeader"),
                                (BigDecimal) result.get("totalCashIn"),
                                (BigDecimal) result.get("totalCashOut")
                        ))
                        .collect(Collectors.toList());

                totalOverallCashIn = detailsList.stream()
                        .map(StatisticsDetailsDto::getTotalCashIn)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalOverallCashOut = detailsList.stream()
                        .map(StatisticsDetailsDto::getTotalCashOut)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            case ConstantsUtils.YEARLY:
                addOneDay = DateTimeHelper.addOneDay(new Date());
                results = msBalanceRepository.getYearlyStats(DateTimeHelper.getYearFromDate(endDate));
                detailsList = results.stream()
                        .map(result -> new StatisticsDetailsDto(
                                (String) result.get("statsHeader"),
                                (BigDecimal) result.get("totalCashIn"),
                                (BigDecimal) result.get("totalCashOut")
                        ))
                        .collect(Collectors.toList());

                totalOverallCashIn = detailsList.stream()
                        .map(StatisticsDetailsDto::getTotalCashIn)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                totalOverallCashOut = detailsList.stream()
                        .map(StatisticsDetailsDto::getTotalCashOut)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                break;
            default:
                addOneDay = DateTimeHelper.addOneDay(new Date());
                BigDecimal totalCashInTillToday = msBalanceRepository.getTotalCashInTillToday(addOneDay);
                BigDecimal totalCashOutTillToday = msBalanceRepository.getTotalCashOutTillToday(addOneDay);
                statsDetails = new StatisticsDetailsDto(ConstantsUtils.ALL, totalCashInTillToday, totalCashOutTillToday);
                detailsList = new ArrayList<>();
                detailsList.add(statsDetails);
                totalOverallCashIn = statsDetails.getTotalCashIn();
                totalOverallCashOut = statsDetails.getTotalCashOut();
                break;
        }

        currentCashDifference = totalOverallCashIn.subtract(totalOverallCashOut);

        if (startCashDifference.compareTo(BigDecimal.ZERO) < 0) {
            startingBalance = startingBalance.subtract(startCashDifference.abs());
        } else {
            startingBalance = startingBalance.add(startCashDifference);
        }

        if (currentCashDifference.compareTo(BigDecimal.ZERO) < 0) {
            endingBalance = startingBalance.subtract(currentCashDifference.abs());
        } else {
            endingBalance = startingBalance.add(currentCashDifference);
        }

        // Build CardDetailsList
        DashboardCardDetailsDto arInvoiceSummary = tbArInvoiceRepository.getARInvoiceCardDetail(startDate, addOneDay);
        DashboardCardDetailsDto cashInSummary = tbCashInRepository.getCashInCardDetail(startDate, addOneDay);
        DashboardCardDetailsDto cashOutDocsSummary = tbDocumentCashOutRepository.getCashOutDocCardDetail(startDate, addOneDay);
        List<BalanceSummaryDetails> balanceSummaryDetailsList = msBalanceRepository.getBalanceSummaryDetails(startDate, endDate);

        List<DashboardCardDetailsDto> summaryList = new ArrayList<>();
        summaryList.add(arInvoiceSummary);
        summaryList.add(cashInSummary);
        summaryList.add(cashOutDocsSummary);

        StatisticsDto statisticsDto = new StatisticsDto();
        statisticsDto.setStatisticsDetailsDtoList(detailsList);
        statisticsDto.setTotalOverallCashIn(totalOverallCashIn);
        statisticsDto.setTotalOverallCashOut(totalOverallCashOut);
        statisticsDto.setStartingBalance(startingBalance);
        statisticsDto.setEndingBalance(endingBalance);
        statisticsDto.setCardDetails(summaryList);
        statisticsDto.setBalanceSummaryDetails(balanceSummaryDetailsList);

        return statisticsDto;
    }
}
