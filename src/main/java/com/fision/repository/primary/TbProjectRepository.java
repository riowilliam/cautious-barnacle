package com.fision.repository.primary;

import com.fision.dto.ProjectCashInProjection;
import com.fision.dto.ProjectCashOutProjection;
import com.fision.dto.ProjectListDto;
import com.fision.dto.ProjectMonitoringDetailDto;
import com.fision.entity.primary.TbProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@Repository
public interface TbProjectRepository extends JpaRepository<TbProject, Long> {
    @Query("SELECT new com.fision.dto.ProjectListDto (" +
            "pr.projectName, " +
            "pr.startDate, " +
            "pr.status, " +
            "pr.createdTm, " +
            "pr.createdBy, " +
            "pr.modifiedTm, " +
            "pr.modifiedBy) " +
            "FROM TbProject pr " +
            "WHERE (:projectName IS NULL OR pr.projectName LIKE %:projectName%) " +
            "AND (:status IS NULL OR pr.status = :status) " +
            "AND (:startDate is null OR pr.startDate >= :startDate) " +
            "AND (:endDate is null OR pr.startDate <= :endDate) ")
    Page<ProjectListDto> getProjectListPaging(@Param("projectName") String projectName,
                                              @Param("status") Integer status,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate,
                                              Pageable pageable);

    @Query("SELECT new map(pr.projectId as projectId, pr.projectName as projectName) FROM TbProject pr " +
            "WHERE (:projectName IS NULL OR pr.projectName LIKE %:projectName%) " +
            "AND pr.projectId IN :projectIds " /*+
            "AND pr.status = 1 "*/)
    List<Map<String, Object>> getProjectListWithProjectId(
            @Param("projectName") String projectName,
            @Param("projectIds") List<Long> projectIds);

    @Query("SELECT new map(pr.projectId as projectId, pr.projectName as projectName) FROM TbProject pr " +
            "WHERE (:projectName IS NULL OR pr.projectName LIKE %:projectName%) ")
    List<Map<String, Object>> getProjectList(
            @Param("projectName") String projectName);


    TbProject findByProjectName(String projectName);

    @Query("SELECT pr.projectName AS projectName, " +
            "COALESCE(SUM(COALESCE(ci.paymentAmount, 0)), 0) AS cashInValue " +
            "FROM TbProject pr " +
            "LEFT JOIN TbArInvoice ai ON pr.projectName = ai.projectName AND (ai.invoiceStatus = 1 OR ai.invoiceStatus IS NULL) " +
            "LEFT JOIN TbCashIn ci ON ai.invoiceNo = ci.invoiceNo AND ci.cashInStatus = 'Completed' " +
            "WHERE pr.status = 1 " +
            "GROUP BY pr.projectName")
    List<ProjectCashInProjection> getCashInData();

    @Query("SELECT pr.projectName AS projectName, " +
            "COALESCE(SUM(COALESCE(co.total, 0)), 0) AS cashOutValue " +
            "FROM TbProject pr " +
            "LEFT JOIN TbCashOut co ON pr.projectName = co.projectName " +
            "WHERE pr.status = 1 " +
            "GROUP BY pr.projectName")
    List<ProjectCashOutProjection> getCashOutData();


    // Proyek dengan total cash in terbanyak
    @Query(value = "SELECT pr.project_name " +
            "FROM tb_project pr " +
            "LEFT JOIN tb_ar_invoice ai ON pr.project_name = ai.project_name " +
            "LEFT JOIN tb_cash_in ci ON ai.invoice_no = ci.invoice_no AND ci.cash_in_status = 'Completed' " +
            "GROUP BY pr.project_name " +
            "ORDER BY COALESCE(SUM(ci.payment_amount), 0) DESC " +
            "LIMIT 1", nativeQuery = true)
    String findMostCashInProject();

    // Proyek dengan total cash out terbanyak
    @Query(value = "SELECT pr.project_name " +
            "FROM tb_project pr " +
            "LEFT JOIN tb_cash_out co ON pr.project_name = co.project_name " +
            "GROUP BY pr.project_name " +
            "ORDER BY COALESCE(SUM(co.amount), 0) DESC " +
            "LIMIT 1", nativeQuery = true)
    String findMostCashOutProject();


    // Proyek MVP berdasarkan selisih cash in dan cash out tertinggi
    @Query(value = "SELECT pr.project_name " +
            "FROM tb_project pr " +
            "LEFT JOIN tb_ar_invoice ai ON pr.project_name = ai.project_name " +
            "LEFT JOIN tb_cash_in ci ON ai.invoice_no = ci.invoice_no AND ci.cash_in_status = 'Completed' " +
            "LEFT JOIN tb_cash_out co ON pr.project_name = co.project_name " +
            "GROUP BY pr.project_name " +
            "ORDER BY (COALESCE(SUM(ci.payment_amount), 0) - COALESCE(SUM(co.amount), 0)) DESC " +
            "LIMIT 1", nativeQuery = true)
    String findMvpProject();

}
