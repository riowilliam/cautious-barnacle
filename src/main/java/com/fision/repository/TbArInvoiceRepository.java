package com.fision.repository;

import com.fision.dto.ARInvoiceDetailDto;
import com.fision.dto.ARInvoiceSummaryDto;
import com.fision.dto.DashboardCardDetailsDto;
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
            "tba.dppAmount, " +
            "SUM(COALESCE(tci.paymentAmount, 0)), " + // Menghitung total paymentAmount
            "tba.ppnAmount, tba.pphAmount, COALESCE(tba.deduction, 0), tba.totalAmount, " +
            "tba.documentTracking, tba.invoiceStatus, tba.paymentStatus, " +
            "tba.createdTm, tba.createdBy, tba.modifiedTm, tba.modifiedBy, tba.paidItemDetails) " +
            "FROM TbArInvoice tba " +
            "LEFT JOIN TbCashIn tci ON tba.invoiceNo = tci.invoiceNo AND tci.cashInStatus = 'Completed' " +
            "WHERE (:partnerName IS NULL OR tba.partnerName LIKE %:partnerName%) " +
            "AND (:projectName IS NULL OR tba.projectName LIKE %:projectName%) " +
            "AND (:invoiceStatus IS NULL OR tba.invoiceStatus = :invoiceStatus) " +
            "AND (:startDate IS NULL OR tba.createdTm >= :startDate) " +
            "AND (:endDate IS NULL OR tba.createdTm <= :endDate) " +
            "GROUP BY tba.invoiceNo, tba.partnerName, tba.contractCode, tba.projectName, " +
            "tba.bappNo, tba.dppAmount, tba.ppnAmount, tba.pphAmount, tba.deduction, " +
            "tba.totalAmount, tba.documentTracking, tba.invoiceStatus, tba.paymentStatus, " +
            "tba.createdTm, tba.createdBy, tba.modifiedTm, tba.modifiedBy, tba.paidItemDetails")
    Page<ARInvoiceDetailDto> getArInvoicePaging(@Param("partnerName") String partnerName,
                                                @Param("projectName") String projectName,
                                                @Param("invoiceStatus") Integer invoiceStatus,
                                                @Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                Pageable pageable);


    @Query(value = "SELECT " +
            "COALESCE(SUM(DISTINCT CASE WHEN tba.invoice_status = 1 THEN tba.total_amount ELSE 0 END), 0), " +
            "COALESCE(SUM(DISTINCT CASE WHEN tba.invoice_status = 0 THEN tba.total_amount ELSE 0 END), 0), " +
            "COALESCE(SUM(DISTINCT CASE WHEN tba.invoice_status = 2 THEN tba.total_amount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN tci.cash_in_status = 'Completed' THEN tci.payment_amount ELSE 0 END), 0), " +
            "COALESCE(SUM(DISTINCT CASE WHEN tba.invoice_status = 1 THEN tba.total_amount ELSE 0 END), 0) - " +
            "COALESCE(SUM(CASE WHEN tci.cash_in_status = 'Completed' THEN tci.payment_amount ELSE 0 END), 0) " +
            "FROM tb_ar_invoice tba " +
            "LEFT JOIN tb_cash_in tci ON tci.invoice_no = tba.invoice_no " +
            "WHERE (:partnerName IS NULL OR tba.partner_name LIKE %:partnerName%) " +
            "AND (:projectName IS NULL OR tba.project_name LIKE %:projectName%) " +
            "AND (:invoiceStatus IS NULL OR tba.invoice_status = :invoiceStatus) " +
            "AND (:startDate IS NULL OR tba.created_tm >= :startDate) " +
            "AND (:endDate IS NULL OR tba.created_tm <= :endDate) ",
            nativeQuery = true)
    Object getArInvoiceSummary (
            @Param("partnerName") String partnerName,
            @Param("projectName") String projectName,
            @Param("invoiceStatus") Integer invoiceStatus,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);

    @Query("SELECT new com.fision.dto.ARInvoiceDetailDto ( " +
            "tba.invoiceNo, tba.partnerName, tba.contractCode, tba.projectName, tba.bappNo, " +
            "tba.dppAmount, SUM(COALESCE(tci.paymentAmount, 0)), tba.ppnAmount, tba.pphAmount, COALESCE(tba.deduction, 0), tba.totalAmount, " +
            "tba.documentTracking, tba.invoiceStatus, tba.paymentStatus, " +
            "tba.createdTm, tba.createdBy, tba.modifiedTm, tba.modifiedBy, tba.paidItemDetails) " +
            "FROM TbArInvoice tba " +
            "LEFT JOIN TbCashIn tci ON tba.invoiceNo = tci.invoiceNo AND tci.cashInStatus = 'Completed' " +
            "WHERE tba.invoiceStatus = 1 " +
            "AND (:invoiceNo IS NULL OR tba.invoiceNo LIKE %:invoiceNo%) " +
            "AND (tba.paymentStatus IS NULL OR tba.paymentStatus <> 'Fully Paid') " +
            "GROUP BY tba.invoiceNo, tba.partnerName, tba.contractCode, tba.projectName, " +
            "tba.bappNo, tba.dppAmount, tba.ppnAmount, tba.pphAmount, tba.deduction, " +
            "tba.totalAmount, tba.documentTracking, tba.invoiceStatus, tba.paymentStatus, " +
            "tba.createdTm, tba.createdBy, tba.modifiedTm, tba.modifiedBy, tba.paidItemDetails" )
    List<ARInvoiceDetailDto> getArInvoiceDetailList(@Param("invoiceNo") String invoiceNo);

    @Query("SELECT new com.fision.dto.DashboardCardDetailsDto(" +
            "'AR Invoice', " +
            "COALESCE(SUM(CASE WHEN tba.invoiceStatus = 1 THEN tba.totalAmount ELSE 0 END), 0), " +
            "CAST(SUM(CASE WHEN tba.invoiceStatus = 1 THEN 1 ELSE 0 END) AS int), " +
            "CAST(SUM(CASE WHEN tba.invoiceStatus = 0 THEN 1 ELSE 0 END) AS int)) " +
            "FROM TbArInvoice tba " +
            "WHERE (:startDate IS NULL OR tba.createdTm >= :startDate) " +
            "AND (:endDate IS NULL OR tba.createdTm < :endDate) ")
    DashboardCardDetailsDto getARInvoiceCardDetail(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


}
