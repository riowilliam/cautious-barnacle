package com.fision.serviceImpl;

import com.fision.entity.TbDocumentCashOut;
import com.fision.repository.TbDocumentCashOutRepository;
import com.fision.service.DocumentCashOutService;
import com.fision.utils.DateTimeHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DocumentCashOutServiceImpl implements DocumentCashOutService {

    @Value("${document.cash.out.prefix}")
    String documentCashOutPrefix;

    @Autowired
    TbDocumentCashOutRepository tbDocumentCashOutRepository;

    @Override
    public String generateDocumentName() {
        return documentCashOutPrefix + DateTimeHelper.nowToString();
    }

    @Override
    public void saveDocumentCashOut(String username, String documentName, BigDecimal totalAmount) {
        TbDocumentCashOut tbDocumentCashOut = new TbDocumentCashOut();
        tbDocumentCashOut.setDocumentName(documentName);
        tbDocumentCashOut.setCreatedBy(username);
        tbDocumentCashOut.setModifiedBy(username);
        tbDocumentCashOut.setTotalAmount(totalAmount);
        tbDocumentCashOut.setStatus(0);
        tbDocumentCashOutRepository.save(tbDocumentCashOut);
    }
}
