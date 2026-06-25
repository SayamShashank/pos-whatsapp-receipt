package com.ina.pos.receipt.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.ina.pos.receipt.dto.PosReceiptRequest;
import com.ina.pos.receipt.exception.CommonValidationException;
import com.ina.pos.receipt.util.CardBrandUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.ina.pos.receipt.constants.AppConstants.*;

@Slf4j
@Service
public class PdfGeneratorService {



    private String htmlTemplate;


    @PostConstruct
    public void loadTemplate() throws Exception {
        ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
        try (InputStream is = resource.getInputStream()) {
            htmlTemplate = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
        log.info("Receipt template loaded successfully");
    }


    public byte[] generateReceiptPdf(PosReceiptRequest receipt) {
        try {
            log.info("Generating receipt PDF: {}", receipt.getReceiptNumber());

            String html = buildReceiptHtml(receipt);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(html, outputStream, new ConverterProperties());

            byte[] pdf = outputStream.toByteArray();
            log.info("PDF generated successfully, size={} bytes", pdf.length);

            return pdf;

        } catch (Exception e) {
            log.error("PDF generation failed", e);
            throw new CommonValidationException(
                    "PDF generation failed: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    private String buildReceiptHtml(PosReceiptRequest r) {

        String formattedDate = formatDateTime(r.getDateTime());
        String currency = r.getCurrency() != null ? r.getCurrency() : "";

        String brand = CardBrandUtil.resolveBrand(r.getPan());

        return htmlTemplate
                .replace("{{storeName}}", safe(r.getStoreName()))
                .replace("{{storeAddress}}", safe(r.getStoreAddress()))

                .replace("{{receiptNumber}}", safe(r.getReceiptNumber()))
                .replace("{{authCode}}", safe(r.getAuthCode()))
                .replace("{{transactionType}}", safe(r.getTransactionType()))
                .replace("{{status}}", safe(r.getStatus()))
                .replace("{{dateTime}}", formattedDate)

                .replace("{{batchNumber}}", safe(r.getBatchNumber()))
                .replace("{{rrn}}", safe(r.getRrn()))

                .replace("{{pan}}", safe(r.getPan()))
                .replace("{{expiryDate}}", safe(r.getExpiryDate()))
                .replace("{{aid}}", safe(r.getAid()))

                .replace("{{paymentBrand}}", brand)
                .replace("{{paymentMethod}}", safe(r.getPaymentMethod()))
                .replace("{{cardNumberBlock}}", buildCardBlock(r.getPan()))

                .replace("{{taxAmount}}", formatAmount(currency, r.getTaxAmount()))
                .replace("{{totalAmount}}", formatAmount(currency, r.getTotalAmount()))
                .replace("{{discountBlock}}", buildDiscountBlock(currency, r.getDiscountAmount()))

                .replace("{{merchantBlock}}", buildMerchantBlock(r.getMerchant()))
                .replace("{{deviceBlock}}", buildDeviceBlock(r.getPosDevice()))

                .replace("{{additionalNotes}}", safe(r.getAdditionalNotes()));
    }


    private String buildCardBlock(String pan) {
        if (pan == null || pan.isBlank()) return "";
        return "<div class='card-number'>" + pan + "</div>";
    }


    private String buildDiscountBlock(String currency, Double discount) {
        if (discount == null || discount <= 0) return "";
        return "<div class='row discount-row'>" +
                "<span class='label'>Discount</span>" +
                "<span class='value'>-" + formatAmount(currency, discount) + "</span>" +
                "</div>";
    }


    private String buildMerchantBlock(PosReceiptRequest.Merchant m) {
        if (m == null) return "";

        return "<div class='section'>" +
                "<div class='section-title'>Merchant Info</div>" +
                "<div class='info-grid'>" +
                row("Merchant ID", safe(m.getMerchantId())) +
                row("Terminal ID", safe(m.getTerminalId())) +
                row("Cashier", safe(m.getCashierName()) + " (" + safe(m.getCashierId()) + ")") +
                "</div></div><hr class='divider'/>";
    }


    private String buildDeviceBlock(PosReceiptRequest.PosDevice d) {
        if (d == null) return "";

        return "<div class='section'>" +
                "<div class='section-title'>Device Info</div>" +
                "<div class='info-grid'>" +
                row("POS ID", safe(d.getPosId())) +
                row("Model", safe(d.getModel())) +
                row("Serial", safe(d.getDeviceSerialNumber())) +
                row("App Version", safe(d.getAppVersion())) +
                row("Firmware", safe(d.getFirmwareVersion())) +
                row("Network", safe(d.getNetworkType())) +
                row("EMV", safe(d.getEmvEnabled())) +
                "</div></div><hr class='divider'/>";
    }


    private String row(String label, String value) {
        return "<div class='row'>" +
                "<span class='label'>" + label + "</span>" +
                "<span class='value'>" + value + "</span>" +
                "</div>";
    }


    private String formatAmount(String currency, Double amount) {
        return currency + " " + (amount == null ? "0.00" : String.format("%.2f", amount));
    }


    private String formatDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) return "N/A";
        try {
            LocalDateTime ldt = LocalDateTime.parse(dateTimeStr, INPUT_FMT);
            return ldt.format(OUTPUT_FMT);
        } catch (Exception e) {
            return dateTimeStr;
        }
    }


    private String safe(String value) {
        return (value != null && !value.isBlank()) ? value : "N/A";
    }
}