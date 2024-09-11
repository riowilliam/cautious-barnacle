package com.fision.controller;

import com.fision.dto.ARInvoiceRequestDto;
import com.fision.dto.CashOutListDto;
import com.fision.dto.ResponseDto;
import com.fision.service.ARInvoiceService;
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

}
