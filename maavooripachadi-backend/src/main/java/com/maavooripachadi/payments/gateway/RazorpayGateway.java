package com.maavooripachadi.payments.gateway;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Component
public class RazorpayGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(RazorpayGateway.class);

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();


    @Value("${payments.razorpay.key:rzp_test}")
    private String key;


    @Value("${payments.razorpay.secret:secret}")
    private String secret;

    @Value("${payments.razorpay.base-url:https://api.razorpay.com/v1}")
    private String apiBase;

    private boolean isConfigured(){
        return key != null && !key.isBlank() && secret != null && !secret.isBlank()
                && !key.equals("rzp_test") && !secret.equals("secret");
    }


    @Override
    public String createGatewayOrder(PaymentAttempt attempt){
        if (!isConfigured()) {
            throw new IllegalStateException("Online payments are disabled because Razorpay key/secret are not configured.");
        }

        try {
            var body = mapper.createObjectNode();
            int amount = attempt.getAmountPaise() > 0 ? attempt.getAmountPaise() : 100;
            body.put("amount", amount);
            body.put("currency", attempt.getCurrency() != null ? attempt.getCurrency() : "INR");
            body.put("receipt", attempt.getOrderNo());
            body.put("payment_capture", 1);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiBase + "/orders"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + basicAuthHeader())
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode node = mapper.readTree(response.body());
                String id = node.path("id").asText(null);
                if (id != null && !id.isBlank()) {
                    return id;
                }
                throw new IllegalStateException("Razorpay responded without an order id: " + response.body());
            }
            throw new IllegalStateException(
                "Failed to create Razorpay order. Status: " + response.statusCode() + ", body: " + response.body());
        } catch (Exception ex) {
            log.error("Error calling Razorpay Orders API", ex);
            throw new IllegalStateException("Could not create an order with Razorpay. Please try again or contact support.", ex);
        }
    }


    @Override
    public boolean verifySignature(String payload, String signature, String secretOverride){
        if (!isConfigured()) {
            return true;
        }
        String s = (secretOverride != null) ? secretOverride : secret;
        String expected = Hmac.sha256Hex(s, payload);
        return expected.equals(signature);
    }


    @Override
    public String capture(String gatewayPaymentId, int amountPaise){
// TODO: call Razorpay capture API
        return gatewayPaymentId;
    }


    @Override
    public String refund(String gatewayPaymentId, int amountPaise, String reason){
// TODO: call Razorpay refund API
        return "rfnd_" + System.currentTimeMillis();
    }

    private String basicAuthHeader(){
        String raw = key + ":" + secret;
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }
}
