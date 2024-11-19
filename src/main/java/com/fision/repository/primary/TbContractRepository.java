package com.fision.repository.primary;

import com.fision.dto.ContractPagingListDto;
import com.fision.entity.primary.TbContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author LordDev
 */
@Repository
public interface TbContractRepository extends JpaRepository<TbContract, Long> {
    TbContract findByContractCodeAndRevision(String contractCode, Integer revision);

    @Query("SELECT new com.fision.dto.ContractPagingListDto(" +
            "c.contractCode, " +
            "c.contractName, " +
            "c.createdTm, " +
            "c.createdBy, " +
            "c.modifiedTm as modifiedDate, " +
            "c.modifiedBy) " +
            "FROM TbContract c " +
            "WHERE c.revision = (SELECT MAX(c2.revision) FROM TbContract c2 WHERE c2.contractCode = c.contractCode) " +
            "AND (:contractName IS NULL OR c.contractName LIKE %:contractName%) " +
            "AND (:startDate is null OR c.createdTm >= :startDate) " +
            "AND (:endDate is null OR c.createdTm <= :endDate) ")
    Page<ContractPagingListDto> getContractListPaging(@Param("contractName") String contractName,
                                                      @Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate,
                                                      Pageable pageable);

    @Query("SELECT c.contractCode, c.contractName, i.itemName, i.totalQuantity, i.remainingQuantity, COALESCE(p.paidQuantity, 0) " +
            "FROM TbContract c " +
            "LEFT JOIN TbItemDetails i ON c.contractCode = i.contractCode AND c.revision = i.revision " +
            "LEFT JOIN TxPaidItem p ON c.contractCode = i.contractCode AND i.itemName = p.itemName " +
            "WHERE c.revision = (SELECT MAX(c2.revision) FROM TbContract c2 WHERE c2.contractCode = c.contractCode) " +
            "AND (:contractName IS NULL OR c.contractName LIKE %:contractName%) " +
            "AND (:contractCode IS NULL OR c.contractCode LIKE %:contractCode%) " /*+
            "AND i.remainingQuantity > 0 "*/ )
    List<Object[]> findContractWithHighestRevision(@Param("contractName") String contractName, @Param("contractCode") String contractCode);


    TbContract findByContractCode(String contractCode);
    @Query("SELECT t FROM TbContract t WHERE t.contractCode = :contractCode AND t.revision = " +
            "(SELECT MAX(t2.revision) FROM TbContract t2 WHERE t2.contractCode = :contractCode)")
    TbContract findByContractCodeAndMaxRevision(@Param("contractCode") String contractCode);

    @Query("SELECT c.revision, c.createdBy, c.createdTm, i.itemName, i.totalQuantity, i.remainingQuantity, COALESCE(p.paidQuantity, 0) " +
            "FROM TbContract c " +
            "LEFT JOIN TbItemDetails i ON c.contractCode = i.contractCode AND c.revision = i.revision " +
            "LEFT JOIN TxPaidItem p ON c.contractCode = i.contractCode AND i.itemName = p.itemName " +
            "WHERE (:contractCode IS NULL OR c.contractCode LIKE %:contractCode%) ")
    List<Object[]> findContractRevisionList(@Param("contractCode") String contractCode);
}
