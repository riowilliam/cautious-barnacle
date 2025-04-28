package com.fision.serviceImpl;

import com.fision.dto.*;
import com.fision.entity.primary.TbFacilityAssetTransaction;
import com.fision.entity.primary.TbPartner;
import com.fision.entity.primary.TbProject;
import com.fision.repository.primary.*;
import com.fision.service.ProjectService;
import com.fision.utils.ConstantsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LordDev
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    TbProjectRepository tbProjectRepository;

    @Autowired
    TbPartnerRepository tbPartneRepository;

    @Autowired
    TbArInvoiceRepository tbArInvoiceRepository;

    @Autowired
    TbCashOutRepository tbCashOutRepository;

    @Autowired
    TmpCashOutRepository tmpCashOutRepository;

    @Autowired
    TbFacilityAssetTransactionRepository tbFacilityAssetTransactionRepository;

    @Override
    public Page<ProjectListDto> getProjectListPaging(int pageNo, int pageSize, String sortBy, String sortOrder, String projectName, Integer status, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return tbProjectRepository.getProjectListPaging(projectName, status, startDate, endDate, pageable);
    }

    @Override
    public TbProject getProjectByProjectName(String projectName) {
        return tbProjectRepository.findByProjectName(projectName);
    }

    @Override
    public void saveProject(String username, ProjectRequestDto projectRequestDto) {
        TbProject project = new TbProject();
        project.setProjectName(projectRequestDto.getProjectName());
        project.setStatus(projectRequestDto.getStatus().equals(ConstantsUtils.ACTIVE) ? 1 : 0);
        project.setStartDate(projectRequestDto.getStartDate());
        project.setCreatedBy(username);
        project.setModifiedBy(username);
        tbProjectRepository.save(project);
    }

    @Override
    public void updateProject(String username, TbProject project, ProjectRequestDto projectRequestDto) {
        String oldProjectName = project.getProjectName();
        project.setProjectName(projectRequestDto.getProjectName());
        project.setStatus(projectRequestDto.getStatus().equals(ConstantsUtils.ACTIVE) ? 1 : 0);
        project.setStartDate(projectRequestDto.getStartDate());
        project.setModifiedBy(username);
        tbProjectRepository.save(project);
        if(!oldProjectName.equals(projectRequestDto.getProjectName())) {
            tbArInvoiceRepository.updateProjectName(oldProjectName, projectRequestDto.getProjectName(), username);
            tbCashOutRepository.updateProjectName(oldProjectName, projectRequestDto.getProjectName(), username);
            tmpCashOutRepository.updateProjectName(oldProjectName, projectRequestDto.getProjectName(), username);
            tbFacilityAssetTransactionRepository.updateProjectName(oldProjectName, projectRequestDto.getProjectName(), username);
        }
    }

    @Override
    public List<Map<String, Object>> getProjectList(String projectName, String partnerName) {
        if(partnerName != null) {
            TbPartner tbPartner = tbPartneRepository.findByPartnerName(partnerName);
            List<Long> projectIdList = tbPartner != null ? Arrays.stream(tbPartner.getActiveProject().split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .collect(Collectors.toList()) : null;
            return projectIdList != null ? tbProjectRepository.getProjectListWithProjectId(projectName, projectIdList) : null;
        } else {
            return tbProjectRepository.getProjectList(projectName);
        }

    }

    @Override
    public List<ProjectMonitoringDetailDto> getProjectMonitoringDetailList() {
        List<ProjectCashInProjection> cashInList = tbProjectRepository.getCashInData();
        List<ProjectCashOutProjection> cashOutList = tbProjectRepository.getCashOutData();

        Map<String, BigDecimal> cashInMap = cashInList.stream()
                .collect(Collectors.toMap(ProjectCashInProjection::getProjectName, ProjectCashInProjection::getCashInValue));

        Map<String, BigDecimal> cashOutMap = cashOutList.stream()
                .collect(Collectors.toMap(ProjectCashOutProjection::getProjectName, ProjectCashOutProjection::getCashOutValue));

        Set<String> projectNames = new HashSet<>();
        projectNames.addAll(cashInMap.keySet());
        projectNames.addAll(cashOutMap.keySet());

        List<ProjectMonitoringDetailDto> result = new ArrayList<>();
        for (String projectName : projectNames) {
            BigDecimal cashIn = cashInMap.getOrDefault(projectName, BigDecimal.ZERO);
            BigDecimal cashOut = cashOutMap.getOrDefault(projectName, BigDecimal.ZERO);
            result.add(new ProjectMonitoringDetailDto(projectName, cashIn, cashOut));
        }

        // Sort by projectName ascending
        result.sort(Comparator.comparing(ProjectMonitoringDetailDto::getProjectName));

        return result;
    }

    @Override
    public ProjectMonitoringSummaryDto getProjectMonitoringSummary() {
        String mostCashInProject = tbProjectRepository.findMostCashInProject();
        String mostCashOutProject = tbProjectRepository.findMostCashOutProject();
        String mostMvpProject = tbProjectRepository.findMvpProject();
        return new ProjectMonitoringSummaryDto(mostMvpProject, mostCashInProject, mostCashOutProject);
    }

}
