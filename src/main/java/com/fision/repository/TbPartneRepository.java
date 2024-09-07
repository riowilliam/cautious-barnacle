package com.fision.repository;

import com.fision.dto.PartnerListDto;
import com.fision.entity.TbPartner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@Repository
public interface TbPartneRepository extends JpaRepository<TbPartner, Long> {
    @Query("SELECT new com.fision.dto.PartnerListDto (" +
            "pt.partnerName, " +
            "pt.validContractDate, " +
            "pt.invalidContractDate, " +
            "pt.documentTracking, " +
            "pt.isPpnWapu, " +
            "pt.activeProject, " +
            "pt.createdTm, " +
            "pt.createdBy, " +
            "pt.modifiedTm, " +
            "pt.modifiedBy) " +
            "FROM TbPartner pt " +
            "WHERE (:partnerName IS NULL OR pt.partnerName LIKE %:partnerName%) " +
            "AND (:documentTracking IS NULL OR pt.documentTracking = :documentTracking) " +
            "AND (:ppnWapu IS NULL OR pt.isPpnWapu = :ppnWapu) " +
            "AND (:startDate is null OR pt.createdTm >= :startDate) " +
            "AND (:endDate is null OR pt.createdTm <= :endDate) ")
    Page<PartnerListDto> getPartnerListPaging(@Param("partnerName") String partnerName,
                                              @Param("documentTracking") Integer documentTracking,
                                              @Param("ppnWapu") Integer ppnWapu,
                                              @Param("startDate") Date startDate,
                                              @Param("endDate") Date endDate,
                                              Pageable pageable);

    @Query("SELECT new map(pt.partnerId as partnerId, pt.partnerName as partnerName) FROM TbPartner pt " +
            "WHERE (:partnerName IS NULL OR pt.partnerName LIKE %:partnerName%) ")
    List<Map<String, Object>> getPartnerList(@Param("partnerName") String partnerName);

    TbPartner findByPartnerName(String partnerName);
}
