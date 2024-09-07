package com.fision.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContractRequestDto {
    private String contractCode;
    private String contractName;
    private Integer revision;
    private List<ItemDetailsListDto> itemDetailList;
}
