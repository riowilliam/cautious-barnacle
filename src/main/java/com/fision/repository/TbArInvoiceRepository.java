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

@Repository
public interface TbArInvoiceRepository extends JpaRepository<TbArInvoice, Long> {
    TbArInvoice findByInvoiceNo(String invoiceNo);

    @Query("SELECT new com.fision.dto.ARInvoiceDetailDto ( " +
            "tba.invoiceNo, tba.partnerName, tba.contractCode, tba.projectName, tba.bappNo, " +
            "tba.dppAmount, tba.ppnAmount, tba.pphAmount, tba.totalAmount, " +
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
            "COALESCE(SUM(CASE WHEN tba.invoiceStatus = 1 THEN tba.totalAmount ELSE 0 END), 0), " +  // Approved total
            "COALESCE(SUM(CASE WHEN tba.invoiceStatus = 0 THEN tba.totalAmount ELSE 0 END), 0), " +  // Not approved total
            "COALESCE(SUM(CASE WHEN tba.invoiceStatus = 2 THEN tba.totalAmount ELSE 0 END), 0), " +  // Rejected total
            "COALESCE((SELECT SUM(tci.paymentAmount) FROM TbCashIn tci WHERE tci.invoiceNo = tba.invoiceNo AND tci.cashInStatus = 'Completed'), 0), " +  // Total payment amount paid
            "COALESCE(SUM(CASE WHEN tba.invoiceStatus = 1 THEN tba.totalAmount ELSE 0 END), 0) - " +
            "(SELECT COALESCE(SUM(tci.paymentAmount), 0) FROM TbCashIn tci WHERE tci.invoiceNo = tba.invoiceNo AND tci.cashInStatus = 'Completed') " +  // Total unpaid amount
            ")" +
            "FROM TbArInvoice tba " +
            "WHERE (:partnerName IS NULL OR tba.partnerName LIKE %:partnerName%) " +
            "AND (:projectName IS NULL OR tba.projectName LIKE %:projectName%) " +
            "AND (:invoiceStatus IS NULL OR tba.invoiceStatus = :invoiceStatus) " +
            "AND (:startDate IS NULL OR tba.createdTm >= :startDate) " +
            "AND (:endDate IS NULL OR tba.createdTm <= :endDate) " +
            "GROUP BY tba.partnerName, tba.projectName, tba.invoiceNo")
    ARInvoiceSummaryDto getArInvoiceSummary(@Param("partnerName") String partnerName,
                                            @Param("projectName") String projectName,
                                            @Param("invoiceStatus") Integer invoiceStatus,
                                            @Param("startDate") Date startDate,
                                            @Param("endDate") Date endDate);




}
