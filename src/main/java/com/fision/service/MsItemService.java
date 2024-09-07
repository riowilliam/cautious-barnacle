package com.fision.service;

import com.fision.entity.MsItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
public interface MsItemService {
    Page<MsItem> getItemListPaging(String itemName, Pageable pageable);
    MsItem getItemByName(String itemName);
    MsItem getItemById(Long id);
    void saveItem(String username, String itemName);
    void updateItem(String username, MsItem item, String newItemName);

    List<Map<String, Object>> getItemList(String itemName);
}
