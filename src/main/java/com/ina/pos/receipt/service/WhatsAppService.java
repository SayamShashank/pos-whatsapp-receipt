package com.ina.pos.receipt.service;

import com.ina.pos.receipt.config.WhatsAppConfig;
import com.ina.pos.receipt.exception.CommonValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final WebClient whatsAppWebClient;
    private final WhatsAppConfig config;

    public String uploadMedia(byte[] pdfBytes, String filename) {
        log.info("Uploading PDF to WhatsApp Media API. Filename: {}, Size: {} bytes",
                filename, pdfBytes.length);

        try {

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            ByteArrayResource resource = new ByteArrayResource(pdfBytes) {
                @Override
                public String getFilename() {
                    return filename;
                }
            };
            body.add("file", resource);
            body.add("messaging_product", "whatsapp");
            body.add("type", "application/pdf");

            Map<String, Object> response = whatsAppWebClient.post()
                .uri("/" + config.getPhoneNumberId() + "/media")
                .header("Authorization", "Bearer " + config.getAccessToken())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(body))
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            if (response == null || !response.containsKey("id")) {
                throw new CommonValidationException("WhatsApp media upload failed: no media ID returned",
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            String mediaId = (String) response.get("id");
            log.info("PDF uploaded successfully. Media ID: {}", mediaId);
            return mediaId;

        } catch (WebClientResponseException exception) {
            log.error("WhatsApp Media Upload Error: {} - {}", exception.getStatusCode(), exception.getResponseBodyAsString());
            throw new CommonValidationException("WhatsApp media upload failed: " + exception.getResponseBodyAsString(),
                    exception.getStatusCode().value());
        }
    }

    public String sendDocumentMessage(String toPhoneNumber,
                                      String mediaId,
                                      String filename,
                                      String caption) {
        log.info("Sending document message to: {}, mediaId: {}", toPhoneNumber, mediaId);

        try {
            Map<String, Object> document = new HashMap<>();
            document.put("id", mediaId);
            document.put("filename", filename);
            document.put("caption", caption);

            Map<String, Object> payload = new HashMap<>();
            payload.put("messaging_product", "whatsapp");
            payload.put("recipient_type", "individual");
            payload.put("to", toPhoneNumber);
            payload.put("type", "document");
            payload.put("document", document);

            Map<String, Object> response = whatsAppWebClient.post()
                .uri("/" + config.getPhoneNumberId() + "/messages")
                .header("Authorization", "Bearer " + config.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            if (response == null) {
                throw new CommonValidationException("WhatsApp send message failed: null response",
                        HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            String messageId = extractMessageId(response);
            log.info("Document message sent successfully. WhatsApp Message ID: {}", messageId);
            return messageId;

        } catch (WebClientResponseException exception) {
            log.error("WhatsApp Send Message Error: {} - {}", exception.getStatusCode(), exception.getResponseBodyAsString());
            throw new CommonValidationException("WhatsApp send message failed: " + exception.getResponseBodyAsString(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private String extractMessageId(Map<String, Object> response) {
        try {
            var messages = (java.util.List<Map<String, Object>>) response.get("messages");
            if (messages != null && !messages.isEmpty()) {
                return (String) messages.get(0).get("id");
            }
        } catch (Exception e) {
            log.warn("Could not extract message ID from response: {}", response);
        }
        return "unknown";
    }
}
