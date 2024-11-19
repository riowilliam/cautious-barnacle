package com.fision.dto;

import com.fision.entity.primary.TbDocumentCashOut;
import lombok.Data;
import java.util.List;

@Data
public class CashOutDocListDto {
    List<TbDocumentCashOut> documentCashOutList;
    CashOutDocSummaryDto cashOutDocSummary;

    public CashOutDocListDto(List<TbDocumentCashOut> documentCashOutList, CashOutDocSummaryDto cashOutDocSummary) {
        this.documentCashOutList = documentCashOutList;
        this.cashOutDocSummary = cashOutDocSummary;
    }

}
