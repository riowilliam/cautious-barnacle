package com.fision.serviceImpl;

import com.fision.dto.ARInvoiceRequestDto;
import com.fision.entity.TbArInvoice;
import com.fision.entity.TbPartner;
import com.fision.repository.TbArInvoiceRepository;
import com.fision.service.ARInvoiceService;
import com.fision.service.PartnerService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;

/**
 * @author LordDev
 */
@Service
public class ARInvoiceServiceImpl implements ARInvoiceService {
    @Autowired
    TbArInvoiceRepository tbArInvoiceRepository;

    @Autowired
    PartnerService partnerService;

    @Override
    @Transactional
    public void saveArInvoice(String username, ARInvoiceRequestDto arInvoiceRequestDto) {
        TbPartner tbPartner = partnerService.getPartnerByName(arInvoiceRequestDto.getPartnerName());

        TbArInvoice tbArInvoice = new TbArInvoice();
        // Mapping request to TbArInvoice and save
        tbArInvoice.setInvoiceNo(arInvoiceRequestDto.getInvoiceNo());
        tbArInvoice.setProjectName(arInvoiceRequestDto.getProjectName());
        tbArInvoice.setPartnerName(arInvoiceRequestDto.getPartnerName());
        tbArInvoice.setContractCode(arInvoiceRequestDto.getContractName());
        tbArInvoice.setBappNo(arInvoiceRequestDto.getBappNo());
        tbArInvoice.setDppAmount(arInvoiceRequestDto.getAmount());
        tbArInvoice.setPpnAmount(arInvoiceRequestDto.getPpn());
        tbArInvoice.setPphAmount(arInvoiceRequestDto.getPph());
        tbArInvoice.setTotalAmount(arInvoiceRequestDto.getTotalAmount());
        tbArInvoice.setDeduction(BigDecimal.ZERO); // Default 0 on create
        tbArInvoice.setPaidItemDetails(JsonHelper.convertListToJsonString(arInvoiceRequestDto.getItemDetails()));
        tbArInvoice.setInvoiceStatus(0);
        tbArInvoice.setDocumentTracking(tbPartner.getDocumentTracking() == 1 ? ConstantsUtils.DOC_TRACKING_SUBMITTED : ConstantsUtils.DOC_TRACKING_ON_PROCESS);
        tbArInvoice.setCreatedBy(username);
        tbArInvoice.setModifiedBy(username);
        tbArInvoiceRepository.save(tbArInvoice);
    }

    @Override
    public void editArInvoice(String username, TbArInvoice tbArInvoice, ARInvoiceRequestDto arInvoiceRequestDto) {
        TbPartner tbPartner = partnerService.getPartnerByName(arInvoiceRequestDto.getPartnerName());

        // Mapping request to TbArInvoice and update
        tbArInvoice.setInvoiceNo(arInvoiceRequestDto.getInvoiceNo());
        tbArInvoice.setProjectName(arInvoiceRequestDto.getProjectName());
        tbArInvoice.setPartnerName(arInvoiceRequestDto.getPartnerName());
        tbArInvoice.setContractCode(arInvoiceRequestDto.getContractName());
        tbArInvoice.setBappNo(arInvoiceRequestDto.getBappNo());
        tbArInvoice.setDppAmount(arInvoiceRequestDto.getAmount());
        tbArInvoice.setPpnAmount(arInvoiceRequestDto.getPpn());
        tbArInvoice.setPphAmount(arInvoiceRequestDto.getPph());
        tbArInvoice.setTotalAmount(arInvoiceRequestDto.getTotalAmount());
        tbArInvoice.setDeduction(BigDecimal.ZERO); // Default 0 on create
        tbArInvoice.setPaidItemDetails(JsonHelper.convertListToJsonString(arInvoiceRequestDto.getItemDetails()));
        tbArInvoice.setInvoiceStatus(0);
        tbArInvoice.setDocumentTracking(tbPartner.getDocumentTracking() == 1 ? ConstantsUtils.DOC_TRACKING_SUBMITTED : ConstantsUtils.DOC_TRACKING_ON_PROCESS);
        tbArInvoice.setCreatedBy(username);
        tbArInvoice.setModifiedBy(username);
        tbArInvoiceRepository.save(tbArInvoice);
    }
}
