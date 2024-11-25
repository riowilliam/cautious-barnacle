package com.fision.repository.primary;

import com.fision.entity.primary.TxPaidItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TxPaidItemRepository extends JpaRepository<TxPaidItem, Long> {
    @Query("SELECT COALESCE(SUM(tx.paidQuantity), 0) " +
            "FROM TxPaidItem tx " +
            "WHERE tx.contractNo = :contractNo " +
            "AND tx.itemName = :itemName ")
    Double getPaidQuantity(@Param("contractNo") String contractNo, @Param("itemName") String itemName);

    TxPaidItem findBycontractNoAndItemName(String contractNo, String itemName);
}
