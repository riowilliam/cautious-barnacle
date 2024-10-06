package com.fision.serviceImpl;

import com.fision.dto.*;
import com.fision.entity.TbArInvoice;
import com.fision.entity.TbPartner;
import com.fision.repository.TbArInvoiceRepository;
import com.fision.service.ARInvoiceService;
import com.fision.service.PartnerService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

    @Override
    public void approvalInvoice(String username, Integer status, TbArInvoice arInvoice) {
        arInvoice.setInvoiceStatus(status);
        arInvoice.setModifiedBy(username);
        tbArInvoiceRepository.save(arInvoice);
    }

    @Override
    public TbArInvoice getInvoiceByInvoiceNo(String invoiceNo) {
        return tbArInvoiceRepository.findByInvoiceNo(invoiceNo);
    }

    @Override
    public Page<ARInvoiceListDto> getArInvoiceListPaging(int pageNo, int pageSize, String sortBy, String sortOrder, String partnerName, String projectName, Integer invoiceStatus, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        Page<ARInvoiceDetailDto> arInvoicePaging = tbArInvoiceRepository.getArInvoicePaging(partnerName, projectName, invoiceStatus, startDate, endDate, pageable);
        ARInvoiceSummaryDto arInvoiceSummaryDto = tbArInvoiceRepository.getArInvoiceSummary(partnerName, projectName, invoiceStatus, startDate, endDate);

        ARInvoiceListDto arInvoiceListDto = new ARInvoiceListDto(arInvoicePaging.getContent(), arInvoiceSummaryDto);
        return new PageImpl<>(Collections.singletonList(arInvoiceListDto), pageable, arInvoicePaging.getTotalElements());
    }

    @Override
    public List<ARInvoiceDetailDto> getArInvoiceList() {
        return tbArInvoiceRepository.getArInvoiceDetailList();
    }

}
