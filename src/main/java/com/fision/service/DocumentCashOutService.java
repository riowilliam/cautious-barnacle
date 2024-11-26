package com.fision.service;

import java.math.BigDecimal;

public interface DocumentCashOutService {
    String generateDocumentName();
    void saveDocumentCashOut(String username, String documentName, BigDecimal totalAmount, String bankcode);
}
