package com.fision.repository.primary;

import com.fision.dto.ContractPagingListDto;
import com.fision.entity.primary.TbContract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
    TbContract findBycontractNoAndRevision(String contractNo, Integer revision);

    @Query("SELECT new com.fision.dto.ContractPagingListDto(" +
            "c.contractNo, " +
            "c.contractName, " +
            "c.partnerName, " +
            "c.contractDate, " +
            "c.endContractDate, " +
            "c.createdTm, " +
            "c.createdBy, " +
            "c.modifiedTm as modifiedDate, " +
            "c.modifiedBy) " +
            "FROM TbContract c " +
            "WHERE c.revision = (SELECT MAX(c2.revision) FROM TbContract c2 WHERE c2.contractNo = c.contractNo) " +
            "AND (:contractNo IS NULL OR c.contractNo LIKE %:contractNo%) " +
            "AND (:partnerName IS NULL OR c.partnerName LIKE %:partnerName%) " +
            "AND (:startDate is null OR c.contractDate >= :startDate) " +
            "AND (:endDate is null OR c.contractDate <= :endDate) ")
    Page<ContractPagingListDto>  getContractListPaging(@Param("contractNo") String contractNo,
                                                      @Param("partnerName") String partnerName,
                                                      @Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate,
                                                      Pageable pageable);

    @Query("SELECT c.contractNo, c.contractName, i.itemName, i.totalQuantity, i.remainingQuantity, COALESCE(p.paidQuantity, 0) " +
            "FROM TbContract c " +
            "LEFT JOIN TbItemDetails i ON c.contractNo = i.contractNo AND c.revision = i.revision " +
            "LEFT JOIN TxPaidItem p ON c.contractNo = i.contractNo AND i.itemName = p.itemName " +
            "LEFT JOIN TbPartner tp ON c.partnerName = tp.partnerName " +
            "WHERE c.revision = (SELECT MAX(c2.revision) FROM TbContract c2 WHERE c2.contractNo = c.contractNo) " +
            "AND tp.partnerName = :partnerName " +
            "AND (:contractName IS NULL OR c.contractName LIKE %:contractName%) " +
            "AND (:contractNo IS NULL OR c.contractNo LIKE %:contractNo%) " +
            "AND i.remainingQuantity > 0 " )
    List<Object[]> findContractWithHighestRevision(@Param("partnerName") String partnerName, @Param("contractName") String contractName, @Param("contractNo") String contractNo);


    TbContract findBycontractNo(String contractNo);
    @Query("SELECT t FROM TbContract t WHERE t.contractNo = :contractNo AND t.revision = " +
            "(SELECT MAX(t2.revision) FROM TbContract t2 WHERE t2.contractNo = :contractNo)")
    TbContract findBycontractNoAndMaxRevision(@Param("contractNo") String contractNo);

    @Query("SELECT c.revision, c.createdBy, c.addendumDate, i.itemName, i.totalQuantity, i.remainingQuantity, COALESCE(p.paidQuantity, 0) " +
            "FROM TbContract c " +
            "LEFT JOIN TbItemDetails i ON c.contractNo = i.contractNo AND c.revision = i.revision " +
            "LEFT JOIN TxPaidItem p ON c.contractNo = i.contractNo AND i.itemName = p.itemName " +
            "WHERE (:contractNo IS NULL OR c.contractNo LIKE %:contractNo%) " +
            "AND c.revision > 0 ")
    List<Object[]> findContractRevisionList(@Param("contractNo") String contractNo);

    @Modifying
    @Query("UPDATE TbContract SET partnerName = :partnerName, modifiedBy = :username, modifiedTm = CURRENT_TIMESTAMP WHERE partnerName = :oldPartnerName")
    void updatePartnerName(@Param("oldPartnerName") String oldPartnerName,
                           @Param("partnerName") String partnerName,
                           @Param("username") String username);
}
