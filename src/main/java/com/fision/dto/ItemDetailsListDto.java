package com.fision.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fision.utils.CustomDoubleSerializer;
import lombok.Data;

@Data
public class ItemDetailsListDto {
    private String itemName;
    @JsonSerialize(using = CustomDoubleSerializer.class)
    private Double totalQuantity;
    @JsonSerialize(using = CustomDoubleSerializer.class)
    private Double remainingQuantity;
    @JsonSerialize(using = CustomDoubleSerializer.class)
    private Double paidQuantity;
}
