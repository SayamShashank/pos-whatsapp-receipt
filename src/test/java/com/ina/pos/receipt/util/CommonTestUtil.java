package com.ina.pos.receipt.util;

import com.ina.pos.receipt.dao.entity.ReceiptUrls;
import com.ina.pos.receipt.dto.PosReceiptRequest;
import com.ina.pos.receipt.dto.ReconciliationReceiptRequest;

import java.math.BigDecimal;
import java.util.List;

public class CommonTestUtil {

    public static PosReceiptRequest getPosReceiptRequest(){

        PosReceiptRequest.PosDevice posDevice = new PosReceiptRequest.PosDevice();
        posDevice.setAppVersion("3.4.1");
        posDevice.setDeviceSerialNumber("SNV2P-983745");
        posDevice.setModel("Sunmi V2 Pro");
        posDevice.setNetworkType("WIFI");

        PosReceiptRequest.Merchant merchant = new PosReceiptRequest.Merchant();
        merchant.setMerchantId("MRC-55678");
        merchant.setTerminalId("TID-998877");
        merchant.setCashierId("CASH-07");
        merchant.setCashierName("Ahmed");

        PosReceiptRequest.Signature signature = new PosReceiptRequest.Signature();
        signature.setCustomerSignatureRequired(false);
        signature.setCustomerSignatureImage(null);

        PosReceiptRequest request = new PosReceiptRequest();

        request.setPan("4132 8415 8921 7218");
        request.setMobileNumber("+916300773594");
        request.setReceiptNumber("RCPT-20240417-12346");

        request.setStoreName("EdgeStore Hypermarket");
        request.setStoreAddress("King Fahd Road, Al Khobar, Saudi Arabia");
        request.setDateTime("2026-04-17T16:34:00+03:00");

        request.setAuthCode("A92F34");
        request.setTransactionType("SALE");
        request.setCurrency("SAR");

        request.setPaymentMethod("CARD");
        request.setPaymentBrand("RUPAY");

        request.setTaxAmount(7.50);
        request.setDiscountAmount(10.00);
        request.setTotalAmount(140.00);

        request.setPosDevice(posDevice);
        request.setMerchant(merchant);
        request.setSignature(signature);

        return request;
    }

    public static ReconciliationReceiptRequest getReconciliationReceiptRequest(){
        ReconciliationReceiptRequest.TransactionDto txn = new ReconciliationReceiptRequest.TransactionDto();
        txn.setReceiptNumber("RCPT-20260427-0001");
        txn.setDateTime("2026-04-27T09:15:00+03:00");
        txn.setPan("************4582");
        txn.setTransactionType("SALE");
        txn.setAmount(new BigDecimal("250.00"));
        txn.setCurrency("SAR");

        ReconciliationReceiptRequest.PosDeviceDto posDevice =
                ReconciliationReceiptRequest.PosDeviceDto.builder()
                        .posId("POS-001")
                        .appVersion("3.4.1")
                        .deviceSerialNumber("SNV2P-983745")
                        .model("Sunmi V2 Pro")
                        .networkType("WIFI")
                        .emvEnabled("YES")
                        .firmwareVersion("FW-2.1.0")
                        .primaryHost("https://host1.payment-gateway.sa")
                        .secondaryHost("https://host2.payment-gateway.sa")
                        .build();

        ReconciliationReceiptRequest.MerchantDto merchant =
                ReconciliationReceiptRequest.MerchantDto.builder()
                        .merchantId("MRC-55678")
                        .terminalId("TID-998877")
                        .cashierId("CASH-07")
                        .cashierName("Ahmed")
                        .build();

        return ReconciliationReceiptRequest.builder()

                        .mobileNumber("+916300773594")
                        .receiptNumber("RECON-20260427-BATCH001")
                        .batchNumber("BATCH-20260427-001")
                        .transactionType("RECONCILIATION")
                        .currency("SAR")
                        .storeName("EdgeStore Hypermarket")
                        .storeAddress("King Fahd Road, Al Khobar, Saudi Arabia")
                        .dateTime("2026-04-27T18:00:00+03:00")
                        .approvedTxns(List.of(txn))
                        .declinedTxns(List.of())
                        .saleCount(2)
                        .saleAmount(new BigDecimal("390.00"))
                        .beziCount(1)
                        .beziAmount(new BigDecimal("85.50"))
                        .cashCount(0)
                        .cashAmount(BigDecimal.ZERO)
                        .echoSaleCount(0)
                        .echoSaleAmount(BigDecimal.ZERO)
                        .zinaraPaymentCount(0)
                        .zinaraPaymentAmount(BigDecimal.ZERO)
                        .subTotalCount(3)
                        .subTotalAmount(new BigDecimal("475.50"))
                        .totalCount(4)
                        .totalAmount(new BigDecimal("795.50"))
                        .onlineTotalCount(3)
                        .onlineTotalAmount(new BigDecimal("475.50"))
                        .offlineTotalCount(0)
                        .offlineTotalAmount(BigDecimal.ZERO)
                        .posDevice(posDevice)
                        .merchant(merchant)
                        .additionalNotes("End-of-day batch reconciliation — Shift 1")
                        .build();
    }


    public static ReceiptUrls getReceiptUrls() {
        return ReceiptUrls
                .builder()
                .url("https://ina-payments-gtms-files.s3.ap-south-1.amazonaws.com/receipt/pdf/Receipt_RCPT-20240417-12346.pdf")
                .total("100")
                .id(1234L)
                .currency("USD")
                .build();
    }

}
