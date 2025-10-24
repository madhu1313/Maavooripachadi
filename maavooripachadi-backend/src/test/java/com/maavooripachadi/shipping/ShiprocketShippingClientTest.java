package com.maavooripachadi.shipping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ShiprocketShippingClientTest {

  private ShiprocketShippingClient client;

  @BeforeEach
  void setUp() {
    client = new ShiprocketShippingClient();
  }

  @Test
  void createLabelGeneratesAwbAndLabelUrl() {
    Shipment shipment = new Shipment();
    shipment.setOrderNo("MP-2001");

    ShiprocketShippingClient.LabelResult result = client.createLabel(shipment);

    assertThat(result.awb).isEqualTo("AWBMP-2001");
    assertThat(result.labelUrl).isEqualTo("https://example.com/label/MP-2001.pdf");
  }

  @Test
  void cancelAlwaysReturnsTrue() {
    assertThat(client.cancel("AWB123")).isTrue();
  }
}
