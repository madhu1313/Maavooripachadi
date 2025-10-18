package com.maavooripachadi.shipping.dto;

import com.maavooripachadi.shipping.ServiceLevel;

public class CreateShipmentRequest {
    private String orderNo; private String toName; private String toPhone; private String toAddress1; private String toAddress2; private String toCity; private String toState; private String toPincode; private String fromPincode; private int weightGrams; private int lengthCm; private int widthCm; private int heightCm; private ServiceLevel serviceLevel = ServiceLevel.STANDARD; private Integer codPaise;
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public String getToName() { return toName; }
    public void setToName(String toName) { this.toName = toName; }
    public String getToPhone() { return toPhone; }
    public void setToPhone(String toPhone) { this.toPhone = toPhone; }
    public String getToAddress1() { return toAddress1; }
    public void setToAddress1(String toAddress1) { this.toAddress1 = toAddress1; }
    public String getToAddress2() { return toAddress2; }
    public void setToAddress2(String toAddress2) { this.toAddress2 = toAddress2; }
    public String getToCity() { return toCity; }
    public void setToCity(String toCity) { this.toCity = toCity; }
    public String getToState() { return toState; }
    public void setToState(String toState) { this.toState = toState; }
    public String getToPincode() { return toPincode; }
    public void setToPincode(String toPincode) { this.toPincode = toPincode; }
    public String getFromPincode() { return fromPincode; }
    public void setFromPincode(String fromPincode) { this.fromPincode = fromPincode; }
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
    public Integer getCodPaise() { return codPaise; }
    public void setCodPaise(Integer codPaise) { this.codPaise = codPaise; }
}
