package com.fision.repository;

import com.fision.dto.ContractListDto;
import com.fision.entity.TbContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@Repository
public interface TbContractRepository extends JpaRepository<TbContract, Long> {
    TbContract findByContractCodeAndRevision(String contractCode, Integer revision);

    @Query("SELECT new com.fision.dto.ContractListDto(" +
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
    Page<ContractListDto> getContractListPaging(@Param("contractName") String contractName,
                                                @Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                Pageable pageable);

    @Query("SELECT c.contractCode FROM TbContract c " +
            "WHERE (:contractCode IS NULL OR c.contractCode LIKE %:contractCode%) ")
    List<String> getContractCodeList(String contractCode);

    @Query("SELECT new map(c.contractId as contractId, c.contractCode as contractCode, c.contractName as contractName) " +
            "FROM TbContract c " +
            "WHERE c.revision = (SELECT MAX(c2.revision) FROM TbContract c2 WHERE c2.contractCode = c.contractCode) " +
            "AND (:contractName IS NULL OR c.contractName LIKE %:contractName%) ")
    List<Map<String, Object>> findContractWithHighestRevision(@Param("contractName") String contractName);

    TbContract findByContractCode(String contractCode);
}
