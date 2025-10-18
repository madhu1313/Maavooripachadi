package com.maavooripachadi.payments.gateway;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;


public class Hmac {
    public static String sha256Hex(String secret, String payload){
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] out = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : out){ sb.append(String.format("%02x", b)); }
            return sb.toString();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}