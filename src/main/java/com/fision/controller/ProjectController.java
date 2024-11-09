package com.fision.controller;

import com.fision.dto.*;
import com.fision.entity.TbProject;
import com.fision.service.ProjectService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */

@RestController
@RequestMapping("/api/project/")
@CrossOrigin
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    ProjectService projectService;

    @GetMapping("getProjectListPaging")
    public ResponseDto<?> getProjectListPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdTm") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            Page<ProjectListDto> projectListPaging = projectService.getProjectListPaging(
                    pageNo, pageSize, sortBy.equalsIgnoreCase("createdDate") ? "createdTm" : sortBy, sortOrder,
                    projectName != null && !projectName.isEmpty() ? projectName : null, status,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDate(endDate) : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, projectListPaging, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("createProject")
    public ResponseDto<?> createProject(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            ProjectRequestDto projectRequestDto = gson.fromJson(requestDto, ProjectRequestDto.class);
            TbProject tbProject = projectService.getProjectByProjectName(projectRequestDto.getProjectName());
            if(tbProject != null) {
                return new ResponseDto<>(ConstantsUtils.PROJECT_NAME_ALREADY_USED, HttpStatus.OK);
            } else {
                projectService.saveProject(username, projectRequestDto);
                return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("editProject")
    public ResponseDto<?> editProject(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>("Invalid Request.", null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            ProjectRequestDto projectRequestDto = gson.fromJson(requestDto, ProjectRequestDto.class);
            TbProject tbProject = projectService.getProjectByProjectName(projectRequestDto.getProjectName());
            if(tbProject != null) {
                projectService.updateProject(username, tbProject, projectRequestDto);
                return new ResponseDto<>(ConstantsUtils.DATA_SAVED, HttpStatus.OK);
            } else {
                return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getProjectList")
    public ResponseDto<?> getProjectList(@RequestParam String username, @RequestParam String projectName, @RequestParam(required = false) String partnerName) {
        try {
            List<Map<String, Object>> partnerList = projectService.getProjectList(projectName != null && !projectName.isEmpty() ? projectName : null,
                    partnerName != null && !partnerName.isEmpty() ? partnerName : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, partnerList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
