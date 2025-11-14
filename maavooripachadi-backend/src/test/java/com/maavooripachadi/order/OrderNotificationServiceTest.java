package com.maavooripachadi.order;

import com.maavooripachadi.engage.WhatsappClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderNotificationServiceTest {

    private JavaMailSender mailSender;
    private WhatsappClient whatsappClient;
    private OrderNotificationProperties properties;
    private OrderNotificationService service;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        whatsappClient = mock(WhatsappClient.class);
        properties = new OrderNotificationProperties();
        properties.setOwnerEmails(List.of("ops@example.com"));
        properties.setOwnerWhatsappNumbers(List.of("919876543210"));
        properties.setWhatsappEnabled(true);
        properties.setEmailEnabled(true);
        when(whatsappClient.canSend()).thenReturn(true);

        service = new OrderNotificationService(mailSender, properties, whatsappClient);
    }

    @Test
    void notifiesCustomerAndOwnerAcrossChannels() {
        Order order = sampleOrder();

        service.notifyOrderPlaced(order);

        verify(mailSender, times(2)).send(any(SimpleMailMessage.class));
        verify(whatsappClient, times(2)).sendText(anyString(), contains(order.getOrderNo()));
    }

    @Test
    void skipsWhatsappWhenGatewayDisabled() {
        when(whatsappClient.canSend()).thenReturn(false);

        service.notifyOrderPlaced(sampleOrder());

        verify(whatsappClient, never()).sendText(anyString(), anyString());
    }

    private Order sampleOrder() {
        Order order = new Order();
        order.setOrderNo("ORD-1");
        order.setCustomerName("Alice");
        order.setCustomerEmail("alice@example.com");
        order.setCustomerPhone("9876543210");
        order.setStatus(OrderStatus.PENDING);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setSubtotalPaise(15000);
        order.setShippingPaise(2000);
        order.setTaxPaise(900);
        order.setDiscountPaise(500);
        order.setTotalPaise(17600);
        order.setCurrency("INR");
        order.setNotes("Leave at door");

        OrderAddress address = new OrderAddress();
        address.setName("Alice");
        address.setLine1("12 Jubilee Hills");
        address.setLine2("Road 2");
        address.setCity("Hyderabad");
        address.setState("TS");
        address.setPincode("500033");
        address.setCountry("IN");
        address.setPhone("9876543210");
        order.setShipTo(address);

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setTitle("Gongura Pickle");
        item.setSku("SKU-1");
        item.setQty(1);
        item.setUnitPricePaise(15000);
        item.setLineTotalPaise(15000);
        order.getItems().add(item);

        return order;
    }
}
