package com.fision.serviceImpl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fision.dto.*;
import com.fision.entity.primary.TbArInvoice;
import com.fision.entity.primary.TbItemDetails;
import com.fision.entity.primary.TbPartner;
import com.fision.entity.primary.TxPaidItem;
import com.fision.repository.primary.TbArInvoiceRepository;
import com.fision.repository.primary.TbItemDetailsRepository;
import com.fision.repository.primary.TxPaidItemRepository;
import com.fision.service.ARInvoiceService;
import com.fision.service.PartnerService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Autowired
    TbItemDetailsRepository tbItemDetailsRepository;

    @Autowired
    TxPaidItemRepository txPaidItemRepository;

    @Override
    @Transactional
    public void saveArInvoice(String username, ARInvoiceRequestDto arInvoiceRequestDto) {
        TbArInvoice tbArInvoice = new TbArInvoice();
        // Mapping request to TbArInvoice and save
        tbArInvoice.setInvoiceNo(arInvoiceRequestDto.getInvoiceNo());
        tbArInvoice.setProjectName(arInvoiceRequestDto.getProjectName());
        tbArInvoice.setPartnerName(arInvoiceRequestDto.getPartnerName());
        tbArInvoice.setContractNo(arInvoiceRequestDto.getContractNo());
        tbArInvoice.setBappNo(arInvoiceRequestDto.getBappNo());
        tbArInvoice.setBappDate(arInvoiceRequestDto.getBappDate());
        tbArInvoice.setTaxInvoiceNumber(arInvoiceRequestDto.getTaxInvoiceNumber());
        tbArInvoice.setInvoiceDate(arInvoiceRequestDto.getInvoiceDate());
        tbArInvoice.setRetention(arInvoiceRequestDto.getRetention());
        tbArInvoice.setDownPayment(arInvoiceRequestDto.getDownPayment());
        tbArInvoice.setProgress(arInvoiceRequestDto.getProgress());
        tbArInvoice.setDppAmount(arInvoiceRequestDto.getAmount());
        tbArInvoice.setPpnAmount(arInvoiceRequestDto.getPpn());
        tbArInvoice.setPphAmount(arInvoiceRequestDto.getPph());
        tbArInvoice.setTotalAmount(arInvoiceRequestDto.getTotalAmount());
        tbArInvoice.setDeduction(BigDecimal.ZERO); // Default 0 on create
        tbArInvoice.setPaidItemDetails(JsonHelper.convertListToJsonString(arInvoiceRequestDto.getItemDetails()));
        tbArInvoice.setInvoiceStatus(0);
        tbArInvoice.setNotes(arInvoiceRequestDto.getNote());
        tbArInvoice.setCreatedBy(username);
        tbArInvoice.setModifiedBy(username);
        tbArInvoiceRepository.save(tbArInvoice);
    }

    @Override
    public void save(TbArInvoice tbArInvoice) {
        tbArInvoiceRepository.save(tbArInvoice);
    }

    @Override
    @Transactional
    public void approvalInvoice(String username, Integer status, TbArInvoice arInvoice) throws JsonProcessingException {
        if(status == 1) {
            // Insert item to tx_paid_item
            ObjectMapper objectMapper = new ObjectMapper();
            List<ItemDetailsRequestDto> paymentItemDetails = objectMapper.readValue(
                    arInvoice.getPaidItemDetails(),
                    new TypeReference<List<ItemDetailsRequestDto>>() {}
            );
            updateAndSaveTxPaidItems(paymentItemDetails, arInvoice, username);
        }

        // Update tb_ar_invoice
        arInvoice.setInvoiceStatus(status);
        arInvoice.setModifiedBy(username);
        tbArInvoiceRepository.save(arInvoice);
    }

    @Override
    public TbArInvoice getInvoiceByInvoiceNo(String invoiceNo) {
        return tbArInvoiceRepository.findByInvoiceNo(invoiceNo);
    }

    @Override
    public Page<ARInvoiceListDto> getArInvoiceListPaging(int pageNo, int pageSize, String sortBy, String sortOrder, String partnerName, String projectName, String invoiceNo, Integer invoiceStatus, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        Page<ARInvoiceDetailDto> arInvoicePaging = tbArInvoiceRepository.getArInvoicePaging(partnerName, projectName, invoiceNo, invoiceStatus, startDate, endDate, pageable);
        ARInvoiceSummaryDto arInvoiceSummaryDto = getARInvoiceSummary(partnerName, projectName, invoiceNo, invoiceStatus, startDate, endDate);

        ARInvoiceListDto arInvoiceListDto = new ARInvoiceListDto(arInvoicePaging.getContent(), arInvoiceSummaryDto);
        return new PageImpl<>(Collections.singletonList(arInvoiceListDto), pageable, arInvoicePaging.getTotalElements());
    }

    @Override
    public List<ARInvoiceDetailDto> getArInvoiceList(String invoiceNo) {
        return tbArInvoiceRepository.getArInvoiceDetailList(invoiceNo);
    }

    private ARInvoiceSummaryDto getARInvoiceSummary(String partnerName, String projectName, String invoiceNo, Integer invoiceStatus,
                                                    Date startDate, Date endDate) {
        Object[] resultArray = (Object[]) tbArInvoiceRepository.getArInvoiceSummary(partnerName, projectName, invoiceNo, invoiceStatus, startDate, endDate);
        return new ARInvoiceSummaryDto(
                (BigDecimal) resultArray[0],  // total for invoice_status = 1
                (BigDecimal) resultArray[1],  // total for invoice_status = 0
                (BigDecimal) resultArray[2],  // total for invoice_status = 2
                (BigDecimal) resultArray[3],  // total completed payments
                (BigDecimal) resultArray[4]   // difference between totalAmount and incompleted payments
        );
    }

    private void updateAndSaveTxPaidItems(List<ItemDetailsRequestDto> itemDetailList, TbArInvoice tbArInvoice, String username) {
        List<TxPaidItem> paidItemList = new ArrayList<>();
        List<TbItemDetails> tbItemDetailsList = new ArrayList<>();
        for(ItemDetailsRequestDto dto : itemDetailList) {
            TxPaidItem txPaidItem = txPaidItemRepository.findBycontractNoAndItemName(tbArInvoice.getContractNo(), dto.getItemName());
            TbItemDetails tbItemDetails = tbItemDetailsRepository.findByContractNoAndMaxRevision(tbArInvoice.getContractNo(), dto.getItemName());
            if (txPaidItem != null) {
                txPaidItem.setPaidQuantity(txPaidItem.getPaidQuantity() + dto.getPaymentQuantity());
                txPaidItem.setModifiedBy(username);
            } else {
                txPaidItem = new TxPaidItem();
                txPaidItem.setInvoiceNo(tbArInvoice.getInvoiceNo());
                txPaidItem.setContractNo(tbArInvoice.getContractNo());
                txPaidItem.setPaidQuantity(dto.getPaymentQuantity());
                txPaidItem.setItemName(dto.getItemName());
                txPaidItem.setCreatedBy(username);
                txPaidItem.setModifiedBy(username);
            }

            tbItemDetails.setRemainingQuantity(tbItemDetails.getRemainingQuantity() - dto.getPaymentQuantity());
            tbItemDetails.setModifiedBy(username);

            paidItemList.add(txPaidItem);
            tbItemDetailsList.add(tbItemDetails);
        }

        txPaidItemRepository.saveAll(paidItemList);
    }
}
