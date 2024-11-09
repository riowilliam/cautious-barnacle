package com.fision.service;

import com.fision.dto.*;
import com.fision.entity.TbProject;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
public interface ProjectService {
    Page<ProjectListDto> getProjectListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                              String projectName, Integer status, Date startDate, Date endDate);
    TbProject getProjectByProjectName(String projectName);
    void saveProject(String username, ProjectRequestDto projectRequestDto);
    void updateProject(String username, TbProject project, ProjectRequestDto projectRequestDto);
    List<Map<String, Object>> getProjectList(String projectName, String partnerName);
    List<ProjectMonitoringDetailDto> getProjectMonitoringDetailList();
    ProjectMonitoringSummaryDto getProjectMonitoringSummary();
}
