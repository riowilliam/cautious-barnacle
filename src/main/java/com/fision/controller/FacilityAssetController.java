package com.fision.controller;

import com.fision.dto.FacilityListDto;
import com.fision.dto.ResponseDto;
import com.fision.entity.primary.MsItem;
import com.fision.service.FacilityBalanceService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
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
}
