package com.maavooripachadi.shipping.dto;

import com.maavooripachadi.shipping.CarrierCode;
import com.maavooripachadi.shipping.ServiceLevel;

public class RateQuoteResponse {
    private CarrierCode carrier; private ServiceLevel serviceLevel; private int amountPaise; private String currency = "INR";
    public CarrierCode getCarrier() { return carrier; }
    public void setCarrier(CarrierCode carrier) { this.carrier = carrier; }
    public ServiceLevel getServiceLevel() { return serviceLevel; }
    public void setServiceLevel(ServiceLevel serviceLevel) { this.serviceLevel = serviceLevel; }
    public int getAmountPaise() { return amountPaise; }
    public void setAmountPaise(int amountPaise) { this.amountPaise = amountPaise; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
