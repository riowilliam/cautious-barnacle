package com.fision.controller;

import com.fision.dto.ARInvoiceListDto;
import com.fision.dto.ARInvoiceRequestDto;
import com.fision.dto.ResponseDto;
import com.fision.entity.TbArInvoice;
import com.fision.entity.TbDocumentCashOut;
import com.fision.service.ARInvoiceService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/arInvoice/")
@CrossOrigin
public class ARInvoiceController {
    private static final Logger logger = LoggerFactory.getLogger(ARInvoiceController.class);

    @Autowired
    ARInvoiceService arInvoiceService;

    @PostMapping("createARInvoice")
    public ResponseDto<?> createARInvoice(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            ARInvoiceRequestDto arInvoiceRequestDto = gson.fromJson(requestDto, ARInvoiceRequestDto.class);
            if(arInvoiceRequestDto != null) {
                TbArInvoice sameInvoiceNo = arInvoiceService.getInvoiceByInvoiceNo(arInvoiceRequestDto.getInvoiceNo());
                if(sameInvoiceNo.getInvoiceStatus() != 2) {
                    return new ResponseDto<>(ConstantsUtils.INVOICE_NO_DUPLICATE, null, HttpStatus.BAD_REQUEST);
                }
                arInvoiceService.saveArInvoice(username, arInvoiceRequestDto);
                return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
            } else {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("approvalARInvoice")
    public ResponseDto<?> approvalARInvoice(@RequestParam String username, @RequestParam Integer status, @RequestParam String invoiceNo) {
        try {
            if(status == null || (invoiceNo == null || invoiceNo.isEmpty())) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            TbArInvoice tbArInvoice = arInvoiceService.getInvoiceByInvoiceNo(invoiceNo);
            if(tbArInvoice == null) {
                return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            arInvoiceService.approvalInvoice(username, status, tbArInvoice);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getArInvoicePaging")
    public ResponseDto<?> getArInvoicePaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdTm") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String partnerName,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) Integer invoiceStatus,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            Page<ARInvoiceListDto> arInvoiceListDtoPage = arInvoiceService.getArInvoiceListPaging(pageNo, pageSize, sortBy.equalsIgnoreCase("createdDate") ? "createdTm" : sortBy, sortOrder,
                    partnerName != null && !partnerName.isEmpty() ? partnerName : null,
                    projectName != null && !projectName.isEmpty() ? projectName : null,
                    invoiceStatus,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDate(endDate) : null);

            return new ResponseDto<>(ConstantsUtils.SUCCESS, arInvoiceListDtoPage, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
