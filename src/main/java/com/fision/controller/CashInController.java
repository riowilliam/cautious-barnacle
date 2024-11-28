package com.fision.controller;

import com.fision.dto.*;
import com.fision.entity.primary.MsBalance;
import com.fision.entity.primary.TbArInvoice;
import com.fision.entity.primary.TbCashIn;
import com.fision.entity.primary.TbPartner;
import com.fision.service.ARInvoiceService;
import com.fision.service.CashInService;
import com.fision.service.MsBalanceService;
import com.fision.service.PartnerService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import com.fision.utils.NumberToWords;
import com.google.gson.Gson;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.*;

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/cashIn/")
@CrossOrigin
public class CashInController {
    private static final Logger logger = LoggerFactory.getLogger(CashInController.class);

    @Value("${file.path}")
    String filePath;

    @Autowired
    CashInService cashInService;

    @Autowired
    ARInvoiceService arInvoiceService;

    @Autowired
    MsBalanceService msBalanceService;

    @Autowired
    PartnerService partnerService;

    @GetMapping("getCashInPaging")
    public ResponseDto<?> getCashInPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdTm") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String partnerName,
            @RequestParam(required = false) String projectName,
            @RequestParam(required = false) String paymentType,
            @RequestParam(required = false) String paymentBank,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            Page<CashInListDto> cashInDetailDtos = cashInService.getCashInListPaging(pageNo, pageSize, sortBy.equalsIgnoreCase("createdDate") ? "createdTm" : sortBy, sortOrder,
                    partnerName != null && !partnerName.isEmpty() ? partnerName : null,
                    projectName != null && !projectName.isEmpty() ? projectName : null,
                    paymentType != null && !paymentType.isEmpty() ? paymentType.equalsIgnoreCase(ConstantsUtils.FULLY_PAYMENT) ? 1 : 2 : null,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDateAddOneDay(endDate) : null);

            return new ResponseDto<>(ConstantsUtils.SUCCESS, cashInDetailDtos, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("createCashIn")
    public ResponseDto<?> createCashIn(@RequestParam String username, @RequestBody String requestDto) {
        try {
            if(requestDto == null || requestDto.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            CashInRequestDto cashInRequestDto = gson.fromJson(requestDto, CashInRequestDto.class);
            if(cashInRequestDto != null) {
                TbArInvoice arInvoice = arInvoiceService.getInvoiceByInvoiceNo(cashInRequestDto.getInvoiceNo());
                BigDecimal incompletedPayment = cashInService.getTotalIncompletedCashIByInvoiceNo(cashInRequestDto.getInvoiceNo());
                if(arInvoice != null) {
                    if (arInvoice.getTotalAmount().compareTo(cashInRequestDto.getPaymentAmount()) < 0) {
                        return new ResponseDto<>(ConstantsUtils.PAYMENT_TOTAL_LESS_THAN_AMOUNT, HttpStatus.BAD_REQUEST);
                    } else if ((cashInRequestDto.getPaymentAmount().add(incompletedPayment)).compareTo(arInvoice.getTotalAmount()) > 0) {
                        return new ResponseDto<>(ConstantsUtils.THERE_ARE_INCOMPLETE_PAYMENT, HttpStatus.BAD_REQUEST);
                    } else {
                        cashInService.saveCashIn(cashInRequestDto, arInvoice, username);
                    }
                }
                return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
            } else {
                return new ResponseDto<>(HttpStatus.BAD_REQUEST.getReasonPhrase(), HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            e.printStackTrace();
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("completeCashIn")
    public ResponseDto<?> completeCashIn(@RequestParam String username, @RequestParam Long cashInId) {
        try {
            if(cashInId == null) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }
            TbCashIn tbCashIn = cashInService.getTbCashInById(cashInId);
            if(tbCashIn != null) {
                TbArInvoice tbArInvoice = arInvoiceService.getInvoiceByInvoiceNo(tbCashIn.getInvoiceNo());
                BigDecimal completedCashIn = cashInService.getTotalCompletedCashIByInvoiceNo(tbArInvoice.getInvoiceNo());
                tbCashIn.setCashInStatus(ConstantsUtils.COMPLETED);
                tbCashIn.setModifiedBy(username);
                cashInService.save(tbCashIn);

                if(tbCashIn.getPaymentAmount().add(tbArInvoice.getDeduction()).add(completedCashIn).compareTo(tbArInvoice.getTotalAmount()) == 0
                        && tbCashIn.getPaymentType() == 1) {
                    tbArInvoice.setPaymentStatus(ConstantsUtils.FULLY_PAID);
                } else {
                    tbArInvoice.setPaymentStatus(ConstantsUtils.PARTIALLY_PAYMENT);
                }
                tbArInvoice.setModifiedBy(username);
                arInvoiceService.save(tbArInvoice);

                return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
            } else {
                return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getArInvoiceList")
    public ResponseDto<?> getArInvoiceList(@RequestParam String username, @RequestParam String invoiceNo) {
        try {
            List<ARInvoiceDetailDto> arInvoiceDetailList = arInvoiceService.getArInvoiceList(invoiceNo != null && !invoiceNo.isEmpty() ? invoiceNo : null);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, arInvoiceDetailList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getCashInListByInvoiceNo")
    public ResponseDto<?> getCashInListByInvoiceNo(@RequestParam String username, @RequestParam String invoiceNo) {
        try {
            List<CashInDetailDto> cashInDetailDtoList = cashInService.getCashInListByInvoiceNo(invoiceNo);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, cashInDetailDtoList, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getDocumentCashInWithInvoiceDetails")
    @Transactional
    public ResponseEntity<?> getDocumentCashIn(@RequestParam String username, @RequestParam Long cashInId) {
        try {
            if (cashInId == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Locale indonesiaLocale = new Locale("id", "ID");
            TbCashIn tbCashIn = cashInService.getTbCashInById(cashInId);
            if(tbCashIn == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            CashInAmountsDto cashInAmountsDto = cashInService.getCashInAmounts(tbCashIn.getInvoiceNo(), tbCashIn.getPaymentProgressNum());
            BigDecimal acceptantionValue = cashInAmountsDto.getTotalAmount().compareTo(BigDecimal.ZERO) == 0 ? cashInAmountsDto.getTotalAmount() :
                    cashInAmountsDto.getTotalAmount().add(cashInAmountsDto.getTotalInterestAmount().add(cashInAmountsDto.getTotalOtherDeduction()));

            TbArInvoice tbArInvoice = arInvoiceService.getInvoiceByInvoiceNo(tbCashIn.getInvoiceNo());
            if(tbArInvoice == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            TbPartner tbPartner = partnerService.getPartnerByName(tbArInvoice.getPartnerName());
            if(tbPartner == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            MsBalance msBalance = msBalanceService.getMsBalanceByBankCode(tbCashIn.getPaymentBankCode());
            if(msBalance == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            String documentName = "Cash-In-Docs-" + tbCashIn.getInvoiceNo() + "-" +tbCashIn.getPaymentProgressNum();
            String notes = ConstantsUtils.INVOICE_NOTES.replace("n", tbCashIn.getPaymentProgressNum().toString()).replace(ConstantsUtils.PLACEHOLDER_INVOICE, tbCashIn.getInvoiceNo());

            // Prepare parameters for Jasper Report
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("bankAccount", msBalance.getBankAccount());
            parameters.put("bankName", StringUtils.join(msBalance.getBankShortName(),
                    msBalance.getBankDesc() != null ? " - "+ msBalance.getBankDesc() : ""));
            parameters.put("customerName", tbArInvoice.getPartnerName());
            parameters.put("totalAmount", tbCashIn.getPaymentAmount());
            parameters.put("numberToWords", NumberToWords.convertToWords(tbCashIn.getPaymentAmount()));
            parameters.put("notes", notes);
            parameters.put("invoiceNo", tbArInvoice.getInvoiceNo());
            parameters.put("progress", tbArInvoice.getProgress());
            parameters.put("retention", tbArInvoice.getRetention());
            parameters.put("downPayment", tbArInvoice.getDownPayment());
            parameters.put("potonganPpn", tbPartner.getIsPpnWapu() == 1 ? tbArInvoice.getPpnAmount() : BigDecimal.ZERO);
            parameters.put("pphAmount", tbArInvoice.getPphAmount());
            parameters.put("interestDeduction", tbCashIn.getInterestDeduction());
            parameters.put("otherDeduction", tbCashIn.getOtherDeduction());
            parameters.put("ppnAmount", tbArInvoice.getPpnAmount());
            parameters.put("paidAmount", acceptantionValue);
            parameters.put("docDate", DateTimeHelper.getJakartaDate(tbCashIn.getCreatedTm()));
            parameters.put("REPORT_LOCALE", indonesiaLocale);

            // Copy image temp
            InputStream inputStream = ResourceUtils.class.getResourceAsStream("/" + "HKA_Logos.png");
            if (inputStream == null) {
                throw new IllegalArgumentException("Resource not found: " + "HKA_Logos.png");
            }

            File tempFile = Files.createTempFile("temp-", "-" + "HKA_Logos.png").toFile();
            tempFile.deleteOnExit();
            parameters.put("imgDir", tempFile.getAbsolutePath());

            // Menyalin isi dari InputStream ke file sementara
            try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            // Load Jasper report
            InputStream jasperStream = this.getClass().getResourceAsStream("/cash_in_doc_with_invoice.jasper");
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            // Fill Report to Jasper and Export to PDF
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
            File fileDoc = new File(filePath.concat("/").concat(documentName + ".pdf"));

            // Export JasperPrint to the FileOutputStream
            FileOutputStream fileOutputStream = new FileOutputStream(fileDoc);
            JasperExportManager.exportReportToPdfStream(jasperPrint, fileOutputStream);
            fileOutputStream.close();

            // Convert File to FileSystemResource
            Resource fileResource = new FileSystemResource(fileDoc);

            // Set the response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + documentName+ ".pdf");
            tbCashIn.setIsFileDownloaded(Boolean.TRUE);
            cashInService.save(tbCashIn);

            deleteFilesAfterResponse(fileDoc);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/pdf"))
                    .body(fileResource);
        } catch (Exception e) {
            logger.error("Error creating cash out document: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void deleteFilesAfterResponse(File file) {
        // Run deletion in a new thread
        new Thread(() -> {
            try {
                Thread.sleep(3000); // Optional delay to ensure response is fully sent
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        logger.warn("Failed to delete file: " + filePath);
                    }
                }
            } catch (InterruptedException e) {
                logger.error("File deletion interrupted: ", e);
            }
        }).start();
    }

}
