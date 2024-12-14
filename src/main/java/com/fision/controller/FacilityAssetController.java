package com.fision.controller;

import com.fision.dto.CashInRequestDto;
import com.fision.dto.FacilityListDto;
import com.fision.dto.FacilityTransactionRequestDto;
import com.fision.dto.ResponseDto;
import com.fision.entity.primary.MsItem;
import com.fision.service.FacilityBalanceService;
import com.fision.service.FacilityTransactionSyncService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import com.google.gson.Gson;
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

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/facilityBalance/")
@CrossOrigin
public class FacilityAssetController {
    private static final Logger logger = LoggerFactory.getLogger(FacilityAssetController.class);

    @Autowired
    FacilityBalanceService facilityBalanceService;

    @Autowired
    FacilityTransactionSyncService facilityTransactionSyncService;

    @GetMapping("getFacilityBalancePaging")
    public ResponseDto<?> getFacilityBalancePaging(@RequestParam(defaultValue = "0") int pageNo,
                                            @RequestParam(defaultValue = "10") int pageSize,
                                            @RequestParam(defaultValue = "createdTm") String sortBy,
                                            @RequestParam(defaultValue = "desc") String sortOrder,
                                            @RequestParam(required = false) String vendorName,
                                            @RequestParam(required = false) String facilityType,
                                            @RequestParam(required = false) String transactionType,
                                            @RequestParam(required = false) boolean tenorDateOnWeekend,
                                            @RequestParam(required = false) String startDate,
                                            @RequestParam(required = false) String endDate) {
        try {

            Page<FacilityListDto> facilityListDtoPage = facilityBalanceService.getFacilityTransactionPaging(vendorName != null && !vendorName.isEmpty() ? vendorName : null,
                    facilityType != null && !facilityType.isEmpty() ? facilityType : null,
                    transactionType != null && !transactionType.isEmpty() ? transactionType : null,
                    tenorDateOnWeekend,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDateAddOneDay(endDate) : null,
                    pageNo, pageSize, sortBy, sortOrder);

            return new ResponseDto<>(ConstantsUtils.SUCCESS, facilityListDtoPage, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getFacilityBalanceTypeList")
    public ResponseDto<?> getFacilityBalanceTypeList(@RequestParam String username) {
        try {
            List<String> facilityType = facilityBalanceService.getFacilityBalanceTypeList();
            return new ResponseDto<>(ConstantsUtils.SUCCESS, facilityType, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("createFacilityTransaction")
    public ResponseDto<?> createFacilityTransaction(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            FacilityTransactionRequestDto facilityTransactionRequestDto = gson.fromJson(requestDto, FacilityTransactionRequestDto.class);
            if(facilityTransactionRequestDto != null) {
                facilityTransactionSyncService.saveFacilityTransaction(facilityTransactionRequestDto, username);
                return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
            } else {
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
