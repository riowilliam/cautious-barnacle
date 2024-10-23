package com.fision.controller;

import com.fision.dto.*;
import com.fision.entity.TbArInvoice;
import com.fision.entity.TbCashIn;
import com.fision.service.ARInvoiceService;
import com.fision.service.CashInService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import com.google.gson.Gson;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/cashIn/")
@CrossOrigin
public class CashInController {
    private static final Logger logger = LoggerFactory.getLogger(CashInController.class);

    @Autowired
    CashInService cashInService;

    @Autowired
    ARInvoiceService arInvoiceService;

    @GetMapping("getCashInPaging")
    public ResponseDto<?> getCashInPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdTm") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String partnerName,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            Page<CashInListDto> cashInDetailDtos = cashInService.getCashInListPaging(pageNo, pageSize, sortBy.equalsIgnoreCase("createdDate") ? "createdTm" : sortBy, sortOrder,
                    partnerName != null && !partnerName.isEmpty() ? partnerName : null,
                    projectName != null && !projectName.isEmpty() ? projectName : null,
                    paymentType != null && !paymentType.isEmpty() ? paymentType.equalsIgnoreCase(ConstantsUtils.FULLY_PAYMENT) ? 1 : 2 : null,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDate(endDate) : null);

            return new ResponseDto<>(ConstantsUtils.SUCCESS, cashInDetailDtos, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("createCashIn")
    public ResponseDto<?> createCashIn(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            CashInRequestDto cashInRequestDto = gson.fromJson(requestDto, CashInRequestDto.class);
            if(cashInRequestDto != null) {
                TbArInvoice arInvoice = arInvoiceService.getInvoiceByInvoiceNo(cashInRequestDto.getInvoiceNo());
                BigDecimal incompletedPayment = cashInService.getTotalIncompletedCashIByInvoiceNo(cashInRequestDto.getInvoiceNo());
                if(arInvoice != null) {
                    if (arInvoice.getTotalAmount().compareTo(cashInRequestDto.getPaymentAmount()) < 0) {
                        return new ResponseDto<>(ConstantsUtils.PAYMENT_TOTAL_LESS_THAN_AMOUNT, HttpStatus.BAD_REQUEST);
                    } else if ((cashInRequestDto.getPaymentAmount().add(incompletedPayment)).compareTo(arInvoice.getTotalAmount()) > 0) {
                        return new ResponseDto<>(ConstantsUtils.THERE_ARE_INCOMPLETE_PAYMENT, HttpStatus.BAD_REQUEST);
                    } else {
                        cashInService.saveCashIn(cashInRequestDto, arInvoice, username);
                    }
                }
                return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
            } else {
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("completeCashIn")
    public ResponseDto<?> completeCashIn(@RequestParam String username, @RequestParam Long cashInId) {
        try {
            if(cashInId == null) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }
            TbCashIn tbCashIn = cashInService.getTbCashInById(cashInId);
            if(tbCashIn != null) {
                tbCashIn.setCashInStatus(ConstantsUtils.COMPLETED);
                tbCashIn.setModifiedBy(username);
                cashInService.save(tbCashIn);
                return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
            } else {
                return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getArInvoiceList")
    public ResponseDto<?> getArInvoiceList(@RequestParam String username, @RequestParam String invoiceNo) {
        try {
            List<ARInvoiceDetailDto> arInvoiceDetailList = arInvoiceService.getArInvoiceList(invoiceNo != null && !invoiceNo.isEmpty() ? invoiceNo : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, arInvoiceDetailList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
