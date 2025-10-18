package com.maavooripachadi.returns;


import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.*;


@Entity
@Table(name = "return_event", indexes = @Index(name = "ix_return_event_req", columnList = "request_id"))
public class ReturnEvent extends BaseEntity {


    @ManyToOne(optional = false)
    @JoinColumn(name = "request_id")
    private ReturnRequest request;


    @Column(nullable = false)
    private String kind; // OPENED, APPROVED, REJECTED, RECEIVED, REFUNDED, EXCHANGED, CLOSED


    @Lob
    private String payloadJson;


    // getters & setters
    public ReturnRequest getRequest() { return request; }
    public void setRequest(ReturnRequest request) { this.request = request; }
    public String getKind() { return kind; }
    public void setKind(String kind) { this.kind = kind; }
    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }
}