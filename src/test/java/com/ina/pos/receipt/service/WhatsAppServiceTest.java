package com.ina.pos.receipt.service;

import com.ina.pos.receipt.config.WhatsAppConfig;
import com.ina.pos.receipt.exception.CommonValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WhatsAppServiceTest {

    @Mock
    WebClient whatsAppWebClient;

    @Mock
    WhatsAppConfig config;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    WhatsAppService whatsAppService;

    Map<String, Object> body = new HashMap<>();

    byte[] mediaContent = "test-document".getBytes();
    String fileName = "test-file-name";

    @BeforeEach
    void setup(){
        when(whatsAppWebClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.header(anyString(), anyString())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(any())).thenReturn(requestBodyUriSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void testUploadMedia(){
        when(requestBodyUriSpec.body(any())).thenReturn(requestHeadersSpec);
        body.put("id", "test-id");
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(body));
        String uploadMedia = whatsAppService.uploadMedia(mediaContent, fileName);
        assertNotNull(uploadMedia);
    }

    @Test
    void testUploadMediaThrowsExceptionWhenResponseIsNull(){
        when(requestBodyUriSpec.body(any())).thenReturn(requestHeadersSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.empty());
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> whatsAppService.uploadMedia(mediaContent, fileName));
        assertNotNull(exception);
    }

    @Test
    void testUploadMediaThrowsException(){
        when(requestBodyUriSpec.body(any())).thenReturn(requestHeadersSpec);
        WebClientResponseException webClientResponseException = mock(WebClientResponseException.class);
        when(webClientResponseException.getResponseBodyAsString()).thenReturn("test-body");
        when(webClientResponseException.getStatusCode()).thenReturn(HttpStatusCode.valueOf(400));
        when(responseSpec.bodyToMono(Map.class)).thenThrow(webClientResponseException);
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> whatsAppService.uploadMedia(mediaContent, fileName));
        assertNotNull(exception);
    }

    @Test
    void testSendDocumentMessage(){
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("id", "test-id");
        body.put("messages", List.of(testMap));
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.just(body));
        String uploadMedia = whatsAppService.sendDocumentMessage("9876543210", "test-media","test-file-name", "file");
        assertNotNull(uploadMedia);
    }

    @Test
    void testSendDocumentMessageThrowsExceptionWhenResponseIsNull(){
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(responseSpec.bodyToMono(Map.class)).thenReturn(Mono.empty());
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> whatsAppService.sendDocumentMessage("987643210", "test-media","test-file-name", "file"));
        assertNotNull(exception);
    }

    @Test
    void testSendDocumentMessageThrowsException(){
        when(requestBodyUriSpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        WebClientResponseException webClientResponseException = mock(WebClientResponseException.class);
        when(webClientResponseException.getResponseBodyAsString()).thenReturn("test-body");
        when(webClientResponseException.getStatusCode()).thenReturn(HttpStatusCode.valueOf(400));
        when(responseSpec.bodyToMono(Map.class)).thenThrow(webClientResponseException);
        CommonValidationException exception = assertThrows(CommonValidationException.class,
                () -> whatsAppService.sendDocumentMessage("8907743210", "test","file-name", "files"));
        assertNotNull(exception);
    }

}
