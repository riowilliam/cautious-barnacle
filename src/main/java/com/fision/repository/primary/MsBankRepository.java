package com.fision.repository.primary;

import com.fision.entity.primary.MsBank;
import com.fision.entity.primary.MsItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    MsBank findByBankName(String bankName);

    @Query("SELECT mb FROM MsBank mb " +
            "WHERE (:bankName IS NULL OR mb.bankName LIKE %:bankName%) ")
    Page<MsBank> getMsBankListPaging(@Param("bankName") String bankName,
                                     Pageable pageable);
}
