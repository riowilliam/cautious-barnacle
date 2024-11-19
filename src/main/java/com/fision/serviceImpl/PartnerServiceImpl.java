package com.fision.serviceImpl;

import com.fision.dto.PartnerListDto;
import com.fision.dto.PartnerRequestDto;
import com.fision.entity.primary.TbPartner;
import com.fision.repository.primary.TbConfigRepository;
import com.fision.repository.primary.TbPartneRepository;
import com.fision.service.PartnerService;
import com.fision.utils.ConstantsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author LordDev
 */
@Service
public class PartnerServiceImpl implements PartnerService {
    @Autowired
    TbPartneRepository tbPartneRepository;

    @Autowired
    TbConfigRepository tbConfigRepository;

    @Override
    public Page<PartnerListDto> getPartnerListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                                     String partnerName, Integer documentTracking, Integer ppnWapu,
                                                     Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return tbPartneRepository.getPartnerListPaging(partnerName, documentTracking, ppnWapu, startDate, endDate, pageable);
    }

    @Override
    public TbPartner getPartnerByName(String partnerName) {
        return tbPartneRepository.findByPartnerName(partnerName);
    }

    @Override
    public void savePartner(String username, PartnerRequestDto partnerRequestDto) {
        TbPartner partner = new TbPartner();
        partner.setPartnerName(partnerRequestDto.getPartnerName());
        partner.setDocumentTracking(partnerRequestDto.getDocumentTracking());
        partner.setIsPpnWapu(partnerRequestDto.getPpnWapu());
        partner.setActiveProject(partnerRequestDto.getActiveProject());
        partner.setValidContractDate(partnerRequestDto.getValidContractDate());
        partner.setInvalidContractDate(partnerRequestDto.getInvalidContractDate());
        partner.setPpnValue(new BigDecimal(0.11));
        partner.setCreatedBy(username);
        partner.setModifiedBy(username);

        tbPartneRepository.save(partner);
    }

    @Override
    public void updatePartner(String username, TbPartner partner, PartnerRequestDto partnerRequestDto) {
        partner.setPartnerName(partnerRequestDto.getPartnerName());
        partner.setDocumentTracking(partnerRequestDto.getDocumentTracking());
        partner.setIsPpnWapu(partnerRequestDto.getPpnWapu());
        partner.setActiveProject(partnerRequestDto.getActiveProject());
        partner.setValidContractDate(partnerRequestDto.getValidContractDate());
        partner.setInvalidContractDate(partnerRequestDto.getInvalidContractDate());
        partner.setModifiedBy(username);

        tbPartneRepository.save(partner);
    }

    @Override
    public List<Map<String, Object>> getPartnerList(String partnerName) {
        return tbPartneRepository.getPartnerList(partnerName);
    }

    @Override
    public List<Map<String, Object>> getPphList() {
        return tbConfigRepository.findValueAndDescByKey(ConstantsUtils.PPH_LIST);
    }
}
