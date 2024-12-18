package com.fision.repository.primary;

import com.fision.dto.FacilityTransactionDto;
import com.fision.entity.primary.TbFacilityAssetTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TbFacilityAssetTransactionRepository extends JpaRepository<TbFacilityAssetTransaction, Long> {
    @Query("SELECT new com.fision.dto.FacilityTransactionDto(" +
            "t.facilityAssetTransactionId, t.companyName, t.transactionDate, t.amount, t.facilityType, t.transactionType, " +
            "t.projectName, t.debitAdvice, t.bankApprovalDate, t.tenorDate) " +
            "FROM TbFacilityAssetTransaction t " +
            "WHERE (:companyName IS NULL OR t.companyName LIKE %:companyName%) " +
            "AND (:facilityType IS NULL OR t.facilityType = :facilityType) " +
            "AND (:projectName IS NULL OR t.projectName = :projectName) " +
            "AND (:debitAdvice IS NULL OR t.debitAdvice LIKE %:debitAdvice%) " +
            "AND (:tenorDateOnWeekend IS NULL OR t.isTenorDateOnWeekend = :tenorDateOnWeekend) " +
            "AND (:startDate IS NULL OR t.tenorDate >= :startDate) " +
            "AND (:endDate IS NULL OR t.tenorDate <= :endDate) ")
    Page<FacilityTransactionDto> findFacilityTransactions(Pageable pageable,
                                                          @Param("companyName") String companyName,
                                                          @Param("facilityType") String facilityType,
                                                          @Param("projectName") String projectName,
                                                          @Param("debitAdvice") String debitAdvice,
                                                          @Param("tenorDateOnWeekend") Boolean tenorDateOnWeekend,
                                                          @Param("startDate") Date startDate,
                                                          @Param("endDate") Date endDate);

    @Query("SELECT t.transactionType AS transactionType, SUM(t.amount) AS totalAmount " +
            "FROM TbFacilityAssetTransaction t " +
            "WHERE (:companyName IS NULL OR t.companyName LIKE %:companyName%) " +
            "AND (:facilityType IS NULL OR t.facilityType = :facilityType) " +
            "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND (:debitAdvice IS NULL OR t.debitAdvice LIKE %:debitAdvice%) " +
            "AND (:tenorDateOnWeekend IS NULL OR t.isTenorDateOnWeekend = :tenorDateOnWeekend) " +
            "AND (:startDate IS NULL OR t.tenorDate >= :startDate) " +
            "AND (:endDate IS NULL OR t.tenorDate <= :endDate) " +
            "GROUP BY t.transactionType")
    List<Object[]> findTransactionSummary(@Param("companyName") String companyName,
                                          @Param("facilityType") String facilityType,
                                          @Param("transactionType") String transactionType,
                                          @Param("debitAdvice") String debitAdvice,
                                          @Param("tenorDateOnWeekend") Boolean tenorDateOnWeekend,
                                          @Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate);

    @Query("SELECT f FROM TbFacilityAssetTransaction f WHERE f.calculateDate = :startOfDay AND isAddedToCashOut = :status ")
    Page<TbFacilityAssetTransaction> findByCalculateDateAndStatus(
            @Param("startOfDay") Date startOfDay,
            @Param("status") boolean status,
            Pageable pageable);

    @Query("SELECT fb.facilityType FROM TbFacilityBalance fb WHERE fb.isCanAddManual = 1 ")
    List<String> getFacilityBalanceTypeList();

    Optional<TbFacilityAssetTransaction> findById(Long id);
}
