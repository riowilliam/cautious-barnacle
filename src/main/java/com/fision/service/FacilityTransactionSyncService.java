package com.fision.service;

import com.fision.dto.FacilityTransactionRequestDto;
import com.fision.entity.primary.TbFacilityAssetTransaction;

public interface FacilityTransactionSyncService {
    void syncDataOutSource();
    void calculateFacilityToCashOut();
    void saveFacilityTransaction(FacilityTransactionRequestDto requestDto, String username);
}
