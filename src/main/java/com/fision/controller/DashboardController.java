package com.fision.controller;

import com.fision.dto.ResponseDto;
import com.fision.dto.StatisticsDto;
import com.fision.service.DashboardService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
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
@RequestMapping("/api/dashboard/")
@CrossOrigin
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    DashboardService dashboardService;

    @GetMapping("getStats")
    public ResponseDto<?> getStats(@RequestParam String filterType,
                                   @RequestParam(required = false) String startDate,
                                   @RequestParam(required = false) String endDate) {
        try {


            StatisticsDto statisticsDto = dashboardService.getDashboardStats(filterType, startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDate(endDate) : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, statisticsDto, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
