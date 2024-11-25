package com.fision.repository.primary;

import com.fision.entity.primary.TbItemDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbItemDetailsRepository extends JpaRepository<TbItemDetails, Long> {
    List<TbItemDetails> findByContractNoAndRevision(String contractNo, Integer revision);
    TbItemDetails findByContractNoAndRevisionAndItemName(String contractNo, Integer revision, String itemName);

    @Query("SELECT i FROM TbItemDetails i WHERE i.contractNo = :contractNo AND i.itemName = :itemName AND i.revision = " +
            "(SELECT MAX(t.revision) FROM TbContract t WHERE t.contractNo = :contractNo)")
    TbItemDetails findByContractNoAndMaxRevision(@Param("contractNo") String contractNo, @Param("itemName") String itemName);
}
