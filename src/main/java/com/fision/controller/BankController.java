package com.fision.controller;

import com.fision.dto.MsBankRequestDto;
import com.fision.dto.ResponseDto;
import com.fision.entity.primary.MsBank;
import com.fision.entity.primary.MsItem;
import com.fision.service.MsBankService;
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
@RequestMapping("/api/bank/")
@CrossOrigin
public class BankController {
    private static final Logger logger = LoggerFactory.getLogger(BankController.class);

    @Autowired
    MsBankService msBankService;

    @GetMapping("getBankListPaging")
    public ResponseDto<?> getItemListPaging(@RequestParam(defaultValue = "0") int pageNo,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(defaultValue = "createdTm") String sortBy,
                                            @RequestParam(defaultValue = "desc") String sortOrder,
                                            @RequestParam(required = false) String bankName) {
        try {
            Pageable pageable = PageRequest.of(pageNo, pageSize,
                    sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

            Page<MsBank> msBankList = msBankService.getBankListPaging(bankName != null && !bankName.isEmpty() ? bankName : null, pageable);

            return new ResponseDto<>(ConstantsUtils.SUCCESS, msBankList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("createBank")
    public ResponseDto<?> createBank(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            MsBankRequestDto requestBank = gson.fromJson(requestDto, MsBankRequestDto.class);
            MsBank bank = msBankService.getBankByName(requestBank.getBankName());
            if(bank != null) {
                return new ResponseDto<>(ConstantsUtils.ITEM_NAME_ALREADY_USED, HttpStatus.OK);
            } else {
                msBankService.saveMsBank(username, requestBank);
                return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("editBank")
    public ResponseDto<?> editBank(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            MsBankRequestDto requestBank = gson.fromJson(requestDto, MsBankRequestDto.class);
            MsBank bank = msBankService.getBankById(requestBank.getId());
            if (bank != null) {
                MsBank existingBank = msBankService.getBankByName(requestBank.getBankName());
                if(existingBank != null) {
                    return new ResponseDto<>(ConstantsUtils.ITEM_NAME_ALREADY_USED, HttpStatus.OK);
                }
                msBankService.updateMsBank(username, requestBank, bank);
                return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
            } else {
                return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
