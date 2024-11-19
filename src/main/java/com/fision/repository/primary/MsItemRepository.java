package com.fision.repository.primary;

import com.fision.entity.primary.MsItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@Repository
public interface MsItemRepository extends JpaRepository<MsItem, Long> {
    @Query("SELECT mi FROM MsItem mi " +
            "WHERE (:itemName IS NULL OR mi.itemName LIKE %:itemName%) ")
    Page<MsItem> getMsItemListPaging(@Param("itemName") String itemName,
                                     Pageable pageable);
    MsItem findByItemName(String itemName);

    @Query("SELECT new map(mi.itemId as itemId, mi.itemName as itemName) FROM MsItem mi " +
            "WHERE (:itemName IS NULL OR mi.itemName LIKE %:itemName%) ")
    List<Map<String, Object>> getItemList(@Param("itemName") String itemName);
}
