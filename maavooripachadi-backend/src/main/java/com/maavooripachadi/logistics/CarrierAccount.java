package com.maavooripachadi.logistics;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "carrier_account")
public class CarrierAccount extends BaseEntity {
    @Column(nullable = false)
    private String carrier; // SHIPROCKET, DTDC


    private String apiKey;
    private String apiSecret;
    private Boolean enabled = Boolean.TRUE;


    // getters/setters
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}