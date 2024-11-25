package com.fision.controller;

import com.fision.dto.*;
import com.fision.entity.primary.TbPartner;
import com.fision.service.PartnerService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */

@RestController
@RequestMapping("/api/partner/")
@CrossOrigin
public class PartnerController {
    private static final Logger logger = LoggerFactory.getLogger(PartnerController.class);

    @Autowired
    PartnerService partnerService;

    @GetMapping("getPartnerListPaging")
    public ResponseDto<?> getProjectListPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdTm") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String partnerName,
            @RequestParam(required = false) Integer ppnWapu,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            Page<PartnerListDto> partnerListDtoPage = partnerService.getPartnerListPaging(
                    pageNo, pageSize, sortBy.equalsIgnoreCase("createdDate") ? "createdTm" : sortBy, sortOrder,
                    partnerName != null && !partnerName.isEmpty() ? partnerName : null,
                    ppnWapu,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDateAddOneDay(endDate) : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, partnerListDtoPage, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("createPartner")
    public ResponseDto<?> createPartner(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            PartnerRequestDto partnerRequestDto = gson.fromJson(requestDto, PartnerRequestDto.class);
            TbPartner tbPartner = partnerService.getPartnerByName(partnerRequestDto.getPartnerName());
            if(tbPartner != null) {
                return new ResponseDto<>(ConstantsUtils.PARTNER_NAME_ALREADY_USED, HttpStatus.OK);
            } else {
                partnerService.savePartner(username, partnerRequestDto);
                return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("editPartner")
    public ResponseDto<?> editPartner(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            PartnerRequestDto partnerRequestDto = gson.fromJson(requestDto, PartnerRequestDto.class);
            TbPartner tbPartner = partnerService.getPartnerByName(partnerRequestDto.getPartnerName());
            if(tbPartner != null) {
                partnerService.updatePartner(username, tbPartner, partnerRequestDto);
                return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
            } else {
                return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getPartnerList")
    public ResponseDto<?> getPartnerList(@RequestParam String username, @RequestParam String partnerName) {
        try {
            List<Map<String, Object>> partnerList = partnerService.getPartnerList(partnerName != null && !partnerName.isEmpty() ? partnerName : null);
            List<Map<String, Object>> pphList = partnerService.getPphList();
            Map<String, Object> response = new HashMap<>();
            response.put("partnerList", partnerList);
            response.put("pphList", pphList);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, response, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
