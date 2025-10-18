package com.maavooripachadi.shipping;

import org.springframework.stereotype.Component;

/**
 * Lightweight adaptor for Shiprocket interactions within the shipping module.
 */
@Component
public class ShiprocketShippingClient {

  public static class LabelResult {
    public String awb;
    public String labelUrl;
  }

  public LabelResult createLabel(Shipment shipment) {
    LabelResult result = new LabelResult();
    result.awb = "AWB" + shipment.getOrderNo();
    result.labelUrl = "https://example.com/label/" + shipment.getOrderNo() + ".pdf";
    return result;
  }

  public boolean cancel(String awb) {
    return true;
  }
}
