package com.maavooripachadi.shipping;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "shipping")
public class ShippingProperties {
    private String defaultFromPincode = "500001"; // Hyderabad sample
    private CarrierCode defaultCarrier = CarrierCode.SHIPROCKET;

    public String getDefaultFromPincode() { return defaultFromPincode; }
    public void setDefaultFromPincode(String defaultFromPincode) { this.defaultFromPincode = defaultFromPincode; }
    public CarrierCode getDefaultCarrier() { return defaultCarrier; }
    public void setDefaultCarrier(CarrierCode defaultCarrier) { this.defaultCarrier = defaultCarrier; }
}
