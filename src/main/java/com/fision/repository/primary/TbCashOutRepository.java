package com.fision.repository.primary;

import com.fision.dto.CashOutDetailDto;
import com.fision.dto.CashOutMutationDto;
import com.fision.entity.primary.TbCashOut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface TbCashOutRepository extends JpaRepository<TbCashOut, Long> {

    @Query("SELECT new com.fision.dto.CashOutMutationDto(tco.cashOutId, tco.vendorName, tco.invoiceTitle, tco.projectName, tco.documentCashOutName, " +
            "v.bankAccount, v.bankAccountName, v.bankName, tco.amount, tco.transferFee, tco.total, tco.createdTm, tco.createdBy) " +
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

    @Query("SELECT new com.fision.dto.CashOutDetailDto(tco.cashOutId, tco.vendorName, tco.invoiceTitle, tco.projectName, v.bankAccount, " +
            "v.bankName, v.bankAccountName, tco.amount, tco.transferFee, tco.total) " +
            "FROM TbCashOut tco " +
            "LEFT JOIN TbVendor v ON v.vendorName = tco.vendorName " +
            "WHERE tco.documentCashOutName = :docName")
    List<CashOutDetailDto> getCashOutDetailList(@Param("docName") String docName);

    @Query("SELECT SUM(tco.total) FROM TbCashOut tco WHERE tco.documentCashOutName = :docName ")
    BigDecimal getSubTotalDetail(@Param("docName") String docName);

    @Modifying
    @Query("UPDATE TbCashOut SET vendorName = :vendorName, modifiedBy = :username, modifiedTm = CURRENT_TIMESTAMP WHERE vendorName = :oldVendorName")
    void updateVendorName(@Param("oldVendorName") String oldVendorName,
                          @Param("vendorName") String vendorName,
                          @Param("username") String username);

    @Modifying
    @Query("UPDATE TbCashOut SET projectName = :projectName, modifiedBy = :username, modifiedTm = CURRENT_TIMESTAMP WHERE projectName = :oldProjectName")
    void updateProjectName(@Param("oldProjectName") String oldVendorName,
                          @Param("projectName") String vendorName,
                          @Param("username") String username);
}
