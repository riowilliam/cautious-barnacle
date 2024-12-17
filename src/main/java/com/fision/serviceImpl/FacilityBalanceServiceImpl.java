package com.fision.serviceImpl;

import com.fision.dto.FacilityListDto;
import com.fision.dto.FacilityTransactionDto;
import com.fision.repository.primary.TbFacilityAssetTransactionRepository;
import com.fision.repository.primary.TbFacilityBalanceRepository;
import com.fision.service.FacilityBalanceService;
import com.fision.utils.ConstantsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class FacilityBalanceServiceImpl implements FacilityBalanceService {
    @Autowired
    TbFacilityAssetTransactionRepository tbFacilityAssetTransactionRepository;

    @Autowired
    TbFacilityBalanceRepository tbFacilityBalanceRepository;

    @Override
    public Page<FacilityListDto> getFacilityTransactionPaging(String vendorName, String facilityType, String projectNamme, String debitAdvice, boolean tenorDateOnWeekend, Date startDate, Date endDate,
                                                              int pageNo, int pageSize, String sortBy, String sortOrder) {

        // Membuat Pageable berdasarkan pageNo, pageSize, sortBy, dan sortOrder
        Pageable pageable = PageRequest.of(pageNo, pageSize,
                sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending());

        // Memanggil repository untuk mendapatkan data transaksi berdasarkan filter
        Page<FacilityTransactionDto> transactionPage = tbFacilityAssetTransactionRepository.findFacilityTransactions(
                pageable, vendorName, facilityType, projectNamme, debitAdvice, tenorDateOnWeekend, startDate, endDate);

        // Menghasilkan ringkasan fasilitas (summary)
        Map<String, Object> facilitySummary = generateFacilitySummary(vendorName, facilityType, ConstantsUtils.PAYMENT,
                tenorDateOnWeekend, startDate, endDate);

        // Membuat FacilityListDto dan memasukkan data transaksi dan ringkasan
        FacilityListDto facilityListDto = new FacilityListDto(transactionPage.getContent(), facilitySummary);

        // Membungkus FacilityListDto ke dalam Page dan mengembalikannya
        return new PageImpl<>(Collections.singletonList(facilityListDto), pageable, transactionPage.getTotalElements());
    }

    @Override
    public List<String> getFacilityBalanceTypeList() {
        return tbFacilityAssetTransactionRepository.getFacilityBalanceTypeList();
    }

    private Map<String, Object> generateFacilitySummary(String vendorName, String facilityType,
                                                        String transactionType, boolean tenorDateOnWeekend, Date startDate, Date endDate) {

        List<Object[]> summaryData = tbFacilityAssetTransactionRepository.findTransactionSummary(vendorName, facilityType,
                transactionType, tenorDateOnWeekend, startDate, endDate);

        Map<String, Object> summary = new HashMap<>();

        // Mengubah hasil query menjadi Map untuk summary
        for (Object[] row : summaryData) {
            String transactionTypeSummary = (String) row[0];
            BigDecimal totalAmount = (BigDecimal) row[1];
            summary.put(transactionTypeSummary, totalAmount);
        }

        // Collect Facility Type
        List<Map<String, Object>> facilityBalanceList = tbFacilityBalanceRepository.getBalanceDetail();
        summary.put("facilityBalanceList", facilityBalanceList);

        return summary;
    }
}
