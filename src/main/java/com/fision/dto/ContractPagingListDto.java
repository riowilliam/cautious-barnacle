package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author LordDev
 */
@Data
public class ContractPagingListDto {
    private String contractCode;
    private String contractName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date createdDate;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date modifiedDate;
    private String modifiedBy;

    public ContractPagingListDto(String contractCode, String contractName, Date createdDate, String createdBy, Date modifiedDate, String modifiedBy) {
        this.contractCode = contractCode;
        this.contractName = contractName;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.modifiedDate = modifiedDate;
        this.modifiedBy = modifiedBy;
    }
}
