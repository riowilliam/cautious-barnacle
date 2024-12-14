package com.fision.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ContractRequestDto {
    private String contractNo;
    private String contractName;
    private String partnerName;
    private Date contractDate;
    private Date endContractDate;
    private Date addendumDate;
    private Integer revision;
    private String activeProject;
    private List<ItemDetailsListDto> itemDetailList;
}
