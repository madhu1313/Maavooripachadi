package com.maavooripachadi.engage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class WhatsappGateway implements WhatsappClient {

    private static final Logger log = LoggerFactory.getLogger(WhatsappGateway.class);

    private final WhatsappProperties properties;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public WhatsappGateway(WhatsappProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean canSend() {
        return properties.isEnabled()
                && hasText(properties.getAccessToken())
                && hasText(properties.getPhoneNumberId());
    }

    @Override
    public String sendText(String phoneNumber, String message) {
        if (!canSend()) {
            throw new IllegalStateException("WhatsApp gateway is disabled or not configured");
        }
        String normalizedPhone = normalizePhone(phoneNumber);
        if (normalizedPhone == null) {
            throw new IllegalArgumentException("Phone number is required to send WhatsApp messages");
        }

        try {
            var payload = mapper.createObjectNode();
            payload.put("messaging_product", "whatsapp");
            payload.put("recipient_type", "individual");
            payload.put("to", normalizedPhone);
            var text = payload.putObject("text");
            text.put("preview_url", false);
            text.put("body", message);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s/%s/messages", properties.getApiBase(), properties.getPhoneNumberId())))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + properties.getAccessToken())
                    .POST(HttpRequest.BodyPublishers.ofString(payload.toString(), StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                JsonNode json = mapper.readTree(response.body());
                JsonNode messages = json.path("messages");
                if (messages.isArray() && messages.size() > 0) {
                    return messages.get(0).path("id").asText("wa-" + System.currentTimeMillis());
                }
                return "wa-" + System.currentTimeMillis();
            }
            throw new IllegalStateException("WhatsApp API error: " + response.statusCode() + " - " + response.body());
        } catch (Exception ex) {
            log.error("Failed to send WhatsApp message", ex);
            throw new IllegalStateException("Failed to send WhatsApp message", ex);
        }
    }

    private String normalizePhone(String raw) {
        if (raw == null) {
            return null;
        }
        String digits = raw.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return null;
        }

        if (digits.startsWith("00")) {
            digits = digits.substring(2);
        }

        String countryCode = sanitize(properties.getDefaultCountryCode());
        if (countryCode != null && !digits.startsWith(countryCode)) {
            digits = countryCode + digits;
        }
        return digits;
    }

    private String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String digits = value.replaceAll("\\D", "");
        return digits.isEmpty() ? null : digits;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
