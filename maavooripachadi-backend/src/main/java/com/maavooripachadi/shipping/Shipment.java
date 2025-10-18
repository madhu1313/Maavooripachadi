package com.maavooripachadi.shipping;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;

@Entity(name = "ShippingShipment")
@Table(name = "shipment", indexes = {@Index(name="ix_ship_order", columnList = "order_no", unique = true)})
public class Shipment extends BaseEntity {
    @Column(name = "order_no", nullable = false)
    private String orderNo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status = ShipmentStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    private CarrierCode carrier; // chosen carrier

    @Enumerated(EnumType.STRING)
    private ServiceLevel serviceLevel = ServiceLevel.STANDARD;

    private String trackingNo;

    @Enumerated(EnumType.STRING)
    private LabelStatus labelStatus = LabelStatus.NONE;

    @Lob
    private String labelUrl; // PDF/PNG label

    private int weightGrams;
    private int lengthCm;
    private int widthCm;
    private int heightCm;

    private String fromPincode; private String toPincode;
    private String toName; private String toPhone; private String toAddress1; private String toAddress2; private String toCity; private String toState;

    private Integer codPaise; // null for prepaid

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }
    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }
    public CarrierCode getCarrier() { return carrier; }
    public void setCarrier(CarrierCode carrier) { this.carrier = carrier; }
    public ServiceLevel getServiceLevel() { return serviceLevel; }
    public void setServiceLevel(ServiceLevel serviceLevel) { this.serviceLevel = serviceLevel; }
    public String getTrackingNo() { return trackingNo; }
    public void setTrackingNo(String trackingNo) { this.trackingNo = trackingNo; }
    public LabelStatus getLabelStatus() { return labelStatus; }
    public void setLabelStatus(LabelStatus labelStatus) { this.labelStatus = labelStatus; }
    public String getLabelUrl() { return labelUrl; }
    public void setLabelUrl(String labelUrl) { this.labelUrl = labelUrl; }
    public int getWeightGrams() { return weightGrams; }
    public void setWeightGrams(int weightGrams) { this.weightGrams = weightGrams; }
    public int getLengthCm() { return lengthCm; }
    public void setLengthCm(int lengthCm) { this.lengthCm = lengthCm; }
    public int getWidthCm() { return widthCm; }
    public void setWidthCm(int widthCm) { this.widthCm = widthCm; }
    public int getHeightCm() { return heightCm; }
    public void setHeightCm(int heightCm) { this.heightCm = heightCm; }
    public String getFromPincode() { return fromPincode; }
    public void setFromPincode(String fromPincode) { this.fromPincode = fromPincode; }
    public String getToPincode() { return toPincode; }
    public void setToPincode(String toPincode) { this.toPincode = toPincode; }
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
    public Integer getCodPaise() { return codPaise; }
    public void setCodPaise(Integer codPaise) { this.codPaise = codPaise; }
}
