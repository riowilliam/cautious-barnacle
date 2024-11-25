package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author LordDev
 */
@Data
public class ContractPagingListDto {
    private String contractNo;
    private String contractName;
    private String partnerName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date contractDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdDate;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date modifiedDate;
    private String modifiedBy;

    public ContractPagingListDto(String contractNo, String contractName, String partnerName, Date contractDate, Date createdDate, String createdBy, Date modifiedDate, String modifiedBy) {
        this.contractNo = contractNo;
        this.contractName = contractName;
        this.partnerName = partnerName;
        this.contractDate = contractDate;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
    }
}
