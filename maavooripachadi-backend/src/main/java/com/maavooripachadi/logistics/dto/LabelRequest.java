package com.maavooripachadi.logistics.dto;


import jakarta.validation.constraints.NotBlank;


public class LabelRequest {
    @NotBlank private String shipmentNo;
    private String carrier = "SHIPROCKET";


    // getters/setters
    public String getShipmentNo() { return shipmentNo; }
    public void setShipmentNo(String shipmentNo) { this.shipmentNo = shipmentNo; }
    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }
}