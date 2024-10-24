package com.fision.service;

import com.fision.dto.CashOutDetailDto;
import com.fision.dto.CashOutDocListDto;
import com.fision.dto.CashOutListDto;
import com.fision.dto.CashOutMutationListDto;
import com.fision.entity.TbDocumentCashOut;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface CashOutService {
    String saveTmpCashOut(String username, CashOutListDto cashOutListDto);
    void editCashOutDoc(String username, CashOutListDto cashOutListDto);
    void approvalCashOutDoc(String username, Integer status, TbDocumentCashOut tbDocumentCashOut);
    TbDocumentCashOut getDocumentCashOut(String docName);
    CashOutListDto getCashOutListByDocName(String documentName);
    Page<CashOutMutationListDto> getCashOutMutationPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                                          String vendorName, String docName, Date startDate, Date endDate);
    Page<CashOutDocListDto> getCashOutDocPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                                     String docName, Integer status, Date startDate, Date endDate);
}
