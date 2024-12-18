package com.fision.serviceImpl;

import com.fision.dto.FacilityTransactionRequestDto;
import com.fision.entity.primary.TbCashOut;
import com.fision.entity.primary.TbFacilityAssetTransaction;
import com.fision.entity.primary.TbFacilityBalance;
import com.fision.entity.primary.TbSchedulerStatus;
import com.fision.entity.secondary.FisionOutSourceData;
import com.fision.repository.primary.TbCashOutRepository;
import com.fision.repository.primary.TbFacilityAssetTransactionRepository;
import com.fision.repository.primary.TbFacilityBalanceRepository;
import com.fision.repository.primary.TbSchedulerStatusRepository;
import com.fision.repository.secondary.FisionOutSourceDataRepository;
import com.fision.service.FacilityTransactionSyncService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FacilityTransactionSyncServiceImpl implements FacilityTransactionSyncService {

    private static final Logger logger = LoggerFactory.getLogger(FacilityTransactionSyncService.class);

    @Autowired
    FisionOutSourceDataRepository fisionOutSourceDataRepository;

    @Autowired
    TbFacilityAssetTransactionRepository tbFacilityAssetTransactionRepository;

    @Autowired
    TbFacilityBalanceRepository tbFacilityBalanceRepository;

    @Autowired
    TbSchedulerStatusRepository tbSchedulerStatusRepository;

    @Autowired
    TbCashOutRepository tbCashOutRepository;

    @Value("${batch.data.count}")
    int batchDataSize;

    @Override
    @Transactional
    public void syncDataOutSource() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        Pageable pageable = PageRequest.of(0, batchDataSize);
        TbSchedulerStatus tbSchedulerStatus = tbSchedulerStatusRepository.findBySchedulerName(ConstantsUtils.FACILITY_TRANSACTION_SCHEDULER);

        if (tbSchedulerStatus != null && !tbSchedulerStatus.getIsRunning()) {
            startScheduler(tbSchedulerStatus);
            try {
                processPendingData(startOfDay, endOfDay, pageable);
                updateSchedulerStatus(tbSchedulerStatus, false, "Proses sync selesai.");
            } catch (Exception e) {
                logger.error("Error during sync process", e);
                updateSchedulerStatus(tbSchedulerStatus, false, "Proses sync gagal.");
            }
        } else {
            logger.warn("Nama schedule " + ConstantsUtils.FACILITY_TRANSACTION_SCHEDULER + " tidak ditemukan, mohon cek data pada tb_scheduler_status.");
        }
    }

    @Override
    @Transactional
    public void calculateFacilityToCashOut() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        Pageable pageable = PageRequest.of(0, batchDataSize);
        TbSchedulerStatus tbSchedulerStatus = tbSchedulerStatusRepository.findBySchedulerName(ConstantsUtils.FACILITY_TO_CASH_OUT_SCHEDULER);
        if (tbSchedulerStatus != null && !tbSchedulerStatus.getIsRunning()) {
            startScheduler(tbSchedulerStatus);
            try {
                processCashOutData(startOfDay, endOfDay, pageable);
                updateSchedulerStatus(tbSchedulerStatus, false, "Proses sync selesai.");
            } catch (Exception e) {
                logger.error("Error during sync process", e);
                updateSchedulerStatus(tbSchedulerStatus, false, "Proses sync gagal.");
            }
        } else {
            logger.info("Scheduler " + ConstantsUtils.FACILITY_TO_CASH_OUT_SCHEDULER + " lain sedang berjalan, yang dimulai pada : " + tbSchedulerStatus.getLastRunTime() + ". Mohon untuk tunggu hingga selesai.");
        }
    }

    @Override
    public void saveFacilityTransaction(FacilityTransactionRequestDto requestDto, String username) {
        TbFacilityAssetTransaction transaction = new TbFacilityAssetTransaction();
        transaction.setCompanyName(requestDto.getCompanyName());
        transaction.setProjectName(requestDto.getProjectName());
        transaction.setTransactionDate(requestDto.getTransactionDate());
        transaction.setAmount(requestDto.getAmount() != null ? requestDto.getAmount() : null);
        transaction.setCoverStartDate(requestDto.getCoverStartDate());
        transaction.setCoverEndDate(requestDto.getCoverEndDate());
        transaction.setTenorDate(DateTimeHelper.add14Days(requestDto.getCoverEndDate()));
        transaction.setTransactionType("Payment");
        transaction.setFacilityType(requestDto.getFacilityType());
        transaction.setDebitAdvice(requestDto.getDebitAdvice());
        transaction.setCreatedBy(username);
        transaction.setModifiedBy(username);
        transaction.setDownPayment(requestDto.getDownPayment());
        transaction.setQuote(requestDto.getQuote());
        transaction.setImplementation(requestDto.getImplementation());
        transaction.setMaintenance(requestDto.getMaintenance());

        // Mengambil tenorDateConfig dari tbFacilityBalance
        TbFacilityBalance tbFacilityBalance = tbFacilityBalanceRepository.findByFacilityType(requestDto.getFacilityType());

        if (tbFacilityBalance != null) {
            if (tbFacilityBalance.getTenorDateConfig() != null) {
                transaction.setTenorDateConfig(tbFacilityBalance.getTenorDateConfig());
            }

            tbFacilityBalance.setAmount(tbFacilityBalance.getAmount().subtract(requestDto.getAmount()));
            tbFacilityBalance.setModifiedBy("System");
            tbFacilityAssetTransactionRepository.save(transaction);
        } else {
            logger.info("Data Facility Type tidak terdaftar di FISION, Facility Type : "+requestDto.getFacilityType());
        }
    }

    @Override
    public void editTenorDate(TbFacilityAssetTransaction tbFacilityAssetTransaction) {
        tbFacilityAssetTransactionRepository.save(tbFacilityAssetTransaction);
    }

    @Override
    public TbFacilityAssetTransaction getFacilityTransactionById(Long id) {
        Optional<TbFacilityAssetTransaction> tbFacilityAssetTransaction = tbFacilityAssetTransactionRepository.findById(id);
        return tbFacilityAssetTransaction.orElse(null);
    }

    private void processPendingData(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable) {
        Page<FisionOutSourceData> pendingDataPage = fisionOutSourceDataRepository.findByTransactionDateAndStatus(DateTimeHelper.convertLocalDateToDate(startOfDay), DateTimeHelper.convertLocalDateToDate(endOfDay), 0, pageable);
        if (pendingDataPage.hasContent()) {
            List<FisionOutSourceData> pendingData = pendingDataPage.getContent();
            List<TbFacilityAssetTransaction> transactionList = mapToTransactions(pendingData);
            tbFacilityAssetTransactionRepository.saveAll(transactionList);

            updateFacilityBalances(pendingData);
            pendingData.forEach(data -> data.setStatus(1));
            fisionOutSourceDataRepository.saveAll(pendingData);

        }
        logger.info("Proses sync " + ConstantsUtils.FACILITY_TRANSACTION_SCHEDULER + " selesai, total data : " + pendingDataPage.getTotalElements());
    }

    private List<TbFacilityAssetTransaction> mapToTransactions(List<FisionOutSourceData> pendingData) {
        return pendingData.stream().map(data -> {
            TbFacilityAssetTransaction transaction = new TbFacilityAssetTransaction();
            transaction.setCompanyName(data.getVendorName());
            transaction.setProjectName(data.getProjectName());
            transaction.setTransactionDate(data.getTransactionDate());
            transaction.setAmount(data.getAmount() != null ? data.getAmount() : null);
            transaction.setBankApprovalDate(data.getBankApprovalDate());
            transaction.setTenorDate(data.getTenorDate());
            transaction.setTransactionType("Payment");
            transaction.setFacilityType(data.getFacilityType());
            transaction.setDebitAdvice(data.getDebitAdvice());
            transaction.setCreatedBy("System");
            transaction.setModifiedBy("System");

            // Mengambil tenorDateConfig dari tbFacilityBalance
            TbFacilityBalance tbFacilityBalance = tbFacilityBalanceRepository.findByFacilityType(data.getFacilityType());
            if (tbFacilityBalance != null && tbFacilityBalance.getTenorDateConfig() != null) {
                transaction.setTenorDateConfig(tbFacilityBalance.getTenorDateConfig());
            }

            return transaction;
        }).collect(Collectors.toList());
    }

    private void updateFacilityBalances(List<FisionOutSourceData> pendingData) {
        Map<String, BigDecimal> groupedData = pendingData.stream()
                .collect(Collectors.groupingBy(
                        FisionOutSourceData::getFacilityType,
                        Collectors.mapping(data -> data.getAmount() != null ? data.getAmount() : BigDecimal.ZERO,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        groupedData.forEach((facilityType, totalAmount) -> {
            TbFacilityBalance tbFacilityBalance = tbFacilityBalanceRepository.findByFacilityType(facilityType);
            if (tbFacilityBalance != null) {
                tbFacilityBalance.setAmount(tbFacilityBalance.getAmount().subtract(totalAmount));
                tbFacilityBalance.setModifiedBy("System");
            } else {
                logger.info("Data Facility Type tidak terdaftar di FISION, Facility Type : "+facilityType);
            }
        });
    }

    private void processCashOutData(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable) {
        Page<TbFacilityAssetTransaction> pendingDataPage = tbFacilityAssetTransactionRepository.findByCalculateDateAndStatus(DateTimeHelper.convertLocalDateToDate(startOfDay), Boolean.FALSE, pageable);
        if (pendingDataPage.hasContent()) {
            List<TbFacilityAssetTransaction> pendingData = pendingDataPage.getContent();
            List<TbCashOut> transactionList = mapToCashout(pendingData);
            tbCashOutRepository.saveAll(transactionList);

            pendingData.forEach(data -> data.setIsAddedToCashOut(Boolean.TRUE));
            tbFacilityAssetTransactionRepository.saveAll(pendingData);
            updateFacilityBalancesForCashOut(pendingData);

        }
        logger.info("Proses sync " + ConstantsUtils.FACILITY_TO_CASH_OUT_SCHEDULER + " selesai, total data : " + pendingDataPage.getTotalElements());
    }

    private List<TbCashOut> mapToCashout(List<TbFacilityAssetTransaction> calculatedData) {
        return calculatedData.stream()
                .filter(data -> !"BG".equals(data.getFacilityType())) // Memfilter data yang bukan BG
                .map(data -> {
                    TbCashOut tbCashOut = new TbCashOut();
                    tbCashOut.setAmount(data.getAmount());
                    tbCashOut.setVendorName(data.getCompanyName());
                    tbCashOut.setProjectName(data.getProjectName());
                    tbCashOut.setCreatedBy("System");
                    tbCashOut.setModifiedBy("System");
                    tbCashOut.setDocumentCashOutName("-");
                    tbCashOut.setInvoiceTitle(data.getDebitAdvice());
                    tbCashOut.setTotal(data.getAmount());
                    return tbCashOut;
                })
                .collect(Collectors.toList());
    }

    private void updateFacilityBalancesForCashOut(List<TbFacilityAssetTransaction> facilityAssetTransactionList) {
        Map<String, BigDecimal> groupedData = facilityAssetTransactionList.stream()
                .collect(Collectors.groupingBy(
                        TbFacilityAssetTransaction::getFacilityType,
                        Collectors.mapping(data -> data.getAmount() != null ? data.getAmount() : BigDecimal.ZERO,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        groupedData.forEach((facilityType, totalAmount) -> {
            TbFacilityBalance tbFacilityBalance = tbFacilityBalanceRepository.findByFacilityType(facilityType);
            if (tbFacilityBalance != null) {
                tbFacilityBalance.setAmount(tbFacilityBalance.getAmount().add(totalAmount));
                tbFacilityBalance.setModifiedBy("System");
            } else {
                logger.info("Data Facility Type tidak terdaftar di FISION, Facility Type : "+facilityType);
            }
        });
    }

    private void startScheduler(TbSchedulerStatus tbSchedulerStatus) {
        tbSchedulerStatus.setIsRunning(true);
        tbSchedulerStatus.setLastRunTime(new Date());
        tbSchedulerStatusRepository.save(tbSchedulerStatus);
    }

    private void updateSchedulerStatus(TbSchedulerStatus tbSchedulerStatus, boolean isRunning, String message) {
        tbSchedulerStatus.setIsRunning(isRunning);
        tbSchedulerStatus.setStatusMessage(message);
        tbSchedulerStatusRepository.save(tbSchedulerStatus);
    }
}
