package com.maavooripachadi.engage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WhatsappGatewayTest {

    private WhatsappProperties properties;

    @BeforeEach
    void setUp() {
        properties = new WhatsappProperties();
        properties.setEnabled(true);
        properties.setAccessToken("test-token");
        properties.setPhoneNumberId("98765");
        properties.setDefaultCountryCode("91");
        properties.setApiBase("https://graph.facebook.com/v18.0");
    }

    @Test
    void canSendRequiresConfiguration() {
        WhatsappGateway gateway = new WhatsappGateway(properties, mock(HttpClient.class), new ObjectMapper());
        assertTrue(gateway.canSend());

        properties.setEnabled(false);
        gateway = new WhatsappGateway(properties, mock(HttpClient.class), new ObjectMapper());
        assertFalse(gateway.canSend());
    }

    @Test
    void sendTextThrowsWhenGatewayDisabled() {
        properties.setEnabled(false);
        WhatsappGateway gateway = new WhatsappGateway(properties, mock(HttpClient.class), new ObjectMapper());

        assertThrows(IllegalStateException.class, () -> gateway.sendText("12345", "Hi"));
    }

    @Test
    void sendTextThrowsWhenPhoneMissing() {
        WhatsappGateway gateway = new WhatsappGateway(properties, mock(HttpClient.class), new ObjectMapper());
        assertThrows(IllegalArgumentException.class, () -> gateway.sendText("   ", "Hi"));
    }

    @Test
    void sendTextReturnsMessageIdOnSuccess() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"messages\":[{\"id\":\"wa-msg-1\"}]}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        WhatsappGateway gateway = new WhatsappGateway(properties, httpClient, new ObjectMapper());

        String id = gateway.sendText("85558 59667", "Hello");

        assertEquals("wa-msg-1", id);
        verify(httpClient).send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));
    }

    @Test
    void sendTextThrowsForApiErrors() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(500);
        when(response.body()).thenReturn("downstream-error");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        WhatsappGateway gateway = new WhatsappGateway(properties, httpClient, new ObjectMapper());

        assertThrows(IllegalStateException.class, () -> gateway.sendText("918555859667", "Hello"));
    }

    @Test
    void sendTextStripsInternationalPrefix() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"messages\":[{\"id\":\"wa-msg-2\"}]}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        WhatsappGateway gateway = new WhatsappGateway(properties, httpClient, new ObjectMapper());

        String id = gateway.sendText("0091 85558 59667", "Hi");

        assertEquals("wa-msg-2", id);
    }

    @Test
    void sendTextLeavesDigitsWhenCountryCodeMissing() throws Exception {
        properties.setDefaultCountryCode(null);
        HttpClient httpClient = mock(HttpClient.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"messages\":[{\"id\":\"wa-msg-3\"}]}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        WhatsappGateway gateway = new WhatsappGateway(properties, httpClient, new ObjectMapper());

        String id = gateway.sendText("+1 (650) 555-1212", "Hi");

        assertEquals("wa-msg-3", id);
    }

    @Test
    void defaultConstructorUsesDefaultDependencies() {
        WhatsappGateway gateway = new WhatsappGateway(properties);
        assertTrue(gateway.canSend());
    }

    @Test
    void constructorRequiresNonNullDependencies() {
        assertThrows(NullPointerException.class,
                () -> new WhatsappGateway(null, mock(HttpClient.class), new ObjectMapper()));
        assertThrows(NullPointerException.class,
                () -> new WhatsappGateway(properties, null, new ObjectMapper()));
        assertThrows(NullPointerException.class,
                () -> new WhatsappGateway(properties, mock(HttpClient.class), null));
    }
}
