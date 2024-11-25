package com.fision.repository.secondary;

import com.fision.entity.secondary.FisionOutSourceData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface FisionOutSourceDataRepository extends JpaRepository<FisionOutSourceData, Long> {
    @Query("SELECT f FROM FisionOutSourceData f WHERE f.transactionDate BETWEEN :startOfDay AND :endOfDay AND f.status = :status")
    Page<FisionOutSourceData> findByTransactionDateAndStatus(
            @Param("startOfDay") Date startOfDay,
            @Param("endOfDay") Date endOfDay,
            @Param("status") Integer status,
            Pageable pageable);
}
