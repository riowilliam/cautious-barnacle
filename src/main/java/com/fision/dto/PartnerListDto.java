package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fision.utils.ConstantsUtils;
import lombok.Data;

import java.util.Date;

/**
 * @author LordDev
 */
@Data
public class PartnerListDto {
    private Long partnerId;
    private String partnerName;
    private String ppnWapu;
    private String[] activeProject;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date createdDate;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Jakarta")
    private Date modifiedDate;
    private String modifiedBy;

    public PartnerListDto(Long partnerId, String partnerName,
                          Integer ppnWapu, String activeProject, Date createdDate, String createdBy,
                          Date modifiedDate, String modifiedBy) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.ppnWapu = ppnWapu == 1 ? ConstantsUtils.YES : ConstantsUtils.NO;
        this.activeProject = activeProject != null && activeProject.contains(",") ? activeProject.split(",") : new String[] {activeProject};
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
    }
}
