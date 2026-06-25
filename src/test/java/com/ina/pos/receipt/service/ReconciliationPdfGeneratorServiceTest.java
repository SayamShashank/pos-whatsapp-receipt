package com.ina.pos.receipt.service;

import com.ina.pos.receipt.dto.ReconciliationReceiptRequest;
import com.ina.pos.receipt.exception.CommonValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.ina.pos.receipt.util.CommonTestUtil.getReconciliationReceiptRequest;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ReconciliationPdfGeneratorServiceTest {

    @InjectMocks
    ReconciliationPdfGeneratorService reconciliationPdfGeneratorService;

    @Test
    void testGenerateReceiptPdf() throws Exception {
        ReconciliationReceiptRequest request = getReconciliationReceiptRequest();
        reconciliationPdfGeneratorService.loadTemplate();
        byte[] result = reconciliationPdfGeneratorService.generateReconciliationPdf(request);
        assertNotNull(result);
    }

    @Test
    void testGenerateReceiptPdfThrowsException() {
        ReconciliationReceiptRequest request = getReconciliationReceiptRequest();
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> reconciliationPdfGeneratorService.generateReconciliationPdf(request));
        assertNotNull(exception);
    }


}
