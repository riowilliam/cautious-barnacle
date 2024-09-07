package com.fision.controller;

import com.fision.dto.PartnerListDto;
import com.fision.dto.ProjectRequestDto;
import com.fision.dto.ResponseDto;
import com.fision.dto.VendorRequestDto;
import com.fision.entity.MsItem;
import com.fision.entity.TbProject;
import com.fision.entity.TbVendor;
import com.fision.service.VendorService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/vendor/")
@CrossOrigin
public class VendorController {
    private static final Logger logger = LoggerFactory.getLogger(VendorController.class);

    @Autowired
    VendorService vendorService;

    @PostMapping("createVendor")
    public ResponseDto<?> createVendor(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            VendorRequestDto vendorRequestDto = gson.fromJson(requestDto, VendorRequestDto.class);
            if(vendorRequestDto != null) {
                boolean vendorDataCheck = vendorService.vendorDataCheck(vendorRequestDto, null);
                if(!vendorDataCheck) {
                    vendorService.saveVendor(username, vendorRequestDto);
                    return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
                } else {
                    return new ResponseDto<>(ConstantsUtils.VENDOR_NAME_ALREADY_USED, HttpStatus.OK);
                }

            } else {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("editVendor")
    public ResponseDto<?> editVendor(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            VendorRequestDto vendorRequestDto = gson.fromJson(requestDto, VendorRequestDto.class);
            TbVendor tbVendor = vendorService.getVendorById(vendorRequestDto.getVendorId());
            if (vendorRequestDto != null && tbVendor != null) {
                boolean vendorDataCheck = vendorService.vendorDataCheck(vendorRequestDto, tbVendor);
                if(vendorDataCheck) {
                    return new ResponseDto<>(ConstantsUtils.VENDOR_NAME_ALREADY_USED, HttpStatus.OK);
                }
                vendorService.updateVendor(username, tbVendor, vendorRequestDto);
                return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
            } else {
                return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getVendorList")
    public ResponseDto<?> getVendorList(@RequestParam String username, @RequestParam String vendorName) {
        try {
            List<Map<String, Object>> vendorList = vendorService.getVendorList(vendorName != null && !vendorName.isEmpty() ? vendorName : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, vendorList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getVendorListPaging")
    public ResponseDto<?> getVendorListPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "vendorName") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String bankName,
            @RequestParam(required = false) String bankAccount,
            @RequestParam(required = false) String bankAccountName
    ) {
        try {
            Page<TbVendor> vendorListPage = vendorService.getVendorListPaging(
                    pageNo, pageSize, sortBy, sortOrder,
                    vendorName != null && !vendorName.isEmpty() ? vendorName : null,
                    bankName != null && !bankName.isEmpty() ? bankName : null,
                    bankAccount != null && !bankAccount.isEmpty() ? bankAccount : null,
                    bankAccountName != null && !bankAccountName.isEmpty() ? bankAccountName : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, vendorListPage, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
