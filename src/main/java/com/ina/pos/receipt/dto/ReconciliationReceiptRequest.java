package com.ina.pos.receipt.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReconciliationReceiptRequest {



    @NotBlank(message = "ReceiptId is required")
    @JsonProperty("receiptId")
    private String receiptId;

    private String mobileNumber;

    @NotBlank(message = "Receipt number is required")
    @JsonProperty("receiptNumber")
    private String receiptNumber;

    @NotBlank(message = "Batch number is required")
    @JsonProperty("batchNumber")
    private String batchNumber;

    @NotBlank(message = "Transaction type is required")
    @JsonProperty("transactionType")
    private String transactionType;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 5)
    @JsonProperty("currency")
    private String currency;

    @NotBlank(message = "Store name is required")
    @JsonProperty("storeName")
    private String storeName;

    @NotBlank(message = "Store address is required")
    @JsonProperty("storeAddress")
    private String storeAddress;

    @NotBlank(message = "Date/time is required")
    @JsonProperty("dateTime")
    private String dateTime;

    @Valid
    @JsonProperty("approvedTxns")
    private List<TransactionDto> approvedTxns;

    @Valid
    @JsonProperty("declinedTxns")
    private List<TransactionDto> declinedTxns;



    @Min(0) @JsonProperty("saleCount")
    private int saleCount;
    @DecimalMin("0.00") @JsonProperty("saleAmount")
    private BigDecimal saleAmount;

    @Min(0) @JsonProperty("beziCount")
    private int beziCount;
    @DecimalMin("0.00") @JsonProperty("beziAmount")
    private BigDecimal beziAmount;

    @Min(0) @JsonProperty("cashCount")
    private int cashCount;
    @DecimalMin("0.00") @JsonProperty("cashAmount")
    private BigDecimal cashAmount;

    @Min(0) @JsonProperty("echoSaleCount")
    private int echoSaleCount;
    @DecimalMin("0.00") @JsonProperty("echoSaleAmount")
    private BigDecimal echoSaleAmount;

    @Min(0) @JsonProperty("zinaraPaymentCount")
    private int zinaraPaymentCount;
    @DecimalMin("0.00") @JsonProperty("zinaraPaymentAmount")
    private BigDecimal zinaraPaymentAmount;



    @Min(0) @JsonProperty("subTotalCount")
    private int subTotalCount;
    @DecimalMin("0.00") @JsonProperty("subTotalAmount")
    private BigDecimal subTotalAmount;

    @Min(0) @JsonProperty("totalCount")
    private int totalCount;
    @DecimalMin("0.00") @JsonProperty("totalAmount")
    private BigDecimal totalAmount;



    @Min(0) @JsonProperty("offlineTotalCount")
    private int offlineTotalCount;
    @DecimalMin("0.00") @JsonProperty("offlineTotalAmount")
    private BigDecimal offlineTotalAmount;

    @Min(0) @JsonProperty("onlineTotalCount")
    private int onlineTotalCount;
    @DecimalMin("0.00") @JsonProperty("onlineTotalAmount")
    private BigDecimal onlineTotalAmount;



    @Valid
    @NotNull(message = "POS device info is required")
    @JsonProperty("posDevice")
    private PosDeviceDto posDevice;

    @Valid
    @NotNull(message = "Merchant info is required")
    @JsonProperty("merchant")
    private MerchantDto merchant;
    @JsonProperty("additionalNotes")
    private String additionalNotes;





    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionDto {

        @NotBlank @JsonProperty("receiptNumber")
        private String receiptNumber;

        @NotBlank @JsonProperty("dateTime")
        private String dateTime;

        @NotBlank @JsonProperty("pan")
        private String pan;

        @NotBlank @JsonProperty("transactionType")
        private String transactionType;

        @NotNull @DecimalMin("0.00")
        @JsonProperty("amount")
        private BigDecimal amount;

        @NotBlank @Size(min = 3, max = 5)
        @JsonProperty("currency")
        private String currency;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PosDeviceDto {

        @NotBlank @JsonProperty("posId")
        private String posId;
        @NotBlank @JsonProperty("appVersion")
        private String appVersion;
        @NotBlank @JsonProperty("deviceSerialNumber")
        private String deviceSerialNumber;
        @NotBlank @JsonProperty("model")
        private String model;
        @NotBlank @JsonProperty("networkType")
        private String networkType;
        @NotBlank @JsonProperty("emvEnabled")
        private String emvEnabled;
        @JsonProperty("firmwareVersion")
        private String firmwareVersion;
        @JsonProperty("primaryHost")
        private String primaryHost;
        @JsonProperty("secondaryHost")
        private String secondaryHost;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MerchantDto {

        @NotBlank @JsonProperty("merchantId")
        private String merchantId;
        @NotBlank @JsonProperty("terminalId")
        private String terminalId;
        @NotBlank @JsonProperty("cashierId")
        private String cashierId;
        @JsonProperty("cashierName")
        private String cashierName;
    }
}