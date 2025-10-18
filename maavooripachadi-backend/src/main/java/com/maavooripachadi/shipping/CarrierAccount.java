package com.maavooripachadi.shipping;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity(name = "ShippingCarrierAccount")
@Table(name = "ship_carrier_acct")
public class CarrierAccount extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CarrierCode carrier;

    private String accountId; // e.g., Shiprocket seller id
    private String apiKey;    // encrypted at rest in real setup
    private String apiSecret; // encrypted at rest
    private Boolean active = Boolean.TRUE;

    public CarrierCode getCarrier() { return carrier; }
    public void setCarrier(CarrierCode carrier) { this.carrier = carrier; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
