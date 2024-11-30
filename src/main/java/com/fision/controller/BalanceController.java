package com.fision.controller;

import com.fision.dto.BalanceListDto;
import com.fision.dto.ResponseDto;
import com.fision.service.MsBalanceService;
import com.fision.utils.ConstantsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/balance/")
@CrossOrigin
public class BalanceController {
    private static final Logger logger = LoggerFactory.getLogger(BalanceController.class);

    @Autowired
    MsBalanceService msBalanceService;

    @GetMapping("getPaymentBankList")
    public ResponseDto<?> getPaymentBankList(@RequestParam String username, @RequestParam String bankName) {
        try {
            List<BalanceListDto> balanceList = msBalanceService.getBalanceList(bankName != null && !bankName.isEmpty() ? bankName : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, balanceList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
