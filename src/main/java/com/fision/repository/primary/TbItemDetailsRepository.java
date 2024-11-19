package com.fision.repository.primary;

import com.fision.entity.primary.TbItemDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TbItemDetailsRepository extends JpaRepository<TbItemDetails, Long> {
    List<TbItemDetails> findByContractCodeAndRevision(String contractCode, Integer revision);
    TbItemDetails findByContractCodeAndRevisionAndItemName(String contractCode, Integer revision, String itemName);

    @Query("SELECT i FROM TbItemDetails i WHERE i.contractCode = :contractCode AND i.itemName = :itemName AND i.revision = " +
            "(SELECT MAX(t.revision) FROM TbContract t WHERE t.contractCode = :contractCode)")
    TbItemDetails findByContractCodeAndMaxRevision(@Param("contractCode") String contractCode, @Param("itemName") String itemName);
}
