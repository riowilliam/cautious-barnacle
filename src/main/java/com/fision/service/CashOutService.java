package com.fision.service;

import com.fision.dto.CashOutDetailDto;
import com.fision.dto.CashOutListDto;
import com.fision.entity.TbDocumentCashOut;

import java.util.List;

public interface CashOutService {
    void saveTmpCashOut(String username, CashOutListDto cashOutListDto);
    void editCashOutDoc(String username, CashOutListDto cashOutListDto);
    void approvalCashOutDoc(String username, Integer status, TbDocumentCashOut tbDocumentCashOut);
    TbDocumentCashOut getDocumentCashOut(String docName);
    CashOutListDto getTmpCashOutListByDocName(String documentName);
}
