package com.fision.repository.primary;

import com.fision.entity.primary.MsBank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MsBankRepository extends JpaRepository<MsBank, Long> {
    @Query("SELECT mb.bankCode as bankCode, mb.bankName as bankName, mb.bankShortName as bankShortName " +
            "FROM MsBank mb " +
            "WHERE (:bankShortName IS NULL OR mb.bankShortName LIKE %:bankShortName%) " +
            "OR (:bankName IS NULL OR mb.bankName LIKE %:bankName%) ")
    List<Map<String, Object>> getBankList(String bankShortName, String bankName);
}
