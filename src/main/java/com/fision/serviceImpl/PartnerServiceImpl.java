package com.fision.serviceImpl;

import com.fision.dto.PartnerListDto;
import com.fision.dto.PartnerRequestDto;
import com.fision.entity.primary.TbPartner;
import com.fision.repository.primary.TbArInvoiceRepository;
import com.fision.repository.primary.TbConfigRepository;
import com.fision.repository.primary.TbContractRepository;
import com.fision.repository.primary.TbPartnerRepository;
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
    TbPartnerRepository tbPartneRepository;

    @Autowired
    TbConfigRepository tbConfigRepository;

    @Autowired
    TbContractRepository tbContractRepository;

    @Autowired
    TbArInvoiceRepository tbArInvoiceRepository;

    @Override
    public Page<PartnerListDto> getPartnerListPaging(int pageNo, int pageSize, String sortBy, String sortOrder,
                                                     String partnerName, Integer ppnWapu,
                                                     Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());
        return tbPartneRepository.getPartnerListPaging(partnerName, ppnWapu, startDate, endDate, pageable);
    }

    @Override
    public TbPartner getPartnerByName(String partnerName) {
        return tbPartneRepository.findByPartnerName(partnerName);
    }

    @Override
    public TbPartner getByPartnerId(Long partnerId) {
        return tbPartneRepository.findByPartnerId(partnerId);
    }

    @Override
    public void savePartner(String username, PartnerRequestDto partnerRequestDto) {
        TbPartner partner = new TbPartner();
        partner.setPartnerName(partnerRequestDto.getPartnerName());
        partner.setIsPpnWapu(partnerRequestDto.getPpnWapu());
        partner.setActiveProject(partnerRequestDto.getActiveProject());
        partner.setPpnValue(new BigDecimal("0.11"));
        partner.setCreatedBy(username);
        partner.setModifiedBy(username);

        tbPartneRepository.save(partner);
    }

    @Override
    public void updatePartner(String username, TbPartner partner, PartnerRequestDto partnerRequestDto) {
        String oldPartnerName = partner.getPartnerName();
        partner.setPartnerName(partnerRequestDto.getPartnerName());
        partner.setIsPpnWapu(partnerRequestDto.getPpnWapu());
        partner.setActiveProject(partnerRequestDto.getActiveProject());
        partner.setModifiedBy(username);

        tbPartneRepository.save(partner);
        if(!partnerRequestDto.equals(partner.getPartnerName())) {
            tbArInvoiceRepository.updatePartnerName(oldPartnerName, partnerRequestDto.getPartnerName(), username);
            tbContractRepository.updatePartnerName(oldPartnerName, partnerRequestDto.getPartnerName(), username);
        }
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
