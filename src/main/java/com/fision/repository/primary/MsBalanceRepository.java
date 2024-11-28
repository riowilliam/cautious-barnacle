package com.fision.repository.primary;

import com.fision.dto.BalanceListDto;
import com.fision.dto.BalanceSummaryDetails;
import com.fision.dto.StatisticsDetailsDto;
import com.fision.entity.primary.MsBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@Repository
public interface MsBalanceRepository extends JpaRepository<MsBalance, Long> {
    @Query("SELECT new com.fision.dto.StatisticsDetailsDto('Daily', " +
            "COALESCE(SUM(COALESCE(ci.paymentAmount, 0)), 0), " +
            "COALESCE(SUM(COALESCE(co.total, 0)), 0)) " +
            "FROM TbCashIn ci " +
            "LEFT JOIN TbCashOut co ON ci.createdTm = co.createdTm AND ci.cashInStatus = 'Completed' " +
            "WHERE ci.cashInStatus = 'Completed' AND " +
            "(:startDate IS NULL OR ci.createdTm >= :startDate) AND " +
            "(:endDate IS NULL OR ci.createdTm <= :endDate)")
    StatisticsDetailsDto getDailyStats(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "WITH cash_in_data AS ( " +
            "    SELECT DATE(created_tm) AS date, SUM(payment_amount) AS totalCashIn " +
            "    FROM tb_cash_in " +
            "    WHERE DATE(created_tm) BETWEEN DATE(:startDate) AND DATE_ADD(:startDate, INTERVAL 6 DAY) " +
            "      AND cash_in_status = 'Completed' " +
            "    GROUP BY DATE(created_tm) " +
            "), " +
            "cash_out_data AS ( " +
            "    SELECT DATE(created_tm) AS date, SUM(total) AS totalCashOut " +
            "    FROM tb_cash_out " +
            "    WHERE DATE(created_tm) BETWEEN DATE(:startDate) AND DATE_ADD(:startDate, INTERVAL 6 DAY) " +
            "    GROUP BY DATE(created_tm) " +
            ") " +
            "SELECT days.n AS dayNumber, " +
            "       DATE_FORMAT(DATE_ADD(:startDate, INTERVAL days.n DAY), '%d %b') AS statsHeader, " +
            "       COALESCE(ci.totalCashIn, 0) AS totalCashIn, " +
            "       COALESCE(co.totalCashOut, 0) AS totalCashOut " +
            "FROM ( " +
            "    SELECT 0 AS n UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 " +
            "    UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 " +
            ") AS days " +
            "LEFT JOIN cash_in_data ci ON ci.date = DATE_ADD(:startDate, INTERVAL days.n DAY) " +
            "LEFT JOIN cash_out_data co ON co.date = DATE_ADD(:startDate, INTERVAL days.n DAY) " +
            "ORDER BY days.n ASC",
            nativeQuery = true)
    List<Map<String, Object>> getWeeklyStats(@Param("startDate") Date startDate);


    @Query(value = "WITH cash_in_data AS ( " +
            "    SELECT DATE(created_tm) AS date, SUM(payment_amount) AS totalCashIn " +
            "    FROM tb_cash_in " +
            "    WHERE YEAR(created_tm) = YEAR(:startDate) " +
            "      AND MONTH(created_tm) = MONTH(:startDate) " +
            "      AND cash_in_status = 'Completed' " +
            "    GROUP BY DATE(created_tm) " +
            "), " +
            "cash_out_data AS ( " +
            "    SELECT DATE(created_tm) AS date, SUM(total) AS totalCashOut " +
            "    FROM tb_cash_out " +
            "    WHERE YEAR(created_tm) = YEAR(:startDate) " +
            "      AND MONTH(created_tm) = MONTH(:startDate) " +
            "    GROUP BY DATE(created_tm) " +
            ") " +
            "SELECT day.n AS dayNumber, " +
            "       DATE_FORMAT(DATE_ADD(:startDate, INTERVAL (day.n - 1) DAY), '%d') AS statsHeader, " +
            "       COALESCE(ci.totalCashIn, 0) AS totalCashIn, " +
            "       COALESCE(co.totalCashOut, 0) AS totalCashOut " +
            "FROM ( " +
            "    SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 " +
            "    UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 " +
            "    UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 " +
            "    UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 " +
            "    UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 " +
            "    UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30 " +
            "    UNION ALL SELECT 31 " +
            ") AS day " +
            "LEFT JOIN cash_in_data ci ON ci.date = DATE_ADD(:startDate, INTERVAL (day.n - 1) DAY) " +
            "LEFT JOIN cash_out_data co ON co.date = DATE_ADD(:startDate, INTERVAL (day.n - 1) DAY) " +
            "WHERE day.n <= DAY(LAST_DAY(:startDate)) " +
            "ORDER BY day.n",
            nativeQuery = true)
    List<Map<String, Object>> getMonthlyStats(@Param("startDate") Date startDate);

    @Query(value =
            "SELECT month.n AS monthNumber, " +
            "DATE_FORMAT(STR_TO_DATE(CONCAT(:year, '-', month.n, '-01'), '%Y-%m-%d'), '%b') AS statsHeader, " +
            "COALESCE(ci.totalCashIn, 0) AS totalCashIn, " +
            "COALESCE(co.totalCashOut, 0) AS totalCashOut " +
            "FROM " +
            "(SELECT 1 AS n UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 " +
            " UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 " +
            " UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12) AS month " +
            "LEFT JOIN " +
            "(SELECT MONTH(created_tm) AS month, SUM(payment_amount) AS totalCashIn " +
            " FROM tb_cash_in " +
            " WHERE YEAR(created_tm) = :year AND cash_in_status = 'Completed' " +
            " GROUP BY MONTH(created_tm)) ci " +
            "ON month.n = ci.month " +
            "LEFT JOIN " +
            "(SELECT MONTH(created_tm) AS month, SUM(total) AS totalCashOut " +
            " FROM tb_cash_out " +
            " WHERE YEAR(created_tm) = :year " +
            " GROUP BY MONTH(created_tm)) co " +
            "ON month.n = co.month " +
            "ORDER BY month.n",
            nativeQuery = true)
    List<Map<String, Object>> getYearlyStats(@Param("year") int year);

    @Query(value = "SELECT COALESCE(SUM(ci.payment_amount), 0) FROM tb_cash_in ci WHERE DATE(ci.created_tm) < DATE(:paramDate) AND ci.cash_in_status = 'Completed'", nativeQuery = true)
    BigDecimal getTotalCashInBeforeDate(@Param("paramDate") Date paramDate);

    @Query(value = "SELECT COALESCE(SUM(co.amount), 0) FROM tb_cash_out co WHERE DATE(co.created_tm) < DATE(:paramDate)", nativeQuery = true)
    BigDecimal getTotalCashOutBeforeDate(@Param("paramDate") Date paramDate);

    @Query(value = "SELECT COALESCE(SUM(ci.payment_amount), 0) FROM tb_cash_in ci WHERE DATE(ci.created_tm) <= DATE(:paramDate) AND ci.cash_in_status = 'Completed'", nativeQuery = true)
    BigDecimal getTotalCashInTillToday(@Param("paramDate") Date paramDate);

    @Query(value = "SELECT COALESCE(SUM(co.amount), 0) FROM tb_cash_out co WHERE DATE(co.created_tm) <= DATE(:paramDate)", nativeQuery = true)
    BigDecimal getTotalCashOutTillToday(@Param("paramDate") Date paramDate);

    @Query(value = "SELECT COALESCE(SUM(ci.paymentAmount), 0) FROM TbCashIn ci " +
            "WHERE ci.createdTm >= :startDate " +
            "AND ci.createdTm < :endDate " +
            "AND ci.cashInStatus = 'Completed' ")
    BigDecimal getTotalCashInToday(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT COALESCE(SUM(co.total), 0) FROM TbCashOut co " +
            "WHERE co.createdTm >= :startDate " +
            "AND co.createdTm < :endDate ")
    BigDecimal getTotalCashOutToday(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    MsBalance findByBankCodeInternal(String bankCodeInternal);

    @Query(value = "SELECT COALESCE(SUM(ms.balanceAmount), 0) FROM MsBalance ms WHERE ms.balanceName = :balanceName")
    BigDecimal getTotalBalanceInitBalance(@Param("balanceName") String balanceName);

    @Query("SELECT new com.fision.dto.BalanceListDto( " +
            "CASE WHEN b.bankDesc IS NOT NULL THEN CONCAT(b.bankShortName, '-', b.bankDesc) ELSE b.bankShortName END, " +
            "b.bankAccount, b.bankAccountName, b.bankCodeInternal) " +
            "FROM MsBalance b " +
            "WHERE :bankName IS NULL OR b.bankName LIKE %:bankName% " +
            "AND :bankName IS NULL OR b.bankShortName LIKE %:bankName% ")
    List<BalanceListDto> findBalanceListByBankName(@Param("bankName") String bankName);

    @Query(value = "SELECT new com.fision.dto.BalanceSummaryDetails( " +
            "   CASE WHEN b.bankDesc IS NOT NULL THEN CONCAT(b.bankShortName, '-', b.bankDesc) ELSE b.bankShortName END, " +
            "   COALESCE(SUM(COALESCE(ci.paymentAmount, 0)), 0), " +
            "   COALESCE(SUM(COALESCE(co.amount, 0)), 0), " +
            "   b.balanceAmount + COALESCE(SUM(COALESCE(ci.paymentAmount, 0)), 0) - COALESCE(SUM(COALESCE(co.amount, 0)), 0)) " +
            "FROM MsBalance b " +
            "LEFT JOIN TbCashIn ci ON b.bankCodeInternal = ci.paymentBankCode " +
            "   AND ci.createdTm >= :startDate " +
            "   AND ci.createdTm < :endDate " +
            "   AND ci.cashInStatus = 'Completed' " +
            "LEFT JOIN TbCashOut co ON b.bankCodeInternal = co.paymentBankCode " +
            "   AND co.createdTm >= :startDate " +
            "   AND co.createdTm < :endDate " +
            "GROUP BY b.bankCodeInternal, b.bankShortName, b.bankDesc, b.balanceAmount " +
            "ORDER BY b.bankShortName")
    List<BalanceSummaryDetails> getBalanceSummaryDetails(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
