package com.maavooripachadi.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    private OrderRepository orderRepository;
    private InvoiceService invoiceService;
    private OrderController controller;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        invoiceService = mock(InvoiceService.class);
        controller = new OrderController(orderRepository, invoiceService);
    }

    @Test
    void pdfReturnsInvoiceBytesFromService() {
        byte[] pdf = new byte[]{1, 2, 3};
        when(invoiceService.render("ORD-100")).thenReturn(pdf);

        ResponseEntity<byte[]> response = controller.pdf("ORD-100");

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_PDF);
        assertThat(response.getBody()).isEqualTo(pdf);
        verify(invoiceService).render("ORD-100");
    }
}
