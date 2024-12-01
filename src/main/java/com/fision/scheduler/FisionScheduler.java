package com.fision.scheduler;

import com.fision.service.FacilityTransactionSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FisionScheduler {
    @Autowired
    FacilityTransactionSyncService facilityTransactionSyncService;

    @Scheduled(cron = "0 */2 * * * ?")
    @Transactional
    public void transferData() {
        facilityTransactionSyncService.syncDataOutSource();
    }

}
