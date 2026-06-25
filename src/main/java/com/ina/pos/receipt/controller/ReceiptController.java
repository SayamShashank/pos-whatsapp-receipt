package com.ina.pos.receipt.controller;

import com.ina.pos.receipt.dto.*;
import com.ina.pos.receipt.exception.CommonValidationException;
import com.ina.pos.receipt.service.TxnReceiptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/receipt")
@RequiredArgsConstructor
public class ReceiptController {

    private final TxnReceiptService txnReceiptService;

    /**
     * POST /api/v1/receipt/send
     *
     * Accepts POS receipt data, generates a PDF, and sends it
     * to the provided mobile number via WhatsApp.
     */
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendReceipt(
            @Valid @RequestBody PosReceiptRequest request) {

        log.info("Received receipt send request. Receipt: {}, Mobile: {}",
                request.getReceiptNumber(), request.getMobileNumber());

        try {
            ApiResponse response = txnReceiptService.processAndSendReceipt(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to process receipt : {}", request.getReceiptNumber(), e);

            ApiResponse errorResponse = ApiResponse.builder()
                .status("error.")
                .message("Failed to send receipt : " + e.getMessage())
                .receiptNumber(request.getReceiptNumber())
                .mobileNumber(request.getMobileNumber())
                .build();

            throw new CommonValidationException(errorResponse.toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    /**
     * POST /api/v1/receipt/reconciliation/send
     *
     * Accepts POS receipt data, generates a PDF, and sends it
     * to the provided mobile number via WhatsApp.
     */
    @PostMapping("reconciliation/send")
    public ResponseEntity<ApiResponse> sendReconReceipt(
            @Valid @RequestBody ReconciliationReceiptRequest request) {

        log.info("Received Reconciliation receipt send request. Receipt: {}, Mobile: {}",
                request.getReceiptNumber(), request.getMobileNumber());

        try {
            ApiResponse response = txnReceiptService.processAndSendReconciliationReceipt(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to process receipt: {} ", request.getReceiptNumber(), e);

            ApiResponse errorResponse = ApiResponse.builder()
                    .status("error ")
                    .message("Failed to send receipt : " + e.getMessage())
                    .receiptNumber(request.getReceiptNumber())
                    .mobileNumber(request.getMobileNumber())
                    .build();

            throw new CommonValidationException(errorResponse.toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    /**
     * POST /api/v1/receipt/whatsapp/send
     *
     * Accepts POS receipt data, generates a PDF, and sends it
     * to the provided mobile number via WhatsApp.
     */
    @PostMapping("whatsapp/send")
    public ResponseEntity<ApiResponse> sendCopyReceipt(
            @Valid @RequestBody WhatsappReceiptRequest request) {

        log.info("Received receipt send request. ReferenceNumber: {}, Mobile: {}",
                request.getReferenceNumber(), request.getMobileNumber());

        try {
            ApiResponse response = txnReceiptService.sendReceiptToWhatsApp(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to process receipt: {}", request.getReferenceNumber(), e);

            ApiResponse errorResponse = ApiResponse.builder()
                    .status("error")
                    .message("Failed to send receipt: " + e.getMessage())
                    .receiptNumber(request.getReferenceNumber())
                    .mobileNumber(request.getMobileNumber())
                    .build();

            throw new CommonValidationException(errorResponse.toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    /**
     * POST /api/v1/receipt/qr/send
     *
     * Accepts POS receipt data, generates a PDF, and sends it
     * to the provided mobile number via WhatsApp.
     */
    @PostMapping("qr/send")
    public ResponseEntity<ApiResponse> sendReconCopyReceipt(
            @Valid @RequestBody QrReceiptFetchRequest request) {

        log.info("Received Reconciliation receipt fetch request. ReferenceNumber: {}",
                request.getReferenceNumber());

        try {
            ApiResponse response = txnReceiptService.getReceiptUrl(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to process receipt: {}", request.getReferenceNumber(), e);

            ApiResponse errorResponse = ApiResponse.builder()
                    .status("error")
                    .message("Failed to send receipt: " + e.getMessage())
                    .receiptNumber(request.getReferenceNumber())
                    .build();

            throw new CommonValidationException(errorResponse.toString(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    /**
     * GET /api/v1/receipt/health
     * Simple health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> health() {
        return ResponseEntity.ok(
            ApiResponse.builder()
                .status("success")
                .message("POS Receipt WhatsApp Service is running")
                .build()
        );
    }
}
