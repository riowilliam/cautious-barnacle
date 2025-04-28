package com.fision.repository.primary;

import com.fision.dto.CashOutDetailDto;
import com.fision.entity.primary.TmpCashOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface TmpCashOutRepository extends JpaRepository<TmpCashOut, Long> {
    @Query("SELECT new com.fision.dto.CashOutDetailDto(tco.cashOutId, tco.vendorName, tco.invoiceTitle, tco.projectName, v.bankAccount, " +
            "v.bankAccountName, v.bankName, tco.amount, tco.transferFee, tco.total) " +
            "FROM TmpCashOut tco " +
            "LEFT JOIN TbVendor v ON v.vendorName = tco.vendorName " +
            "WHERE tco.documentCashOutName = :docName")
    List<CashOutDetailDto> getTmpCashOutDetailList(@Param("docName") String docName);

    @Query("SELECT SUM(tco.total) FROM TmpCashOut tco WHERE tco.documentCashOutName = :docName ")
    BigDecimal getSubTotal(@Param("docName") String docName);

    List<TmpCashOut> findByDocumentCashOutName(String docName);

    @Modifying
    @Query("UPDATE TmpCashOut SET vendorName = :vendorName, modifiedBy = :username, modifiedTm = CURRENT_TIMESTAMP WHERE vendorName = :oldVendorName")
    void updateVendorName(@Param("oldVendorName") String oldVendorName,
                          @Param("vendorName") String vendorName,
                          @Param("username") String username);

    @Modifying
    @Query("UPDATE TmpCashOut SET projectName = :projectName, modifiedBy = :username, modifiedTm = CURRENT_TIMESTAMP WHERE projectName = :oldProjectName")
    void updateProjectName(@Param("oldProjectName") String oldVendorName,
                           @Param("projectName") String vendorName,
                           @Param("username") String username);
}
