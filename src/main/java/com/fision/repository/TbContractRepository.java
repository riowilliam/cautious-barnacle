package com.fision.repository;

import com.fision.dto.ContractListDto;
import com.fision.dto.ContractPagingListDto;
import com.fision.entity.TbContract;
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

    @Query("SELECT c.contractCode, i.itemName, i.totalQuantity, i.remainingQuantity, COALESCE(p.paidQuantity, 0) " +
            "FROM TbContract c " +
            "LEFT JOIN TbItemDetails i ON c.contractCode = i.contractCode " +
            "LEFT JOIN TxPaidItem p ON c.contractCode = i.contractCode AND i.itemName = p.itemName " +
            "WHERE c.revision = (SELECT MAX(c2.revision) FROM TbContract c2 WHERE c2.contractCode = c.contractCode) " +
            "AND (:contractName IS NULL OR c.contractName LIKE %:contractName%)")
    List<Object[]> findContractWithHighestRevision(@Param("contractName") String contractName);


    TbContract findByContractCode(String contractCode);
}
