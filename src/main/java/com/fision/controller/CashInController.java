package com.fision.controller;

import com.fision.dto.ARInvoiceRequestDto;
import com.fision.dto.CashOutListDto;
import com.fision.dto.ResponseDto;
import com.fision.service.CashInService;
import com.fision.utils.ConstantsUtils;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("createARInvoice")
    public ResponseDto<?> createARInvoice(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            ARInvoiceRequestDto arInvoiceRequestDto = gson.fromJson(requestDto, ARInvoiceRequestDto.class);
            if(arInvoiceRequestDto != null) {
                cashInService.saveArInvoice(username, arInvoiceRequestDto);
                return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
            } else {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
