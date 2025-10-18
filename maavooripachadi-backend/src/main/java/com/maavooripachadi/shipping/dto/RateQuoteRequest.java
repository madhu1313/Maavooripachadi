package com.maavooripachadi.shipping.dto;

import com.maavooripachadi.shipping.ServiceLevel;

public class RateQuoteRequest {
    private String fromPincode; private String toPincode; private int weightGrams; private int lengthCm; private int widthCm; private int heightCm; private ServiceLevel serviceLevel = ServiceLevel.STANDARD;
    public String getFromPincode() { return fromPincode; }
    public void setFromPincode(String fromPincode) { this.fromPincode = fromPincode; }
    public String getToPincode() { return toPincode; }
    public void setToPincode(String toPincode) { this.toPincode = toPincode; }
    public int getWeightGrams() { return weightGrams; }
    public void setWeightGrams(int weightGrams) { this.weightGrams = weightGrams; }
    public int getLengthCm() { return lengthCm; }
    public void setLengthCm(int lengthCm) { this.lengthCm = lengthCm; }
    public int getWidthCm() { return widthCm; }
    public void setWidthCm(int widthCm) { this.widthCm = widthCm; }
    public int getHeightCm() { return heightCm; }
    public void setHeightCm(int heightCm) { this.heightCm = heightCm; }
    public ServiceLevel getServiceLevel() { return serviceLevel; }
    public void setServiceLevel(ServiceLevel serviceLevel) { this.serviceLevel = serviceLevel; }
}
