package com.fision.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ContractRevisionListDto {
    private Integer revision;
    private String createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date addendumDate;
    List<ItemDetailsListDto> itemList;

    public ContractRevisionListDto(Integer revision, String createdBy, Date addendumDate, List<ItemDetailsListDto> itemList) {
        this.revision = revision;
        this.createdBy = createdBy;
        this.addendumDate = addendumDate;
        this.itemList = itemList;
    }
}
