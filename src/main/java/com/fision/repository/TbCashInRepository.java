package com.fision.repository;

import com.fision.dto.CashInDetailDto;
import com.fision.dto.CashInSummaryDto;
import com.fision.entity.TbCashIn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface TbCashInRepository extends JpaRepository<TbCashIn, Long> {
    @Query("SELECT new com.fision.dto.CashInDetailDto (tai.projectName, tci.invoiceNo, tai.partnerName, tai.contractCode," +
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

    @Query("SELECT new com.fision.dto.CashInSummaryDto ( " +
            "COALESCE(SUM(CASE WHEN tci.paymentType = 1 THEN tci.paymentAmount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN tci.paymentType = 2 THEN tci.paymentAmount ELSE 0 END), 0), " +
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

}
