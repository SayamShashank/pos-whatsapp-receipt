package com.ina.pos.receipt.service;

import com.ina.pos.receipt.dto.ReconciliationReceiptRequest;
import com.ina.pos.receipt.exception.CommonValidationException;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
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
import java.util.List;

@Slf4j
@Service
public class ReconciliationPdfGeneratorService {

    private static final String TEMPLATE_PATH = "templates/reconciliation-receipt-template.html";

    private static final DateTimeFormatter INPUT_FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final DateTimeFormatter OUTPUT_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private String htmlTemplate;


    @PostConstruct
    public void loadTemplate() throws Exception {
        ClassPathResource resource = new ClassPathResource(TEMPLATE_PATH);
        try (InputStream is = resource.getInputStream()) {
            htmlTemplate = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        }
        log.info("Reconciliation receipt template loaded successfully");
    }


    public byte[] generateReconciliationPdf(ReconciliationReceiptRequest receipt) {
        try {
            log.info("Generating reconciliation PDF: receipt={} batch={}",
                    receipt.getReceiptNumber(), receipt.getBatchNumber());

            String html = buildReconciliationHtml(receipt);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(html, outputStream, new ConverterProperties());

            byte[] pdf = outputStream.toByteArray();
            log.info("Reconciliation PDF generated successfully, size={} bytes", pdf.length);

            return pdf;

        } catch (Exception e) {
            log.error("Reconciliation PDF generation failed", e);
            throw new CommonValidationException(
                    "Reconciliation PDF generation failed: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    private String buildReconciliationHtml(ReconciliationReceiptRequest r) {

        String formattedDate = formatDateTime(r.getDateTime());
        String currency      = r.getCurrency() != null ? r.getCurrency() : "";

        return htmlTemplate
                .replace("{{storeName}}",    safe(r.getStoreName()))
                .replace("{{storeAddress}}", safe(r.getStoreAddress()))

                .replace("{{receiptNumber}}",   safe(r.getReceiptNumber()))
                .replace("{{batchNumber}}",     safe(r.getBatchNumber()))
                .replace("{{transactionType}}", safe(r.getTransactionType()))
                .replace("{{currency}}",        safe(r.getCurrency()))
                .replace("{{mobileNumber}}",    safe(r.getMobileNumber()))
                .replace("{{dateTime}}",        formattedDate)

                .replace("{{approvedTxnsBlock}}", buildTransactionTableBlock(r.getApprovedTxns(), currency, true))
                .replace("{{declinedTxnsBlock}}", buildTransactionTableBlock(r.getDeclinedTxns(), currency, false))

                .replace("{{saleCount}}",            String.valueOf(r.getSaleCount()))
                .replace("{{saleAmount}}",           formatAmount(currency, r.getSaleAmount()))
                .replace("{{beziCount}}",            String.valueOf(r.getBeziCount()))
                .replace("{{beziAmount}}",           formatAmount(currency, r.getBeziAmount()))
                .replace("{{cashCount}}",            String.valueOf(r.getCashCount()))
                .replace("{{cashAmount}}",           formatAmount(currency, r.getCashAmount()))
                .replace("{{echoSaleCount}}",        String.valueOf(r.getEchoSaleCount()))
                .replace("{{echoSaleAmount}}",       formatAmount(currency, r.getEchoSaleAmount()))
                .replace("{{zinaraPaymentCount}}",   String.valueOf(r.getZinaraPaymentCount()))
                .replace("{{zinaraPaymentAmount}}",  formatAmount(currency, r.getZinaraPaymentAmount()))
                .replace("{{subTotalCount}}",        String.valueOf(r.getSubTotalCount()))
                .replace("{{subTotalAmount}}",       formatAmount(currency, r.getSubTotalAmount()))
                .replace("{{totalCount}}",           String.valueOf(r.getTotalCount()))
                .replace("{{totalAmount}}",          formatAmount(currency, r.getTotalAmount()))

                .replace("{{onlineTotalCount}}",   String.valueOf(r.getOnlineTotalCount()))
                .replace("{{onlineTotalAmount}}",  formatAmount(currency, r.getOnlineTotalAmount()))
                .replace("{{offlineTotalCount}}",  String.valueOf(r.getOfflineTotalCount()))
                .replace("{{offlineTotalAmount}}", formatAmount(currency, r.getOfflineTotalAmount()))

                .replace("{{merchantBlock}}", buildMerchantBlock(r.getMerchant()))
                .replace("{{deviceBlock}}",   buildDeviceBlock(r.getPosDevice()))

                .replace("{{additionalNotes}}", safe(r.getAdditionalNotes()));
    }


    private String buildTransactionTableBlock(
            List<ReconciliationReceiptRequest.TransactionDto> txns,
            String currency,
            boolean approved) {

        String badgeClass = approved ? "badge-approved" : "badge-declined";
        String badgeIcon  = approved ? "&#10003;&nbsp;APPROVED" : "&#10007;&nbsp;DECLINED";
        String amtClass   = approved ? "amt-approved" : "amt-declined";

        int count = (txns != null) ? txns.size() : 0;

        StringBuilder sb = new StringBuilder();
        sb.append("<div class='txn-card'>")
          .append("<div class='card-header-row'>")
          .append("<div class='count-label'>").append(count).append(" transaction(s)</div>")
          .append("<div class='status-badge ").append(badgeClass).append("'>").append(badgeIcon).append("</div> ")
          .append("</div>");  // card-header-row

        if (txns == null || txns.isEmpty()) {
            sb.append("<div class='no-txn'>No ")
              .append(approved ? "approved" : "declined")
              .append(" transactions in this batch.</div>");
        } else {
            sb.append("<table class='txn-table'>")
              .append("<thead><tr>")
              .append("<th>Rcpt#</th>")
              .append("<th>Date / Time</th>")
              .append("<th>PAN</th>")
              .append("<th>Type</th>")
              .append("<th class='right'>Amount</th>")
              .append("</tr></thead>")
              .append("<tbody>");

            for (ReconciliationReceiptRequest.TransactionDto txn : txns) {
                String amt = (txn.getAmount() != null)
                        ? String.format("%.2f", txn.getAmount())
                        : "0.00";

                sb.append("<tr>")
                  .append("<td>").append(safe(txn.getReceiptNumber())).append("</td> ")
                  .append("<td>").append(safe(txn.getDateTime())).append("</td>")
                  .append("<td class='pan-cell'>").append(safe(txn.getPan())).append("</td> ")
                  .append("<td><span class='type-pill'>").append(safe(txn.getTransactionType())).append("</span></td>")
                  .append("<td class='right ").append(amtClass).append("'>")
                  .append(currency).append("&nbsp;").append(amt)
                  .append(" </td>")
                  .append("</tr>");
            }

            sb.append("</tbody></table>");
        }

        sb.append("</div> "); // txn-card
        return sb.toString();
    }


    private String buildMerchantBlock(ReconciliationReceiptRequest.MerchantDto m) {
        if (m == null) return "";

        return "<div class='section'>" +
                "<div class='section-title'>Merchant Info</div>" +
                "<div class='info-grid'>" +
                row("Merchant ID", safe(m.getMerchantId())) +
                row("Terminal ID", safe(m.getTerminalId())) +
                row("Cashier", safe(m.getCashierName()) + " (" + safe(m.getCashierId()) + ")") +
                "</div></div>" +
                "<hr class='divider'/>";
    }


    private String buildDeviceBlock(ReconciliationReceiptRequest.PosDeviceDto d) {
        if (d == null) return "";

        return "<div class='section'>" +
                "<div class='section-title'>Device Info</div>" +
                "<div class='info-grid'>" +
                row("POS ID",      safe(d.getPosId())) +
                row("Model",       safe(d.getModel())) +
                row("Serial",      safe(d.getDeviceSerialNumber())) +
                row("App Version", safe(d.getAppVersion())) +
                row("Firmware",    safe(d.getFirmwareVersion())) +
                row("Network",     safe(d.getNetworkType())) +
                row("EMV",         safe(d.getEmvEnabled())) +
                row("Primary Host",safe(d.getPrimaryHost())) +
                "</div></div>" +
                "<hr class='divider'/>";
    }


    private String row(String label, String value) {
        return "<div class='row'>" +
                "<span class='label'>" + label + "</span>" +
                "<span class='value'>" + value + "</span>" +
                "</div>";
    }


    private String formatAmount(String currency, java.math.BigDecimal amount) {
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