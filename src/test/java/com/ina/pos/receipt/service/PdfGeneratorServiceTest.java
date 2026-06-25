package com.ina.pos.receipt.service;

import com.ina.pos.receipt.dto.PosReceiptRequest;
import com.ina.pos.receipt.exception.CommonValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ina.pos.receipt.util.CommonTestUtil.getPosReceiptRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class PdfGeneratorServiceTest {

    @InjectMocks
    PdfGeneratorService pdfGeneratorService;

    @Test
    void testGenerateReceiptPdf() throws Exception {
        PosReceiptRequest posReceiptRequest = getPosReceiptRequest();
        pdfGeneratorService.loadTemplate();
        byte[] result = pdfGeneratorService.generateReceiptPdf(posReceiptRequest);
        assertNotNull(result);
    }

    @Test
    void testGenerateReceiptPdfThrowsException() {
        PosReceiptRequest posReceiptRequest = getPosReceiptRequest();
        posReceiptRequest.setPan("6232 8415 8921 7218");
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> pdfGeneratorService.generateReceiptPdf(posReceiptRequest));
        assertNotNull(exception);
    }

}