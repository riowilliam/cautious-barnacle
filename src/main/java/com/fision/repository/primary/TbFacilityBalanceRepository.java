package com.fision.repository.primary;

import com.fision.entity.primary.TbFacilityBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface TbFacilityBalanceRepository extends JpaRepository<TbFacilityBalance, Long> {
    @Query("SELECT new map(f.facilityType as facilityType, f.amount as currentBalance) FROM TbFacilityBalance f")
    List<Map<String, Object>> getBalanceDetail();
}
