package com.fision.dto;

import lombok.Data;

import java.util.List;

/**
 * @author LordDev
 */
@Data
public class ProjectMonitoringDetailListDto {
    List<ProjectMonitoringDetailDto> projectMonitoringDetailDtoList;
    ProjectMonitoringSummaryDto projectMonitoringSummaryDto;

    public ProjectMonitoringDetailListDto(List<ProjectMonitoringDetailDto> projectMonitoringDetailDtoList, ProjectMonitoringSummaryDto projectMonitoringSummaryDto) {
        this.projectMonitoringDetailDtoList = projectMonitoringDetailDtoList;
        this.projectMonitoringSummaryDto = projectMonitoringSummaryDto;
    }
}
