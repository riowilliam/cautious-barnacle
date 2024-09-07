package com.fision.serviceImpl;

import com.fision.dto.CashOutDetailDto;
import com.fision.dto.CashOutListDto;
import com.fision.entity.TbCashOut;
import com.fision.entity.TbDocumentCashOut;
import com.fision.entity.TmpCashOut;
import com.fision.repository.TbCashOutRepository;
import com.fision.repository.TbDocumentCashOutRepository;
import com.fision.repository.TmpCashOutRepository;
import com.fision.service.CashOutService;
import com.fision.service.DocumentCashOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CashOutServiceImpl implements CashOutService {
    @Autowired
    TmpCashOutRepository tmpCashOutRepository;

    @Autowired
    TbDocumentCashOutRepository tbDocumentCashOutRepository;

    @Autowired
    TbCashOutRepository tbCashOutRepository;

    @Autowired
    DocumentCashOutService documentCashOutService;

    @Override
    @Transactional
    public void saveTmpCashOut(String username, CashOutListDto cashOutListDto) {
        String documentCashOutName = documentCashOutService.generateDocumentName();
        // Save tmpCashOutList
        List<TmpCashOut> tmpCashOutList = mapToTmpCashOutList(cashOutListDto.getCashOutDetailList(), username, documentCashOutName);
        tmpCashOutRepository.saveAll(tmpCashOutList);

        //Save documentCashOut
        documentCashOutService.saveDocumentCashOut(username, documentCashOutName, cashOutListDto.getSubTotal());
    }

    @Override
    @Transactional
    public void editCashOutDoc(String username, CashOutListDto cashOutListDto) {
        // Compare existing data with request
        List<TmpCashOut> existingData = tmpCashOutRepository.findByDocumentCashOutName(cashOutListDto.getDocumentName());
        List<TmpCashOut> tmpCashOutList = mapToTmpCashOutList(cashOutListDto.getCashOutDetailList(), username, cashOutListDto.getDocumentName());

        // Set result and save
        List<TmpCashOut> toBeDelete = existingData.stream()
                .filter(existing -> tmpCashOutList.stream()
                        .filter(newItem -> newItem.getCashOutId() != null)
                        .noneMatch(newItem -> existing.getCashOutId().equals(newItem.getCashOutId())))
                .collect(Collectors.toList());

        tmpCashOutRepository.deleteAll(toBeDelete);
        tmpCashOutRepository.saveAll(tmpCashOutList);
    }

    @Override
    @Transactional
    public void approvalCashOutDoc(String username, Integer status, TbDocumentCashOut tbDocumentCashOut) {
        // Copy List from TmpCashOut and save
        List<TmpCashOut> tmpCashOutData = tmpCashOutRepository.findByDocumentCashOutName(tbDocumentCashOut.getDocumentName());
        if(status == 1) { // Rejected
            List<TbCashOut> tbCashOutList = tmpCashOutData.stream()
                    .map(this::mapToTbCashOut)
                    .collect(Collectors.toList());

            tbCashOutRepository.saveAll(tbCashOutList);
        } else {
            tmpCashOutRepository.deleteAll(tmpCashOutData);
        }

        // Set status and save
        tbDocumentCashOut.setStatus(status);
        tbDocumentCashOut.setModifiedBy(username);
        tbDocumentCashOutRepository.save(tbDocumentCashOut);
    }

    @Override
    public TbDocumentCashOut getDocumentCashOut(String docName) {
        return tbDocumentCashOutRepository.findByDocumentName(docName);
    }

    @Override
    public CashOutListDto getTmpCashOutListByDocName(String documentName) {
        List<CashOutDetailDto> cashOutDetailDtoList = tmpCashOutRepository.getTmpCashOutDetailList(documentName);
        BigDecimal subTotal = tmpCashOutRepository.getSubTotal(documentName);

        return new CashOutListDto(cashOutDetailDtoList, subTotal, documentName);
    }

    private List<TmpCashOut> mapToTmpCashOutList(List<CashOutDetailDto> cashOutDetailDtoList, String username, String documentCashOutName) {
        return cashOutDetailDtoList.stream()
                .map(dto -> mapToTmpCashOut(dto, username, documentCashOutName))
                .collect(Collectors.toList());
    }

    private TmpCashOut mapToTmpCashOut(CashOutDetailDto dto, String username, String documentCashOutName) {
        TmpCashOut tmpCashOut = new TmpCashOut();
        if(dto.getIdTmpCashOut() != null) tmpCashOut.setCashOutId(dto.getIdTmpCashOut());
        tmpCashOut.setVendorName(dto.getVendorName());
        tmpCashOut.setProjectName(dto.getProjectName());
        tmpCashOut.setAmount(dto.getAmount());
        tmpCashOut.setInvoiceTitle(dto.getInvoice());
        tmpCashOut.setTransferFee(dto.getTransferFee());
        tmpCashOut.setTotal(dto.getTotalAmount());
        tmpCashOut.setDocumentCashOutName(documentCashOutName);
        tmpCashOut.setCreatedBy(username);
        tmpCashOut.setModifiedBy(username);
        return tmpCashOut;
    }

    private TbCashOut mapToTbCashOut(TmpCashOut tmpCashOut) {
        TbCashOut tbCashOut = new TbCashOut();

        // Copying fields from TmpCashOut to TbCashOut
        tbCashOut.setAmount(tmpCashOut.getAmount());
        tbCashOut.setInvoiceTitle(tmpCashOut.getInvoiceTitle());
        tbCashOut.setTransferFee(tmpCashOut.getTransferFee());
        tbCashOut.setTotal(tmpCashOut.getTotal());
        tbCashOut.setVendorName(tmpCashOut.getVendorName());
        tbCashOut.setProjectName(tmpCashOut.getProjectName());
        tbCashOut.setDocumentCashOutName(tmpCashOut.getDocumentCashOutName());
        tbCashOut.setCreatedBy(tmpCashOut.getCreatedBy());
        tbCashOut.setModifiedBy(tmpCashOut.getModifiedBy());
        tbCashOut.setCreatedTm(tmpCashOut.getCreatedTm());
        tbCashOut.setModifiedTm(tmpCashOut.getModifiedTm());

        return tbCashOut;
    }


}
