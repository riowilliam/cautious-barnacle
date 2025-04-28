package com.fision.serviceImpl;

import com.fision.dto.*;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
                addOneDay = DateTimeHelper.getDayAfterLastDayOfWeek(startDate);
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
                addOneDay = DateTimeHelper.getDayAfterLastDayOfMonth(startDate);
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

        startDate = startDate == null ? DateTimeHelper.convertLocalDateToDate(LocalDate.now().atStartOfDay()) : startDate;

        // Build CardDetailsList
        DashboardCardDetailsDto arInvoiceSummary = tbArInvoiceRepository.getARInvoiceCardDetail(startDate, addOneDay);
        DashboardCardDetailsDto cashInSummary = tbCashInRepository.getCashInCardDetail(startDate, addOneDay);
        DashboardCardDetailsDto cashOutDocsSummary = tbDocumentCashOutRepository.getCashOutDocCardDetail(startDate, addOneDay);
        List<BalanceSummaryDetails> balanceSummaryDetailsList = getBalanceSummary(startDate, addOneDay);

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

    private List<BalanceSummaryDetails> getBalanceSummary(Date startDate, Date endDate) {
        List<BalanceCashInProjection> cashInList = msBalanceRepository.getCashInByBank(startDate, endDate);
        List<BalanceCashOutProjection> cashOutList = msBalanceRepository.getCashOutByBank(startDate, endDate);

        Map<String, BigDecimal> cashInMap = cashInList.stream()
                .collect(Collectors.toMap(BalanceCashInProjection::getBankName, BalanceCashInProjection::getTotalCashInValue));

        Map<String, BigDecimal> cashOutMap = cashOutList.stream()
                .collect(Collectors.toMap(BalanceCashOutProjection::getBankName, BalanceCashOutProjection::getTotalCashOutValue));

        Map<String, BigDecimal> balanceMap = cashInList.stream()
                .collect(Collectors.toMap(BalanceCashInProjection::getBankName, BalanceCashInProjection::getTotalBalance));

        // Combine
        Set<String> allBankNames = new HashSet<>();
        allBankNames.addAll(cashInMap.keySet());
        allBankNames.addAll(cashOutMap.keySet());

        List<BalanceSummaryDetails> result = new ArrayList<>();
        for (String bankName : allBankNames) {
            BigDecimal cashIn = cashInMap.getOrDefault(bankName, BigDecimal.ZERO);
            BigDecimal cashOut = cashOutMap.getOrDefault(bankName, BigDecimal.ZERO);
            BigDecimal balance = balanceMap.get(bankName).add(cashIn).subtract(cashOut);

            result.add(new BalanceSummaryDetails(bankName, cashIn, cashOut, balance));
        }

        return result;
    }
}
