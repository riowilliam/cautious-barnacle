package com.fision.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContractListDto {
    private String contractCode;
    List<ItemDetailsListDto> itemList;

    public ContractListDto(String contractCode, List<ItemDetailsListDto> itemList) {
        this.contractCode = contractCode;
        this.itemList = itemList;
    }
}
