package com.fision.repository;

import com.fision.dto.StatisticsDetailsDto;
import com.fision.entity.MsBalance;
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
            "COALESCE(SUM(COALESCE(co.amount, 0)), 0)) " +
            "FROM TbCashIn ci " +
            "LEFT JOIN TbCashOut co ON ci.createdTm = co.createdTm AND ci.cashInStatus = 'Completed' " +
            "WHERE ci.cashInStatus = 'Completed' AND " +
            "(:startDate IS NULL OR ci.createdTm >= :startDate) AND " +
            "(:endDate IS NULL OR ci.createdTm <= :endDate)")
    StatisticsDetailsDto getDailyStats(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT DATE_FORMAT(DATE_ADD(DATE(:startDate), INTERVAL days.n DAY), '%d %b') AS statsHeader, " +
            "COALESCE(SUM(ci.payment_amount), 0) AS totalCashIn, " +
            "COALESCE(SUM(co.amount), 0) AS totalCashOut " +
            "FROM (SELECT 0 AS n " +
            "UNION ALL SELECT 1 " +
            "UNION ALL SELECT 2 " +
            "UNION ALL SELECT 3 " +
            "UNION ALL SELECT 4 " +
            "UNION ALL SELECT 5 " +
            "UNION ALL SELECT 6) AS days " +
            "LEFT JOIN tb_cash_in ci ON DATE(ci.created_tm) = DATE_ADD(DATE(:startDate), INTERVAL days.n DAY) AND ci.cash_in_status = 'Completed' " +
            "LEFT JOIN tb_cash_out co ON DATE(co.created_tm) = DATE_ADD(DATE(:startDate), INTERVAL days.n DAY) " +
            "GROUP BY days.n " +
            "ORDER BY days.n ASC ", nativeQuery = true )
    List<Map<String, Object>> getWeeklyStats(@Param("startDate") Date startDate);

    @Query(value = "SELECT DATE_FORMAT(DATE(:startDate) + INTERVAL day.n DAY, '%d %b') AS statsHeader, " +
            "COALESCE(SUM(ci.payment_amount), 0) AS totalCashIn, " +
            "COALESCE(SUM(co.amount), 0) AS totalCashOut " +
            "FROM (SELECT 0 AS n " +
            "      UNION ALL SELECT 1 " +
            "      UNION ALL SELECT 2 " +
            "      UNION ALL SELECT 3 " +
            "      UNION ALL SELECT 4 " +
            "      UNION ALL SELECT 5 " +
            "      UNION ALL SELECT 6 " +
            "      UNION ALL SELECT 7 " +
            "      UNION ALL SELECT 8 " +
            "      UNION ALL SELECT 9 " +
            "      UNION ALL SELECT 10 " +
            "      UNION ALL SELECT 11 " +
            "      UNION ALL SELECT 12 " +
            "      UNION ALL SELECT 13 " +
            "      UNION ALL SELECT 14 " +
            "      UNION ALL SELECT 15 " +
            "      UNION ALL SELECT 16 " +
            "      UNION ALL SELECT 17 " +
            "      UNION ALL SELECT 18 " +
            "      UNION ALL SELECT 19 " +
            "      UNION ALL SELECT 20 " +
            "      UNION ALL SELECT 21 " +
            "      UNION ALL SELECT 22 " +
            "      UNION ALL SELECT 23 " +
            "      UNION ALL SELECT 24 " +
            "      UNION ALL SELECT 25 " +
            "      UNION ALL SELECT 26 " +
            "      UNION ALL SELECT 27 " +
            "      UNION ALL SELECT 28 " +
            "      UNION ALL SELECT 29 " +
            "      UNION ALL SELECT 30 " +
            "      UNION ALL SELECT 31) AS day " +
            "LEFT JOIN tb_cash_in ci ON DATE(ci.created_tm) = DATE(:startDate) + INTERVAL day.n DAY AND ci.cash_in_status = 'Completed' " +
            "LEFT JOIN tb_cash_out co ON DATE(co.created_tm) = DATE(:startDate) + INTERVAL day.n DAY " +
            "WHERE MONTH(DATE(:startDate) + INTERVAL day.n DAY) = MONTH(:startDate) " +
            "AND YEAR(DATE(:startDate) + INTERVAL day.n DAY) = YEAR(:startDate) " +
            "AND day.n < DAY(LAST_DAY(:startDate)) + 1 " +
            "GROUP BY day.n " +
            "ORDER BY day.n ASC", nativeQuery = true)
    List<Map<String, Object>> getMonthlyStats(@Param("startDate") Date startDate);

    @Query(value = "SELECT DATE_FORMAT(DATE_FORMAT(:startDate, '%Y-01-01') + INTERVAL month.n MONTH, '%b') AS statsHeader, " +
            "COALESCE(SUM(ci.payment_amount), 0) AS totalCashIn, " +
            "COALESCE(SUM(co.amount), 0) AS totalCashOut " +
            "FROM (SELECT 0 AS n " +
            "      UNION ALL SELECT 1 " +
            "      UNION ALL SELECT 2 " +
            "      UNION ALL SELECT 3 " +
            "      UNION ALL SELECT 4 " +
            "      UNION ALL SELECT 5 " +
            "      UNION ALL SELECT 6 " +
            "      UNION ALL SELECT 7 " +
            "      UNION ALL SELECT 8 " +
            "      UNION ALL SELECT 9 " +
            "      UNION ALL SELECT 10 " +
            "      UNION ALL SELECT 11) AS month " +
            "LEFT JOIN tb_cash_in ci ON MONTH(ci.created_tm) = month.n + 1 AND YEAR(ci.created_tm) = YEAR(:startDate) AND ci.cash_in_status = 'Completed' " +
            "LEFT JOIN tb_cash_out co ON MONTH(co.created_tm) = month.n + 1 AND YEAR(co.created_tm) = YEAR(:startDate) " +
            "GROUP BY month.n " +
            "ORDER BY month.n ASC", nativeQuery = true)
    List<Map<String, Object>> getYearlyStats(@Param("startDate") Date startDate);

    @Query(value = "SELECT COALESCE(SUM(ci.payment_amount), 0) FROM tb_cash_in ci WHERE DATE(ci.created_tm) < DATE(:paramDate) AND ci.cash_in_status = 'Completed'", nativeQuery = true)
    BigDecimal getTotalCashInBeforeDate(@Param("paramDate") Date paramDate);

    @Query(value = "SELECT COALESCE(SUM(co.amount), 0) FROM tb_cash_out co WHERE DATE(co.created_tm) < DATE(:paramDate)", nativeQuery = true)
    BigDecimal getTotalCashOutBeforeDate(@Param("paramDate") Date paramDate);

    @Query(value = "SELECT COALESCE(SUM(ci.payment_amount), 0) FROM tb_cash_in ci WHERE DATE(ci.created_tm) <= DATE(:paramDate) AND ci.cash_in_status = 'Completed'", nativeQuery = true)
    BigDecimal getTotalCashInTillToday(@Param("paramDate") Date paramDate);

    @Query(value = "SELECT COALESCE(SUM(co.amount), 0) FROM tb_cash_out co WHERE DATE(co.created_tm) <= DATE(:paramDate)", nativeQuery = true)
    BigDecimal getTotalCashOutTillToday(@Param("paramDate") Date paramDate);

    @Query(value = "SELECT COALESCE(SUM(ci.payment_amount), 0) FROM tb_cash_in ci WHERE DATE(ci.created_tm) >= DATE(:startDate) AND DATE(ci.created_tm) < DATE(:endDate) AND ci.cash_in_status = 'Completed'", nativeQuery = true)
    BigDecimal getTotalCashInToday(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "SELECT COALESCE(SUM(co.amount), 0) FROM tb_cash_out co WHERE DATE(co.created_tm) >= DATE(:startDate) AND DATE(co.created_tm) < DATE(:endDate)", nativeQuery = true)
    BigDecimal getTotalCashOutToday(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    MsBalance findByBalanceName(String balanceName);
}
