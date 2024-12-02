package com.fision.service;

import com.fision.dto.PartnerListDto;
import com.fision.dto.PartnerRequestDto;
import com.fision.entity.primary.TbPartner;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
public interface PartnerService {
    Page<PartnerListDto> getPartnerListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                              String partnerName, Integer ppnWapu, Date startDate, Date endDate);
    TbPartner getPartnerByName(String partnerName);
    TbPartner getByPartnerId(Long partnerId);
    void savePartner(String username, PartnerRequestDto partnerRequestDto);
    void updatePartner(String username, TbPartner partner, PartnerRequestDto partnerRequestDto);
    List<Map<String, Object>> getPartnerList(String partnerName);
    List<Map<String, Object>> getPphList();
}
