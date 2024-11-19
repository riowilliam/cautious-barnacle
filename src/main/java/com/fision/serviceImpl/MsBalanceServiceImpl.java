package com.fision.serviceImpl;

import com.fision.entity.primary.MsBalance;
import com.fision.repository.primary.MsBalanceRepository;
import com.fision.service.MsBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author LordDev
 */
@Service
public class MsBalanceServiceImpl implements MsBalanceService {
    @Autowired
    MsBalanceRepository msBalanceRepository;

    @Override
    public void save(MsBalance msBalance) {
        msBalanceRepository.save(msBalance);
    }
}
