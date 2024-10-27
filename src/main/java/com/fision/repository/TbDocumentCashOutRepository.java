package com.fision.repository;

import com.fision.dto.CashOutDocSummaryDto;
import com.fision.dto.DashboardCardDetailsDto;
import com.fision.entity.TbDocumentCashOut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface TbDocumentCashOutRepository extends JpaRepository<TbDocumentCashOut, Long> {
    TbDocumentCashOut findByDocumentName(String documentName);
    @Query("SELECT dco FROM TbDocumentCashOut dco " +
            "WHERE (:docName IS NULL OR dco.documentName LIKE %:docName%) " +
            "AND (:status IS NULL OR dco.status = :status) " +
            "AND (:startDate is null OR dco.createdTm >= :startDate) " +
            "AND (:endDate is null OR dco.createdTm <= :endDate) ")
    Page<TbDocumentCashOut> getCashOutDocPaging(@Param("docName") String docName,
                                                @Param("status") Integer status,
                                                @Param("startDate") Date startDate,
                                                @Param("endDate") Date endDate,
                                                Pageable pageable);
    @Query("SELECT new com.fision.dto.CashOutDocSummaryDto( " +
            "COALESCE(SUM(CASE WHEN dco.status = 1 THEN dco.totalAmount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN dco.status = 0 THEN dco.totalAmount ELSE 0 END), 0), " +
            "COALESCE(SUM(CASE WHEN dco.status = 2 THEN dco.totalAmount ELSE 0 END), 0), " +
            "CAST(COUNT(CASE WHEN dco.status = 1 THEN dco END) AS integer), " +
            "CAST(COUNT(CASE WHEN dco.status = 0 THEN dco END) AS integer), " +
            "CAST(COUNT(CASE WHEN dco.status = 2 THEN dco END) AS integer)) " +
            "FROM TbDocumentCashOut dco " +
            "WHERE (:docName IS NULL OR dco.documentName LIKE %:docName%) " +
            "AND (:status IS NULL OR dco.status = :status) " +
            "AND (:startDate is null OR dco.createdTm >= :startDate) " +
            "AND (:endDate is null OR dco.createdTm <= :endDate) ")
    CashOutDocSummaryDto getSummary(@Param("docName") String docName,
                                    @Param("status") Integer status,
                                    @Param("startDate") Date startDate,
                                    @Param("endDate") Date endDate);

    @Query("SELECT new com.fision.dto.DashboardCardDetailsDto(" +
            "'Cash Out Document', " +
            "COALESCE(SUM(CASE WHEN dco.status = 1 THEN dco.totalAmount ELSE 0 END), 0), " +
            "CAST(SUM(CASE WHEN dco.status = 1 THEN 1 ELSE 0 END) AS int), " +
            "CAST(SUM(CASE WHEN dco.status = 0 THEN 1 ELSE 0 END) AS int)) " +
            "FROM TbDocumentCashOut dco " +
            "WHERE (:startDate IS NULL OR dco.createdTm >= :startDate) " +
            "AND (:endDate IS NULL OR dco.createdTm <= :endDate)")
    DashboardCardDetailsDto getCashOutDocCardDetail(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
