package com.fision.dto;

import lombok.Data;

@Data
public class ItemDetailsListDto {
    private Long itemId;
    private String itemName;
    private Integer totalQuantity;
    private Integer remainingQuantity;
    private Integer paidQuantity;
}
