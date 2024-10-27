package com.fision.service;

import com.fision.dto.StatisticsDto;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

/**
 * @author LordDev
 */
public interface DashboardService {
    StatisticsDto getDashboardStats(String filterType, Date startDate, Date endDate);
}
