package com.ina.pos.receipt.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QrReceiptFetchRequest {

    @NotBlank(message = "Receipt number is required")
    private String referenceNumber;
}