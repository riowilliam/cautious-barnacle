package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author LordDev
 */
@Data
public class PartnerRequestDto {
    private Long partnerId;
    private String partnerName;
    private Integer ppnWapu;
    private String activeProject;
}
