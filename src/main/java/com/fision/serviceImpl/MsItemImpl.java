package com.fision.serviceImpl;

import com.fision.entity.primary.MsItem;
import com.fision.repository.primary.MsItemRepository;
import com.fision.repository.primary.TbItemDetailsRepository;
import com.fision.repository.primary.TxPaidItemRepository;
import com.fision.service.MsItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@Service
public class MsItemImpl implements MsItemService {

    @Autowired
    MsItemRepository msItemRepository;

    @Autowired
    TbItemDetailsRepository tbItemDetailsRepository;

    @Autowired
    TxPaidItemRepository txPaidItemRepository;

    @Override
    public Page<MsItem> getItemListPaging(String itemName, Pageable pageable) {
        return msItemRepository.getMsItemListPaging(itemName, pageable);
    }

    @Override
    public MsItem getItemByName(String itemName) {
        return msItemRepository.findByItemName(itemName);
    }

    @Override
    public MsItem getItemById(Long id) {
        return msItemRepository.findById(id).isPresent() ? msItemRepository.findById(id).get() : null;
    }

    @Override
    public void saveItem(String username, String itemName) {
        MsItem item = new MsItem();
        item.setItemName(itemName);
        item.setCreatedBy(username);
        item.setModifiedBy(username);
        msItemRepository.save(item);
    }

    @Override
    public void updateItem(String username, MsItem item, String newItemName) {
        String oldItemName = item.getItemName();
        item.setItemName(newItemName);
        item.setModifiedBy(username);
        msItemRepository.save(item);

        if(!oldItemName.equals(newItemName)) {
            tbItemDetailsRepository.updateItemName(oldItemName, newItemName, username);
            txPaidItemRepository.updateItemName(oldItemName, newItemName, username);
        }
    }

    @Override
    public List<Map<String, Object>> getItemList(String itemName) {
        return msItemRepository.getItemList(itemName);
    }
}
