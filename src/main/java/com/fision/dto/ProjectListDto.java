package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fision.utils.ConstantsUtils;
import lombok.Data;

import java.util.Date;

/**
 * @author LordDev
 */
@Data
public class ProjectListDto {
    private String projectName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;
    private String status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdDate;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date modifiedDate;
    private String modifiedBy;

    public ProjectListDto(String projectName, Date startDate, Integer status, Date createdDate, String createdBy, Date modifiedDate, String modifiedBy) {
        this.projectName = projectName;
        this.startDate = startDate;
        this.status = status != null && status == 1 ? ConstantsUtils.ACTIVE : ConstantsUtils.INACTIVE;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
    }
}
