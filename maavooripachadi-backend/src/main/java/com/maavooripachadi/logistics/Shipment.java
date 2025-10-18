package com.maavooripachadi.logistics;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity(name = "LogisticsShipment")
@Table(name = "log_shipment")
public class Shipment extends BaseEntity {
    @Column(nullable = false)
    private String orderNo;


    @Column(unique = true, nullable = false)
    private String shipmentNo; // generated SN


    @ManyToOne
    private Warehouse warehouse;


    private String carrier; // SHIPROCKET, DTDC, etc
    private String trackingNo;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status = ShipmentStatus.CREATED;


    private Integer weightGrams;
    private Integer lengthCm;
    private Integer widthCm;
private Integer heightCm;


private String labelUrl; // pre-signed S3 or external URL
private String manifestId;


private String consigneeName;
private String consigneePhone;
private String shipLine1;
private String shipLine2;
private String shipCity;
private String shipState;
private String shipPincode;
private String shipCountry;


// getters/setters
public String getOrderNo() { return orderNo; }
public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
public String getShipmentNo() { return shipmentNo; }
public void setShipmentNo(String shipmentNo) { this.shipmentNo = shipmentNo; }
public Warehouse getWarehouse() { return warehouse; }
public void setWarehouse(Warehouse warehouse) { this.warehouse = warehouse; }
public String getCarrier() { return carrier; }
public void setCarrier(String carrier) { this.carrier = carrier; }
public String getTrackingNo() { return trackingNo; }
public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }
public ShipmentStatus getStatus() { return status; }
public void setStatus(ShipmentStatus status) { this.status = status; }
public Integer getWeightGrams() { return weightGrams; }
public void setWeightGrams(Integer weightGrams) { this.weightGrams = weightGrams; }
public Integer getLengthCm() { return lengthCm; }
public void setLengthCm(Integer lengthCm) { this.lengthCm = lengthCm; }
public Integer getWidthCm() { return widthCm; }
public void setWidthCm(Integer widthCm) { this.widthCm = widthCm; }
public Integer getHeightCm() { return heightCm; }
public void setHeightCm(Integer heightCm) { this.heightCm = heightCm; }
public String getLabelUrl() { return labelUrl; }
public void setLabelUrl(String labelUrl) { this.labelUrl = labelUrl; }
public String getManifestId() { return manifestId; }
public void setManifestId(String manifestId) { this.manifestId = manifestId; }
public String getConsigneeName() { return consigneeName; }
public void setConsigneeName(String consigneeName) { this.consigneeName = consigneeName; }
public String getConsigneePhone() { return consigneePhone; }
public void setConsigneePhone(String consigneePhone) { this.consigneePhone = consigneePhone; }
public String getShipLine1() { return shipLine1; }
public void setShipLine1(String shipLine1) { this.shipLine1 = shipLine1; }
public String getShipLine2() { return shipLine2; }
public void setShipLine2(String shipLine2) { this.shipLine2 = shipLine2; }
public String getShipCity() { return shipCity; }
public void setShipCity(String shipCity) { this.shipCity = shipCity; }
public String getShipState() { return shipState; }
public void setShipState(String shipState) { this.shipState = shipState; }
public String getShipPincode() { return shipPincode; }
public void setShipPincode(String shipPincode) { this.shipPincode = shipPincode; }
public String getShipCountry() { return shipCountry; }
public void setShipCountry(String shipCountry) { this.shipCountry = shipCountry; }
}
