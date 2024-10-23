package com.fision.repository;

import com.fision.entity.MsBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author LordDev
 */
@Repository
public interface MsBalanceRepository extends JpaRepository<MsBalance, Long> {
}
