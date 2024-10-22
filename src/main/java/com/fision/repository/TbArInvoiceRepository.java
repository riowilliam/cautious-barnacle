package com.fision.repository;

import com.fision.dto.ARInvoiceDetailDto;
import com.fision.dto.ARInvoiceSummaryDto;
import com.fision.entity.TbArInvoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TbArInvoiceRepository extends JpaRepository<TbArInvoice, Long> {
    TbArInvoice findByInvoiceNo(String invoiceNo);

    @Query("SELECT new com.fision.dto.ARInvoiceDetailDto ( " +
            "tba.invoiceNo, tba.partnerName, tba.contractCode, tba.projectName, tba.bappNo, " +
            "tba.dppAmount, tba.ppnAmount, tba.pphAmount, COALESCE(tba.deduction, 0), tba.totalAmount, " +
            "tba.documentTracking, tba.invoiceStatus, tba.paymentStatus, " +
            "tba.createdTm, tba.createdBy, tba.modifiedTm, tba.modifiedBy) " +
            "FROM TbArInvoice tba " +
            "WHERE (:partnerName IS NULL OR tba.partnerName LIKE %:partnerName%) " +
            "AND (:projectName IS NULL OR tba.projectName LIKE %:projectName%) " +
            "AND (:invoiceStatus IS NULL OR tba.invoiceStatus = :invoiceStatus) " +
            "AND (:startDate is null OR tba.createdTm >= :startDate) " +
            "AND (:endDate is null OR tba.createdTm <= :endDate) ")
    Page<ARInvoiceDetailDto> getArInvoicePaging(@Param("partnerName") String partnerName,
                                                @Param("projectName") String projectName,
                                                @Param("invoiceStatus") Integer invoiceStatus,
                                                @Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                Pageable pageable);

    @Query("SELECT new com.fision.dto.ARInvoiceSummaryDto ( " +
            "COALESCE(SUM(CASE WHEN tba.invoiceStatus = 1 THEN tba.totalAmount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN tba.invoiceStatus = 0 THEN tba.totalAmount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN tba.invoiceStatus = 2 THEN tba.totalAmount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN tci.cashInStatus = 'Completed' THEN tci.paymentAmount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN tba.invoiceStatus = 1 THEN tba.totalAmount ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN tci.cashInStatus = 'Completed' THEN tci.paymentAmount ELSE 0 END), 0) " +
            ")" +
            "FROM TbArInvoice tba " +
            "LEFT JOIN TbCashIn tci ON tci.invoiceNo = tba.invoiceNo " +
            "WHERE (:partnerName IS NULL OR tba.partnerName LIKE %:partnerName%) " +
            "AND (:projectName IS NULL OR tba.projectName LIKE %:projectName%) " +
            "AND (:invoiceStatus IS NULL OR tba.invoiceStatus = :invoiceStatus) " +
            "AND (:startDate IS NULL OR tba.createdTm >= :startDate) " +
            "AND (:endDate IS NULL OR tba.createdTm <= :endDate) " +
            "GROUP BY tba.partnerName, tba.projectName")
    ARInvoiceSummaryDto getArInvoiceSummary(@Param("partnerName") String partnerName,
                                            @Param("projectName") String projectName,
                                            @Param("invoiceStatus") Integer invoiceStatus,
                                            @Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate);


    @Query("SELECT new com.fision.dto.ARInvoiceDetailDto ( " +
            "tba.invoiceNo, tba.partnerName, tba.contractCode, tba.projectName, tba.bappNo, " +
            "tba.dppAmount, tba.ppnAmount, tba.pphAmount, COALESCE(tba.deduction, 0), tba.totalAmount, " +
            "tba.documentTracking, tba.invoiceStatus, tba.paymentStatus, " +
            "tba.createdTm, tba.createdBy, tba.modifiedTm, tba.modifiedBy) " +
            "FROM TbArInvoice tba " +
            "WHERE tba.invoiceStatus = 1 " +
            "AND (:invoiceNo IS NULL OR tba.invoiceNo LIKE %:invoiceNo%) " +
            "AND (tba.paymentStatus IS NULL OR tba.paymentStatus <> 'Fully Paid') " )
    List<ARInvoiceDetailDto> getArInvoiceDetailList(@Param("invoiceNo") String invoiceNo);
}
