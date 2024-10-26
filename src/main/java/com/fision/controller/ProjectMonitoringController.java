package com.fision.controller;

import com.fision.dto.ProjectMonitoringDetailDto;
import com.fision.dto.ProjectMonitoringDetailListDto;
import com.fision.dto.ProjectMonitoringSummaryDto;
import com.fision.dto.ResponseDto;
import com.fision.service.ProjectService;
import com.fision.utils.ConstantsUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/projectMonitoring/")
@CrossOrigin
public class ProjectMonitoringController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectMonitoringController.class);

    @Autowired
    ProjectService projectService;

    @GetMapping("getProjectMonitoring")
    public ResponseDto<?> getProjectList() {
        try {
            List<ProjectMonitoringDetailDto> projectMonitoringDetailDtoList = projectService.getProjectMonitoringDetailList();
            ProjectMonitoringSummaryDto projectMonitoringSummaryDto = projectService.getProjectMonitoringSummary();
            ProjectMonitoringDetailListDto projectList = new ProjectMonitoringDetailListDto(projectMonitoringDetailDtoList, projectMonitoringSummaryDto);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, projectList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
