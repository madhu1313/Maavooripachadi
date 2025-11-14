package com.maavooripachadi.engage;

public interface WhatsappClient {
    boolean canSend();
    String sendText(String phoneNumber, String message);
}
