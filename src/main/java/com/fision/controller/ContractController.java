package com.fision.controller;

import com.fision.dto.*;
import com.fision.entity.TbContract;
import com.fision.service.ContractService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/contract/")
@CrossOrigin
public class ContractController {
    private static final Logger logger = LoggerFactory.getLogger(ContractController.class);

    @Autowired
    ContractService contractService;

    @PostMapping("createContract")
    public ResponseDto<?> createContract(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            ContractRequestDto contractRequest = gson.fromJson(requestDto, ContractRequestDto.class);
            if(contractRequest != null) {
                contractService.saveContract(username, contractRequest);
                return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
            } else {
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("editContract")
    public ResponseDto<?> editContract(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            ContractRequestDto requestContract = gson.fromJson(requestDto, ContractRequestDto.class);
            if (requestContract.getContractCode() != null && !requestContract.getContractCode().isEmpty()) {
                TbContract tbContract = contractService.getContractByCodeAndRevision(requestContract.getContractCode(), requestContract.getRevision() - 1);
                if(tbContract == null) {
                    return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
                } else {
                    Boolean isDataDiff = contractService.checkExistingItemDetails(requestContract.getItemDetailList(), tbContract);
                    Boolean isAvailable =  contractService.checkRemainingQuantity(requestContract.getItemDetailList(), tbContract);
                    if((isDataDiff != null && isDataDiff) && (isAvailable != null && isAvailable)) {
                        contractService.updateContract(username, tbContract, requestContract);
                        return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
                    } else {
                        return new ResponseDto<>(Boolean.FALSE.equals(isDataDiff) ? ConstantsUtils.TOTAL_QUANTITY_EQUALS_WITH_EXISTING : ConstantsUtils.TOTAL_QUANTITY_SMALLER_THAN_PAID_QUANTITY, HttpStatus.BAD_REQUEST);
                    }
                }
            } else {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getContractListPaging")
    public ResponseDto<?> getContractListPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdTm") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String contractName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            Page<ContractPagingListDto> contractListPaging = contractService.getContractListPaging(
                    pageNo, pageSize, sortBy.equalsIgnoreCase("createdDate") ? "createdTm" : sortBy, sortOrder,
                    contractName != null && !contractName.isEmpty() ? contractName : null,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDate(endDate) : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, contractListPaging, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getContractList")
    public ResponseDto<?> getContractList(@RequestParam String username, @RequestParam String contractName, @RequestParam String contractCode) {
        try {
            List<ContractListDto> itemList = contractService.getContractList(contractName != null && !contractName.isEmpty() ? contractName : null,
                    contractCode != null && !contractCode.isEmpty() ? contractCode : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, itemList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getContractRevisionList")
    public ResponseDto<?> getContractRevisionList(@RequestParam String username, @RequestParam String contractCode) {
        try {
            List<ContractRevisionListDto> itemList = contractService.getContractRevisionList(contractCode != null && !contractCode.isEmpty() ? contractCode : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, itemList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
