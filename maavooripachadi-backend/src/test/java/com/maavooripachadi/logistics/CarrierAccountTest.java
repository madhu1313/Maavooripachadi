package com.maavooripachadi.logistics;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CarrierAccountTest {

    @Test
    void gettersAndSettersRoundTrip() {
        CarrierAccount account = new CarrierAccount();
        account.setCarrier("DTDC");
        account.setApiKey("key");
        account.setApiSecret("secret");
        account.setEnabled(false);

        assertEquals("DTDC", account.getCarrier());
        assertEquals("key", account.getApiKey());
        assertEquals("secret", account.getApiSecret());
        assertEquals(false, account.getEnabled());
    }

    @Test
    void defaultsToEnabled() {
        CarrierAccount account = new CarrierAccount();
        assertTrue(account.getEnabled());
    }
}
