package com.fision.repository;

import com.fision.dto.CashOutMutationDto;
import com.fision.entity.TbCashOut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;

@Repository
public interface TbCashOutRepository extends JpaRepository<TbCashOut, Long> {

    @Query("SELECT new com.fision.dto.CashOutMutationDto(tco.cashOutId, tco.vendorName, tco.invoiceTitle, tco.projectName, tco.documentCashOutName, " +
            "v.bankAccount, v.bankName, tco.amount, tco.transferFee, tco.total, tco.createdTm, tco.createdBy) " +
            "FROM TbCashOut tco " +
            "LEFT JOIN TbVendor v ON v.vendorName = tco.vendorName " +
            "WHERE (:vendorName IS NULL OR tco.vendorName LIKE %:vendorName%) " +
            "AND (:docName IS NULL OR tco.documentCashOutName LIKE %:docName%)" +
            "AND (:startDate is null OR tco.createdTm >= :startDate) " +
            "AND (:endDate is null OR tco.createdTm <= :endDate) ")
    Page<CashOutMutationDto> getCashOutMutationPaging(@Param("vendorName") String vendorName,
                                                      @Param("docName") String docName,
                                                      @Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate,
                                                      Pageable pageable);

    @Query("SELECT SUM(tco.total) FROM TbCashOut tco " +
            "WHERE (:vendorName IS NULL OR tco.vendorName LIKE %:vendorName%) " +
            "AND (:docName IS NULL OR tco.documentCashOutName LIKE %:docName%)" +
            "AND (:startDate is null OR tco.createdTm >= :startDate) " +
            "AND (:endDate is null OR tco.createdTm <= :endDate) ")
    BigDecimal getSubTotal(@Param("vendorName") String vendorName,
                           @Param("docName") String docName,
                           @Param("startDate")Date startDate,
                           @Param("endDate")Date endDate);
}
