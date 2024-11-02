package com.fision.repository;

import com.fision.dto.CashInDetailDto;
import com.fision.dto.CashInSummaryDto;
import com.fision.dto.DashboardCardDetailsDto;
import com.fision.entity.TbCashIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface TbCashInRepository extends JpaRepository<TbCashIn, Long> {
    TbCashIn findByCashInId(Long id);
    @Query("SELECT new com.fision.dto.CashInDetailDto (tci.cashInId, tai.projectName, tci.invoiceNo, tai.partnerName, tai.contractCode," +
            "tci.paymentAmount, tci.paymentDate, tci.paymentType, tci.createdTm, tci.createdBy, tci.modifiedTm, tci.modifiedBy, tci.cashInStatus) " +
            "FROM TbCashIn tci " +
            "LEFT JOIN TbArInvoice tai ON tci.invoiceNo = tai.invoiceNo " +
            "WHERE (:partnerName IS NULL OR tai.partnerName LIKE %:partnerName%) " +
            "AND (:projectName IS NULL OR tai.projectName LIKE %:projectName%) " +
            "AND (:paymentType IS NULL OR tci.paymentType = :paymentType) " +
            "AND (:startDate IS NULL OR tci.createdTm >= :startDate) " +
            "AND (:endDate IS NULL OR tci.createdTm <= :endDate)")
    Page<CashInDetailDto> getCashInPaging(@Param("partnerName") String partnerName,
                                          @Param("projectName") String projectName,
                                          @Param("paymentType") Integer paymentType,
                                          @Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate,
                                          Pageable pageable);

    @Query("SELECT new com.fision.dto.CashInDetailDto (tci.cashInId, tai.projectName, tci.invoiceNo, tai.partnerName, tai.contractCode," +
            "tci.paymentAmount, tci.paymentDate, tci.paymentType, tci.createdTm, tci.createdBy, tci.modifiedTm, tci.modifiedBy, tci.cashInStatus) " +
            "FROM TbCashIn tci " +
            "LEFT JOIN TbArInvoice tai ON tci.invoiceNo = tai.invoiceNo " +
            "WHERE tci.invoiceNo = :invoiceNo ")
    List<CashInDetailDto> getCashInListByInvoiceNo(@Param("invoiceNo") String invoiceNo);

    @Query("SELECT new com.fision.dto.CashInSummaryDto ( " +
            "COALESCE(SUM(CASE WHEN tci.paymentType = 1 AND tci.cashInStatus = 'Completed' THEN tci.paymentAmount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN tci.paymentType = 2 AND tci.cashInStatus = 'Completed' THEN tci.paymentAmount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN tci.cashInStatus = 'Completed' THEN tci.paymentAmount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN tci.cashInStatus = 'Incompleted' THEN tci.paymentAmount ELSE 0 END), 0)) " +
            "FROM TbCashIn tci " +
            "LEFT JOIN TbArInvoice tai ON tci.invoiceNo = tai.invoiceNo " +
            "WHERE (:partnerName IS NULL OR tai.partnerName LIKE %:partnerName%) " +
            "AND (:projectName IS NULL OR tai.projectName LIKE %:projectName%) " +
            "AND (:paymentType IS NULL OR tci.paymentType = :paymentType) " +
            "AND (:startDate IS NULL OR tci.createdTm >= :startDate) " +
            "AND (:endDate IS NULL OR tci.createdTm <= :endDate)")
    CashInSummaryDto getSummaryCashIn(@Param("partnerName") String partnerName,
                                      @Param("projectName") String projectName,
                                      @Param("paymentType") Integer paymentType,
                                      @Param("startDate") Date startDate,
                                      @Param("endDate") Date endDate);

    @Query("SELECT COALESCE(SUM(COALESCE(tci.paymentAmount,0) + COALESCE(tci.deduction, 0)), 0) " +
            "FROM TbCashIn tci " +
            "WHERE tci.invoiceNo = :invoiceNo " +
            "AND tci.cashInStatus = 'Incompleted' ")
    BigDecimal getTotalIncompletedCashIByInvoiceNo(@Param("invoiceNo") String invoiceNo);

    @Query("SELECT new com.fision.dto.DashboardCardDetailsDto(" +
            "'Cash In', " +
            "COALESCE(SUM(CASE WHEN tci.cashInStatus = 'Completed' THEN tci.paymentAmount ELSE 0 END), 0), " +
            "CAST(SUM(CASE WHEN tci.cashInStatus = 'Completed' THEN 1 ELSE 0 END) AS int), " +
            "CAST(SUM(CASE WHEN tci.cashInStatus = 'Incompleted' THEN 1 ELSE 0 END) AS int)) " +
            "FROM TbCashIn tci " +
            "WHERE (:startDate IS NULL OR tci.createdTm >= :startDate) " +
            "AND (:endDate IS NULL OR tci.createdTm < :endDate)")
    DashboardCardDetailsDto getCashInCardDetail(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
