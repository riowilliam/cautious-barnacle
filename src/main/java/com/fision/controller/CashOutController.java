package com.fision.controller;

import com.fision.dto.*;
import com.fision.entity.TbDocumentCashOut;
import com.fision.service.CashOutService;
import com.fision.utils.ConstantsUtils;
import com.fision.utils.DateTimeHelper;
import com.google.gson.Gson;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author LordDev
 */
@RestController
@RequestMapping("/api/cashOut/")
@CrossOrigin
public class CashOutController {
    private static final Logger logger = LoggerFactory.getLogger(CashOutController.class);

    @Value("${file.path}")
    String filePath;

    @Autowired
    CashOutService cashOutService;

    @PostMapping("createCashOutDoc")
    public ResponseEntity<?> createCashOutDoc(@RequestParam String username, @RequestBody String requestDto) {
        String csvOutputFile = null;
        String zipOutputFile = null;
        String outputFile = null;

        try {
            if (requestDto == null || requestDto.isEmpty()) {
                return new ResponseEntity<>(new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null), HttpStatus.BAD_REQUEST);
            }

            Gson gson = new Gson();
            Locale indonesiaLocale = new Locale("id", "ID");
            CashOutListDto cashOutListDto = gson.fromJson(requestDto, CashOutListDto.class);
            String documentCashOutName = cashOutService.saveTmpCashOut(username, cashOutListDto);

            // Prepare parameters for Jasper Report
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("docDate", DateTimeHelper.getJakartaDate(new Date()));
            parameters.put("REPORT_LOCALE", indonesiaLocale);
            parameters.put("REPORT_CLASS_PATH", getClass().getResource("/").getPath());

            // Convert cashOutDetailList to JRBeanCollectionDataSource
            List<CashOutDetailDto> cashOutDetails = cashOutListDto.getCashOutDetailList();
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(cashOutDetails);

            // Load Jasper file
            InputStream jasperStream = this.getClass().getResourceAsStream("/document_cash_out.jasper");
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            // Fill report with data and parameters
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // Save report as PDF
            outputFile = filePath.concat("/").concat(documentCashOutName + ".pdf");
            JasperExportManager.exportReportToPdfFile(jasperPrint, outputFile);

            // Create CSV file
            String csvFileName = documentCashOutName + ".csv";
            csvOutputFile = filePath.concat("/").concat(csvFileName);
            createCsvFile(cashOutDetails, csvOutputFile);

            // Zip both PDF and CSV files
            String zipFileName = documentCashOutName + ".zip";
            zipOutputFile = filePath.concat("/").concat(zipFileName);
            zipFiles(zipOutputFile, outputFile, csvOutputFile);

            // Prepare the ZIP file for download
            FileSystemResource zipFileResource = new FileSystemResource(zipOutputFile);

            // Set response headers
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + zipFileName);

            // Return the ZIP file and schedule deletion
            ResponseEntity<FileSystemResource> responseEntity = new ResponseEntity<>(zipFileResource, headers, HttpStatus.OK);

            // Delete files in a separate thread after returning the response
            deleteFilesAfterResponse(outputFile, csvOutputFile, zipOutputFile);

            return responseEntity;
        } catch (Exception e) {
            logger.error("Error creating cash out document: ", e);
            return new ResponseEntity<>(new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("getCashOutDocByName")
    public ResponseDto<?> getCashOutDocByName(@RequestParam String username, @RequestParam String docName) {
        try {
            if(docName == null || docName.isEmpty()) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }
            CashOutListDto cashOutListDto = cashOutService.getCashOutListByDocName(docName);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, cashOutListDto, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("approvalCashOutDoc")
    public ResponseDto<?> approvalCashOutDoc(@RequestParam String username, @RequestParam Integer status, @RequestParam String docName) {
        try {
            if(status == null || (docName == null || docName.isEmpty())) {
                return new ResponseDto<>(ConstantsUtils.INVALID_REQUEST, null, HttpStatus.BAD_REQUEST);
            }

            TbDocumentCashOut tbDocumentCashOut = cashOutService.getDocumentCashOut(docName);
            if(tbDocumentCashOut == null) {
                return new ResponseDto<>(ConstantsUtils.DATA_NOT_FOUND, HttpStatus.NOT_FOUND);
            }
            cashOutService.approvalCashOutDoc(username, status, tbDocumentCashOut);
            return new ResponseDto<>(ConstantsUtils.SUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage());
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getCashOutMutationPaging")
    public ResponseDto<?> getCashOutMutationPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdTm") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String documentCashOutName,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            Page<CashOutMutationListDto> cashOutMutationListDtoPage = cashOutService.getCashOutMutationPaging(pageNo, pageSize, sortBy.equalsIgnoreCase("createdDate") ? "createdTm" : sortBy, sortOrder,
                    vendorName != null && !vendorName.isEmpty() ? vendorName : null,
                    documentCashOutName != null && !documentCashOutName.isEmpty() ? documentCashOutName : null,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDate(endDate) : null);

            return new ResponseDto<>(ConstantsUtils.SUCCESS, cashOutMutationListDtoPage, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getCashOutDocPaging")
    public ResponseDto<?> getCashOutDocPaging(
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "createdTm") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(required = false) String documentName,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        try {
            Page<CashOutDocListDto> cashOutMutationListDtoPage = cashOutService.getCashOutDocPaging(pageNo, pageSize, sortBy.equalsIgnoreCase("createdDate") ? "createdTm" : sortBy, sortOrder,
                    documentName != null && !documentName.isEmpty() ? documentName : null,
                    status,
                    startDate != null && !startDate.isEmpty() ? DateTimeHelper.stringToDate(startDate) : null,
                    endDate != null && !endDate.isEmpty() ? DateTimeHelper.stringToDate(endDate) : null);

            return new ResponseDto<>(ConstantsUtils.SUCCESS, cashOutMutationListDtoPage, HttpStatus.OK);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            return new ResponseDto<>(ConstantsUtils.ERROR_SYSTEM, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void createCsvFile(List<CashOutDetailDto> cashOutDetails, String csvOutputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvOutputFile))) {
            // Write CSV header
            writer.write("rekanan,invoice,unit,no_rekening,atas_nama,nama_bank,di_transfer,biaya_transfer,total_pembayaran");
            writer.newLine();

            // Write data
            for (CashOutDetailDto detail : cashOutDetails) {
                writer.write(detail.getVendorName() + "," + detail.getInvoice() + "," + detail.getProjectName() + "," + detail.getBankAccount() +" " +
                        "," + detail.getBankAccountName() + "," + detail.getBankName() + "," + detail.getTransferAmount() + "," + detail.getTransferFee() + "," + detail.getPaymentAmount());
                writer.newLine();
            }
        }
    }

    private void zipFiles(String zipOutputFile, String... filesToZip) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(zipOutputFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            for (String file : filesToZip) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry zipEntry = new ZipEntry(new File(file).getName());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                }
            }
        }
    }

    private void deleteFiles(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) {
                logger.warn("Failed to delete file: " + filePath);
            }
        }
    }

    private void deleteFilesAfterResponse(String outputFile, String csvOutputFile, String zipOutputFile) {
        // Run deletion in a new thread
        new Thread(() -> {
            try {
                Thread.sleep(3000); // Optional delay to ensure response is fully sent
                deleteFiles(outputFile);
                deleteFiles(csvOutputFile);
                deleteFiles(zipOutputFile);
            } catch (InterruptedException e) {
                logger.error("File deletion interrupted: ", e);
            }
        }).start();
    }
}
