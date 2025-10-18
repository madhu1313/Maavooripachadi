package com.maavooripachadi.compliance;

import com.maavooripachadi.common.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "einvoice_meta")
public class EinvoiceMeta extends BaseEntity {
  private String orderNo;
  private String irn; // Invoice Reference Number
  private String ackNo; // acknowledgement number
  private OffsetDateTime ackDt;

  @Lob
  private String signedQr; // base64 image/svg or QR payload

  @Lob
  private String payloadJson; // request/response archival

  public EinvoiceMeta() {
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getIrn() {
    return irn;
  }

  public void setIrn(String irn) {
    this.irn = irn;
  }

  public String getAckNo() {
    return ackNo;
  }

  public void setAckNo(String ackNo) {
    this.ackNo = ackNo;
  }

  public OffsetDateTime getAckDt() {
    return ackDt;
  }

  public void setAckDt(OffsetDateTime ackDt) {
    this.ackDt = ackDt;
  }

  public String getSignedQr() {
    return signedQr;
  }

  public void setSignedQr(String signedQr) {
    this.signedQr = signedQr;
  }

  public String getPayloadJson() {
    return payloadJson;
  }

  public void setPayloadJson(String payloadJson) {
    this.payloadJson = payloadJson;
  }
}
