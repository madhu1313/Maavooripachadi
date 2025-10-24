package com.maavooripachadi.shipping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ShippingControllerTest {

    private PincodeService pincodeService;
    private ShippingController controller;

    @BeforeEach
    void setUp() {
        pincodeService = mock(PincodeService.class);
        controller = new ShippingController(pincodeService);
    }

    @Test
    void checkReturnsServiceabilityFlag() {
        when(pincodeService.serviceable("500032")).thenReturn(true);

        Map<String, Object> response = controller.check("500032");

        assertThat(response)
            .containsEntry("pincode", "500032")
            .containsEntry("serviceable", true);
        verify(pincodeService).serviceable("500032");
    }
}
