package com.fision.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContractListDto {
    private String contractNo;
    private String contractName;
    List<ItemDetailsListDto> itemList;

    public ContractListDto(String contractNo, String contractName, List<ItemDetailsListDto> itemList) {
        this.contractNo = contractNo;
        this.contractName = contractName;
        this.itemList = itemList;
    }
}
