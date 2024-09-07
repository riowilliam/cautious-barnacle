package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author LordDev
 */
@Data
public class PartnerRequestDto {
    private String partnerName;
    private Date validContractDate;
    private Date invalidContractDate;
    private Integer documentTracking;
    private Integer ppnWapu;
    private String activeProject;
}
