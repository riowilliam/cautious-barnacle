package com.fision.controller;

import com.fision.dto.ResponseDto;
import com.fision.entity.MsItem;
import com.fision.service.MsItemService;
import com.fision.utils.ConstantsUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/item/")
@CrossOrigin
public class ItemsController {
    private static final Logger logger = LoggerFactory.getLogger(ItemsController.class);

    @Autowired
    MsItemService msItemService;

    @GetMapping("getItemListPaging")
    public ResponseDto<?> getItemListPaging(@RequestParam(defaultValue = "0") int pageNo,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(defaultValue = "itemName") String sortBy,
                                            @RequestParam(defaultValue = "asc") String sortOrder,
                                            @RequestParam(required = false) String itemName) {
        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize,
                    sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

            Page<MsItem> msItemListPaging = msItemService.getItemListPaging(itemName != null && !itemName.isEmpty() ? itemName : null, pageable);

            return new ResponseDto<>(ConstantsUtils.SUCCESS, msItemListPaging, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("createItem")
    public ResponseDto<?> createItem(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            JsonObject requestItem = gson.fromJson(requestDto, JsonObject.class);
            MsItem item = msItemService.getItemByName(requestItem.get("itemName").getAsString());
            if(item != null) {
                return new ResponseDto<>(ConstantsUtils.ITEM_NAME_ALREADY_USED, HttpStatus.OK);
            } else {
                msItemService.saveItem(username, requestItem.get("itemName").getAsString());
                return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("editItem")
    public ResponseDto<?> editItem(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            JsonObject requestItem = gson.fromJson(requestDto, JsonObject.class);
            MsItem item = msItemService.getItemById(requestItem.get("id").getAsLong());
            if (item != null) {
                MsItem existingItem = msItemService.getItemByName(requestItem.get("itemName").getAsString());
                if(existingItem != null) {
                    return new ResponseDto<>(ConstantsUtils.ITEM_NAME_ALREADY_USED, HttpStatus.OK);
                }
                msItemService.updateItem(username, item, requestItem.get("itemName").getAsString());
                return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
            } else {
                return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getItemList")
    public ResponseDto<?> getItemList(@RequestParam String username, @RequestParam String itemName) {
        try {
            List<Map<String, Object>> itemList = msItemService.getItemList(itemName != null && !itemName.isEmpty() ? itemName : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, itemList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
