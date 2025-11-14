package com.maavooripachadi.payments.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RazorpayGatewayTest {

    @Test
    void createGatewayOrderThrowsWhenNotConfigured() {
        RazorpayGateway gateway = new RazorpayGateway();
        assertThrows(IllegalStateException.class, () -> gateway.createGatewayOrder(new PaymentAttempt()));
    }

    @Test
    void createGatewayOrderReturnsIdOnSuccess() throws Exception {
        HttpClient httpClient = mock(HttpClient.class);
        @SuppressWarnings("unchecked")
        HttpResponse<String> response = mock(HttpResponse.class);
        when(response.statusCode()).thenReturn(200);
        when(response.body()).thenReturn("{\"id\":\"order_123\"}");
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(response);

        RazorpayGateway gateway = new RazorpayGateway(httpClient, new ObjectMapper());
        setConfig(gateway, "rzp_live_key", "super-secret", "https://api.example.com");

        PaymentAttempt attempt = new PaymentAttempt();
        attempt.setOrderNo("ORD-1");
        attempt.setAmountPaise(5000);
        attempt.setCurrency("INR");

        String id = gateway.createGatewayOrder(attempt);

        assertEquals("order_123", id);
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        HttpRequest request = requestCaptor.getValue();
        assertEquals(URI.create("https://api.example.com/orders"), request.uri());
        String authHeader = request.headers().firstValue("Authorization").orElse("");
        String expectedAuth = Base64.getEncoder()
                .encodeToString("rzp_live_key:super-secret".getBytes(StandardCharsets.UTF_8));
        assertEquals("Basic " + expectedAuth, authHeader);
    }

    @Test
    void verifySignatureRespectsOverride() {
        RazorpayGateway gateway = new RazorpayGateway();
        setConfig(gateway, "rzp_live_key", "secret", "https://api.example.com");

        String payload = "{\"order_id\":\"order_1\"}";
        String overrideSecret = "override";
        String signature = Hmac.sha256Hex(overrideSecret, payload);

        boolean valid = gateway.verifySignature(payload, signature, overrideSecret);
        assertEquals(true, valid);
    }

    private static void setConfig(RazorpayGateway gateway, String key, String secret, String apiBase) {
        ReflectionTestUtils.setField(gateway, "key", key);
        ReflectionTestUtils.setField(gateway, "secret", secret);
        ReflectionTestUtils.setField(gateway, "apiBase", apiBase);
    }
}
