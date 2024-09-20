package com.fision.controller;

import com.fision.dto.*;
import com.fision.entity.TbDocumentCashOut;
import com.fision.service.CashOutService;
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
@RequestMapping("/api/cashOut/")
@CrossOrigin
public class CashOutController {
    private static final Logger logger = LoggerFactory.getLogger(CashOutController.class);

    @Autowired
    CashOutService cashOutService;

    @PostMapping("createCashOutDoc")
    public ResponseDto<?> createCashOutDoc(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            CashOutListDto cashOutListDto= gson.fromJson(requestDto, CashOutListDto.class);
            cashOutService.saveTmpCashOut(username, cashOutListDto);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("editCashOutDoc")
    public ResponseDto<?> editCashOutDoc(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            CashOutListDto cashOutListDto= gson.fromJson(requestDto, CashOutListDto.class);
            cashOutService.editCashOutDoc(username, cashOutListDto);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getCashOutDocByName")
    public ResponseDto<?> getCashOutDocByName(@RequestParam String username, @RequestParam String docName) {
        try {
            if(docName == null || docName.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }
            CashOutListDto cashOutListDto = cashOutService.getCashOutListByDocName(docName);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, cashOutListDto, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("approvalCashOutDoc")
    public ResponseDto<?> approvalCashOutDoc(@RequestParam String username, @RequestParam Integer status, @RequestParam String docName) {
        try {
            if(status == null || (docName == null || docName.isEmpty())) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            TbDocumentCashOut tbDocumentCashOut = cashOutService.getDocumentCashOut(docName);
            if(tbDocumentCashOut == null) {
                return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            cashOutService.approvalCashOutDoc(username, status, tbDocumentCashOut);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getCashOutMutationPaging")
    public ResponseDto<?> getCashOutMutationPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdTm") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String documentCashOutName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            Page<CashOutMutationListDto> cashOutMutationListDtoPage = cashOutService.getCashOutMutationPaging(pageNo, pageSize, sortBy.equalsIgnoreCase("createdDate") ? "createdTm" : sortBy, sortOrder,
                    vendorName != null && !vendorName.isEmpty() ? vendorName : null,
                    documentCashOutName != null && !documentCashOutName.isEmpty() ? documentCashOutName : null,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDate(endDate) : null);

            return new ResponseDto<>(ConstantsUtils.SUCCESS, cashOutMutationListDtoPage, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getCashOutDocPaging")
    public ResponseDto<?> getCashOutDocPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdTm") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String documentName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            Page<CashOutDocListDto> cashOutMutationListDtoPage = cashOutService.getCashOutDocPaging(pageNo, pageSize, sortBy.equalsIgnoreCase("createdDate") ? "createdTm" : sortBy, sortOrder,
                    documentName != null && !documentName.isEmpty() ? documentName : null,
                    status,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDate(endDate) : null);

            return new ResponseDto<>(ConstantsUtils.SUCCESS, cashOutMutationListDtoPage, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
