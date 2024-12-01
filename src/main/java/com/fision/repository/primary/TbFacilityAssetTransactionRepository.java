package com.fision.repository.primary;

import com.fision.dto.FacilityTransactionDto;
import com.fision.entity.primary.TbFacilityAssetTransaction;
import com.fision.entity.secondary.FisionOutSourceData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TbFacilityAssetTransactionRepository extends JpaRepository<TbFacilityAssetTransaction, Long> {
    @Query("SELECT new com.fision.dto.FacilityTransactionDto(" +
            "t.vendorName, t.transactionDate, t.amount, t.facilityType, t.transactionType, t.bankApprovalDate, t.tenorDate) " +
            "FROM TbFacilityAssetTransaction t " +
            "WHERE (:vendorName IS NULL OR t.vendorName = :vendorName) " +
            "AND (:facilityType IS NULL OR t.facilityType = :facilityType) " +
            "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND (:tenorDateOnWeekend IS NULL OR t.isTenorDateOnWeekend = :tenorDateOnWeekend) " +
            "AND (:startDate IS NULL OR t.tenorDate >= :startDate) " +
            "AND (:endDate IS NULL OR t.tenorDate <= :endDate) ")
    Page<FacilityTransactionDto> findFacilityTransactions(Pageable pageable,
                                                          @Param("vendorName") String vendorName,
                                                          @Param("facilityType") String facilityType,
                                                          @Param("transactionType") String transactionType,
                                                          @Param("tenorDateOnWeekend") Boolean tenorDateOnWeekend,
                                                          @Param("startDate") Date startDate,
                                                          @Param("endDate") Date endDate);

    @Query("SELECT t.transactionType AS transactionType, SUM(t.amount) AS totalAmount " +
            "FROM TbFacilityAssetTransaction t " +
            "WHERE (:vendorName IS NULL OR t.vendorName = :vendorName) " +
            "AND (:facilityType IS NULL OR t.facilityType = :facilityType) " +
            "AND (:transactionType IS NULL OR t.transactionType = :transactionType) " +
            "AND (:tenorDateOnWeekend IS NULL OR t.isTenorDateOnWeekend = :tenorDateOnWeekend) " +
            "AND (:startDate IS NULL OR t.tenorDate >= :startDate) " +
            "AND (:endDate IS NULL OR t.tenorDate <= :endDate) " +
            "GROUP BY t.transactionType")
    List<Object[]> findTransactionSummary(@Param("vendorName") String vendorName,
                                          @Param("facilityType") String facilityType,
                                          @Param("transactionType") String transactionType,
                                          @Param("tenorDateOnWeekend") Boolean tenorDateOnWeekend,
                                          @Param("startDate") Date startDate,
                                          @Param("endDate") Date endDate);
    @Query("SELECT f FROM TbFacilityAssetTransaction f WHERE f.calculateDate = :startOfDay AND isAddedToCashOut = :status ")
    Page<TbFacilityAssetTransaction> findByCalculateDateAndStatus(
            @Param("startOfDay") Date startOfDay,
            @Param("status") boolean status,
            Pageable pageable);
}
