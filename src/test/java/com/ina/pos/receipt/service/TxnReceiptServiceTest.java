package com.ina.pos.receipt.service;

import com.ina.pos.receipt.aws.service.AmazonS3Service;
import com.ina.pos.receipt.dao.ReceiptUrlsRepository;
import com.ina.pos.receipt.dao.entity.ReceiptUrls;
import com.ina.pos.receipt.dto.*;
import com.ina.pos.receipt.exception.CommonValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import static com.ina.pos.receipt.util.CommonTestUtil.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TxnReceiptServiceTest {

    @Mock
    PdfGeneratorService pdfGeneratorService;

    @Mock
    ReconciliationPdfGeneratorService reconciliationPdfGeneratorService;

    @Mock
    WhatsAppService whatsAppService;

    @Mock
    AmazonS3Service amazonS3Service;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    ReceiptUrlsRepository urlsRepository;

    @InjectMocks
    TxnReceiptService txnReceiptService;

    @Test
    void testProcessAndSendReceipt(){
        PosReceiptRequest posReceiptRequest = getPosReceiptRequest();
        when(amazonS3Service.uploadFileAndGetUrl(any(), anyString())).thenReturn("url");
        when(pdfGeneratorService.generateReceiptPdf(any())).thenReturn("test-data".getBytes());
        when(urlsRepository.findByReferenceId(any())).thenReturn(null);
        ApiResponse apiResponse = txnReceiptService.processAndSendReceipt(posReceiptRequest);
        assertNotNull(apiResponse);
    }

    @Test
    void testProcessAndSendReconciliationReceipt(){
        ReconciliationReceiptRequest request = getReconciliationReceiptRequest();
        when(amazonS3Service.uploadFileAndGetUrl(any(), anyString())).thenReturn("url");
        when(reconciliationPdfGeneratorService.generateReconciliationPdf(any())).thenReturn("test-Data".getBytes());
        when(urlsRepository.findByReferenceId(any())).thenReturn(null);
        when(urlsRepository.findByReferenceId(any())).thenReturn(null);
        ApiResponse apiResponse = txnReceiptService.processAndSendReconciliationReceipt(request);
        assertNotNull(apiResponse);
    }

    @Test
    void testSendReceiptToWhatsApp(){
        WhatsappReceiptRequest whatsappReceiptRequest = new WhatsappReceiptRequest();
        whatsappReceiptRequest.setMobileNumber("987345678");
        whatsappReceiptRequest.setReferenceNumber("test-ref-number");
        when(urlsRepository.findByReferenceId(any())).thenReturn(getReceiptUrls());
        when(whatsAppService.uploadMedia(any(), anyString())).thenReturn("test-id");
        ApiResponse apiResponse = txnReceiptService.sendReceiptToWhatsApp(whatsappReceiptRequest);
        assertNotNull(apiResponse);
    }

    @Test
    void testSendReceiptToWhatsAppThrowsException(){
        WhatsappReceiptRequest whatsappReceiptRequest = new WhatsappReceiptRequest();
        whatsappReceiptRequest.setMobileNumber("987345678");
        whatsappReceiptRequest.setReferenceNumber("test-ref-number");
        ReceiptUrls receiptUrls = getReceiptUrls();
        receiptUrls.setUrl("test-url");
        when(urlsRepository.findByReferenceId(any())).thenReturn(receiptUrls);
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> txnReceiptService.sendReceiptToWhatsApp(whatsappReceiptRequest));
        assertNotNull(exception);
    }

    @Test
    void testGetReceiptUrl(){
        QrReceiptFetchRequest qrReceiptFetchRequest = new QrReceiptFetchRequest();
        qrReceiptFetchRequest.setReferenceNumber("test-ref-id");
        when(urlsRepository.findByReferenceId(any())).thenReturn(getReceiptUrls());
        ApiResponse apiResponse = txnReceiptService.getReceiptUrl(qrReceiptFetchRequest);
        assertNotNull(apiResponse);
    }

}
