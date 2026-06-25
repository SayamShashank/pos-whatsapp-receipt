package com.ina.pos.receipt.controller;

import com.ina.pos.receipt.dto.*;
import com.ina.pos.receipt.exception.CommonValidationException;
import com.ina.pos.receipt.service.TxnReceiptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static com.ina.pos.receipt.util.CommonTestUtil.getPosReceiptRequest;
import static com.ina.pos.receipt.util.CommonTestUtil.getReconciliationReceiptRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReceiptControllerTest {

    @Mock
    TxnReceiptService txnReceiptService;

    @InjectMocks
    ReceiptController receiptController;

    @Test
    void testSendReceipt(){
        PosReceiptRequest posReceiptRequest = getPosReceiptRequest();
        when(txnReceiptService.processAndSendReceipt(any())).thenReturn(new ApiResponse());
        ResponseEntity<ApiResponse> responseEntity = receiptController.sendReceipt(posReceiptRequest);
        assertNotNull(responseEntity);
    }

    @Test
    void testSendReceiptThrowsException(){
        PosReceiptRequest posReceiptRequest = getPosReceiptRequest();
        when(txnReceiptService.processAndSendReceipt(any())).thenThrow(new CommonValidationException("error-message",400));
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> receiptController.sendReceipt(posReceiptRequest));
        assertNotNull(exception);
    }

    @Test
    void testSendReconReceipt(){
        ReconciliationReceiptRequest reconciliationReceiptRequest = getReconciliationReceiptRequest();
        when(txnReceiptService.processAndSendReconciliationReceipt(any())).thenReturn(new ApiResponse());
        ResponseEntity<ApiResponse> responseEntity = receiptController.sendReconReceipt(reconciliationReceiptRequest);
        assertNotNull(responseEntity);
    }

    @Test
    void testSendReconReceiptThrowsException(){
        ReconciliationReceiptRequest reconciliationReceiptRequest = getReconciliationReceiptRequest();
        when(txnReceiptService.processAndSendReconciliationReceipt(any())).thenThrow(new CommonValidationException("error-message",400));
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> receiptController.sendReconReceipt(reconciliationReceiptRequest));
        assertNotNull(exception);
    }

    @Test
    void testSendCopyReceipt(){
        WhatsappReceiptRequest whatsappReceiptRequest = new WhatsappReceiptRequest();
        when(txnReceiptService.sendReceiptToWhatsApp(any())).thenReturn(new ApiResponse());
        ResponseEntity<ApiResponse> responseEntity = receiptController.sendCopyReceipt(whatsappReceiptRequest);
        assertNotNull(responseEntity);
    }

    @Test
    void testSendCopyReceiptThrowsException(){
        WhatsappReceiptRequest whatsappReceiptRequest = new WhatsappReceiptRequest();
        when(txnReceiptService.sendReceiptToWhatsApp(any())).thenThrow(new CommonValidationException("error-message",400));
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> receiptController.sendCopyReceipt(whatsappReceiptRequest));
        assertNotNull(exception);
    }

    @Test
    void testSendReconCopyReceipt(){
        QrReceiptFetchRequest qrReceiptFetchRequest = new QrReceiptFetchRequest();
        when(txnReceiptService.getReceiptUrl(any())).thenReturn(new ApiResponse());
        ResponseEntity<ApiResponse> responseEntity = receiptController.sendReconCopyReceipt(qrReceiptFetchRequest);
        assertNotNull(responseEntity);
    }

    @Test
    void testSendReconCopyReceiptThrowsException(){
        QrReceiptFetchRequest qrReceiptFetchRequest = new QrReceiptFetchRequest();
        when(txnReceiptService.getReceiptUrl(any())).thenThrow(new CommonValidationException("error-message ",400));
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> receiptController.sendReconCopyReceipt(qrReceiptFetchRequest));
        assertNotNull(exception);
    }

    @Test
    void testHealth(){
        ResponseEntity<ApiResponse> responseEntity = receiptController.health();
        assertNotNull(responseEntity);
    }

}
