package com.ina.pos.receipt.service;

import com.ina.pos.receipt.aws.service.AmazonS3Service;
import com.ina.pos.receipt.dao.ReceiptUrlsRepository;
import com.ina.pos.receipt.dao.entity.ReceiptUrls;
import com.ina.pos.receipt.dto.*;
import com.ina.pos.receipt.exception.CommonValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class TxnReceiptService {

    private final PdfGeneratorService pdfGeneratorService;
    private final ReconciliationPdfGeneratorService reconciliationPdfGeneratorService;
    private final WhatsAppService whatsAppService;
    private final AmazonS3Service amazonS3Service;
    private final ReceiptUrlsRepository urlsRepository;

    /**
     * Full pipeline:
     *  1. Generate PDF from receipt data
     *  2. Upload PDF to WhatsApp Media API
     *  3. Send document message to the customer's WhatsApp
     */
    public ApiResponse processAndSendReceipt(PosReceiptRequest request) {
        log.info("Processing receipt: {} for mobile: {}",
                request.getReceiptNumber(), request.getMobileNumber());

        byte[] pdfBytes = pdfGeneratorService.generateReceiptPdf(request);
        String filename = buildFilename(request.getReceiptNumber());

        log.info("PDF generated. Filename: {}, Size: {} bytes", filename, pdfBytes.length);


        String pdfUrl = amazonS3Service.uploadFileAndGetUrl(pdfBytes, filename);
        log.info("PDF url generated : {} ", pdfUrl);
        if (!pdfUrl.isEmpty()){
            ReceiptUrls byReferenceId = urlsRepository.findByReferenceId(request.getReceiptNumber());
            if (nonNull(byReferenceId)) {
                throw new CommonValidationException("Already a record existed with reference number provided",500);
            }

            ReceiptUrls receiptUrls = ReceiptUrls.builder()
                    .referenceId(request.getReceiptId())
                    .receiptNumber(request.getReceiptNumber())
                    .storeName(request.getStoreName())
                    .currency(request.getCurrency())
                    .total(String.valueOf(request.getTotalAmount()))
                    .url(pdfUrl)
                    .createdDate(Timestamp.valueOf(LocalDateTime.now()))
                    .updatedDate(Timestamp.valueOf(LocalDateTime.now()))
                    .build();
            urlsRepository.save(receiptUrls);
        }


        return ApiResponse.builder()
                .status("success.")
                .message("Receipt generated successfully")
                .receiptNumber(request.getReceiptNumber())
                .pdfUrl(pdfUrl)
                .build();

    }


    private String buildFilename(String receiptNumber) {
        String sanitized = receiptNumber != null
            ? receiptNumber.replaceAll("[^a-zA-Z0-9\\-_]", "_")
            : "receipt";
        return "Receipt_" + sanitized + ".pdf";
    }

    private String buildCaption(String storeName,
                                String currency,
                                String totalAmount,
                                String receiptNumber) {

        String safeStoreName = storeName != null ? storeName : "Our Store";
        String safeCurrency  = currency != null ? currency : "";
        String safeTotal     = totalAmount != null
                ?  totalAmount
                : "0.00";
        String safeReceipt   = receiptNumber != null ? receiptNumber : "-";

        return String.format("""
                        🧾 *Payment Receipt*
                        Store: %s
                        Receipt: %s
                        Amount: %s %s
                        
                        Thank you for your payment!
                        """,
                safeStoreName,
                safeReceipt,
                safeCurrency,
                safeTotal
        );
    }

    /**
     * Full pipeline:
     *  1. Generate PDF from receipt data
     *  2. Upload PDF to WhatsApp Media API
     *  3. Send document message to the customer's WhatsApp
     */
    public ApiResponse processAndSendReconciliationReceipt(ReconciliationReceiptRequest request) {
        log.info("Processing receipt : {} for mobile : {}",
                request.getReceiptNumber(), request.getMobileNumber());

        byte[] pdfBytes = reconciliationPdfGeneratorService.generateReconciliationPdf(request);
        String filename  = buildFilename(request.getReceiptId());

        log.info("PDF generated. Filename : {}, Size : {} bytes", filename, pdfBytes.length);

            String pdfUrl = amazonS3Service.uploadFileAndGetUrl(pdfBytes, filename);
            log.info("PDF url generated: {}", pdfUrl);
            if (!pdfUrl.isEmpty()){
                ReceiptUrls byReferenceId = urlsRepository.findByReferenceId(request.getReceiptNumber());
                if (nonNull(byReferenceId)) {
                    throw new CommonValidationException("Already a record existed with reference number provided",500);
                }

                ReceiptUrls receiptUrls = ReceiptUrls.builder()
                        .referenceId(request.getReceiptId())
                        .total(String.valueOf(request.getTotalAmount()))
                        .currency(request.getCurrency())
                        .storeName(request.getStoreName())
                        .url(pdfUrl)
                        .createdDate(Timestamp.valueOf(LocalDateTime.now()))
                        .updatedDate(Timestamp.valueOf(LocalDateTime.now()))
                        .build();
                urlsRepository.save(receiptUrls);
            }

            return ApiResponse.builder()
                    .status("success.")
                    .message("Receipt generated successfully")
                    .receiptNumber(request.getReceiptNumber())
                    .pdfUrl(pdfUrl)
                    .build();

    }



    public ApiResponse sendReceiptToWhatsApp(WhatsappReceiptRequest request) {
        ReceiptUrls receiptUrls = getReceiptUrls(request.getReferenceNumber());
        String fileUrl = receiptUrls.getUrl();
        byte[] pdfBytes = downloadFileFromS3(fileUrl);
        String filename = extractFileName(fileUrl);

        String mediaId = whatsAppService.uploadMedia(pdfBytes, filename);
        log.info("PDF uploaded to WhatsApp. Media ID: {}", mediaId);

        String caption = buildCaption(
                receiptUrls.getStoreName(),
                receiptUrls.getCurrency(),
                receiptUrls.getTotal(),
                request.getReferenceNumber());

        String whatsappMessageId = whatsAppService.sendDocumentMessage(
                request.getMobileNumber(),
                mediaId,
                filename
                ,caption
        );

        log.info("Receipt sent via WhatsApp. Message ID: {}", whatsappMessageId);

        return ApiResponse.builder()
                .status("success")
                .message("Receipt PDF sent successfully via WhatsApp")
                .receiptNumber(request.getReferenceNumber())
                .mobileNumber(request.getMobileNumber())
                .whatsappMessageId(whatsappMessageId)
                .build();
    }

    public ApiResponse getReceiptUrl(QrReceiptFetchRequest request){
        ReceiptUrls receiptUrls = getReceiptUrls(request.getReferenceNumber());
        return ApiResponse.builder()
                .status("success")
                .message("Receipt retrieved successfully")
                .receiptNumber(request.getReferenceNumber())
                .pdfUrl(receiptUrls.getUrl())
                .build();

    }

    private ReceiptUrls getReceiptUrls(String referenceNumber) {
        ReceiptUrls receiptUrls = urlsRepository.findByReferenceId(referenceNumber);
        if (isNull(receiptUrls)){
            throw new CommonValidationException("No record found with the provided reference number",500);
        }
        return receiptUrls;
    }

    private byte[] downloadFileFromS3(String fileUrl) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<byte[]> response = restTemplate.getForEntity(fileUrl, byte[].class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                throw new CommonValidationException("Failed to download file from S3", 500);
            }
        } catch (Exception e) {
            log.error("Error downloading file from S3: {}", fileUrl, e);
            throw new CommonValidationException("Error fetching file from S3", 500);
        }
    }

    private String extractFileName(String url) {
        try {
            String path = new URI(url).getPath();
            return path.substring(path.lastIndexOf("/") + 1);
        } catch (Exception e) {
            throw  new CommonValidationException("Unable to fetch file name from url",500);
        }
    }

}
