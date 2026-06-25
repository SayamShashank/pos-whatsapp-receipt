package com.ina.pos.receipt.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class WhatsAppConfigTest {

    @InjectMocks
    WhatsAppConfig whatsAppConfig;

    @Test
    void testWebClientBeanCreation() {
        setField(whatsAppConfig, "baseUrl", "https://api.test.whatsapp.com");
        setField(whatsAppConfig, "phoneNumberId", "12345");
        setField(whatsAppConfig, "accessToken", "token");
        setField(whatsAppConfig, "businessAccountId", "biz123");
        WebClient client = whatsAppConfig.whatsAppWebClient();
        assertNotNull(client);
    }

}
