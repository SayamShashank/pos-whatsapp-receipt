package com.ina.pos.receipt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PosReceiptRequest {

    @NotBlank(message = "ReceiptId is required")
    @JsonProperty("receiptId")
    private String receiptId;
    private String mobileNumber;

    private String batchNumber;
    private String rrn;

    private String pan;
    private String expiryDate;
    private String aid;

    @NotBlank(message = "Receipt number is required")
    private String receiptNumber;

    private String authCode;
    private String transactionType;
    private String currency;
    private String status;

    private String storeName;
    private String storeAddress;
    private String dateTime;

    private String paymentMethod;
    private String paymentBrand;

    private Double taxAmount;
    private Double discountAmount;

    @NotNull(message = "Total amount is required")
    private Double totalAmount;


    private PosDevice posDevice;
    private Merchant merchant;
    private Signature signature;
    private String additionalNotes;



    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PosDevice {
        private String posId;
        private String appVersion;
        private String deviceSerialNumber;
        private String model;
        private String networkType;
        private String emvEnabled;
        private String firmwareVersion;
        private String primaryHost;
        private String secondaryHost;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Merchant {
        private String merchantId;
        private String terminalId;
        private String cashierId;
        private String cashierName;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Signature {
        private Boolean customerSignatureRequired;
        private String customerSignatureImage;
    }
}