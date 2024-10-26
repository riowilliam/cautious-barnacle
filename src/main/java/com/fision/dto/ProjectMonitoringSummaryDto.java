package com.fision.dto;

import lombok.Data;

/**
 * @author LordDev
 */
@Data
public class ProjectMonitoringSummaryDto {
    String mvpProject;
    String mostCashInProject;
    String mostCashOutProject;

    public ProjectMonitoringSummaryDto(String mvpProject, String mostCashInProject, String mostCashOutProject) {
        this.mvpProject = mvpProject;
        this.mostCashInProject = mostCashInProject;
        this.mostCashOutProject = mostCashOutProject;
    }
}
