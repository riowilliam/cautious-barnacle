package com.fision.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author LordDev
 */
@Data
public class ProjectRequestDto {
    private String projectName;
    private Date startDate;
    private String status;
}
