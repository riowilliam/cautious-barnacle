package com.fision.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContractListDto {
    private String contractCode;
    private String contractName;
    List<ItemDetailsListDto> itemList;

    public ContractListDto(String contractCode, String contractName, List<ItemDetailsListDto> itemList) {
        this.contractCode = contractCode;
        this.contractName = contractName;
        this.itemList = itemList;
    }
}
