package com.maavooripachadi.order;


import com.maavooripachadi.order.dto.CheckoutRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


@Service
public class OrderService {
    private final OrderRepository orders;
private final OrderItemRepository items;
private final OrderNumberService orderNos;
private final OrderPricingService pricing;


public OrderService(OrderRepository orders, OrderItemRepository items, OrderNumberService orderNos, OrderPricingService pricing){
    this.orders = orders; this.items = items; this.orderNos = orderNos; this.pricing = pricing;
}


/** Build an order from an existing Cart session (TODO: inject CartService and load items). */
@Transactional
public Order checkout(CheckoutRequest req){
    Order o = new Order();
    o.setOrderNo(orderNos.next());
    o.setStatus(OrderStatus.PENDING);
    o.setPaymentStatus(PaymentStatus.PENDING);
    o.setCustomerEmail(trimOrNull(req.getCustomerEmail()));
    o.setCustomerPhone(trimOrNull(req.getCustomerPhone()));
    o.setCustomerName(trimOrNull(req.getCustomerName()));
    OrderAddress addr = new OrderAddress();
    addr.setName(firstNonBlank(req.getShipName(), req.getCustomerName()));
    addr.setPhone(firstNonBlank(req.getShipPhone(), req.getCustomerPhone()));
    addr.setLine1(req.getShipLine1());
    addr.setLine2(trimOrNull(req.getShipLine2()));
    addr.setCity(req.getShipCity());
    addr.setState(req.getShipState());
    addr.setPincode(req.getShipPincode());
    addr.setCountry(normalizeCountry(req.getShipCountry()));
    o.setShipTo(addr);
    o.setCouponCode(trimOrNull(req.getCouponCode()));
    o.setNotes(trimOrNull(req.getNotes()));
    o.setPaymentGateway(normalizeGateway(req.getPaymentGateway()));


// TODO: Replace with items from CartService by sessionId.
// For skeleton, add a single demo item so totals compute.
    OrderItem it = new OrderItem();
    it.setOrder(o); it.setVariantId(1L); it.setSku("SKU1"); it.setTitle("Sample Pickle 500g"); it.setQty(1); it.setUnitPricePaise(19900); it.setLineTotalPaise(19900);
    o.getItems().add(it);


    int subtotal = 0; for (OrderItem x : o.getItems()) subtotal += x.getLineTotalPaise();
    int shipping = pricing.shippingPaiseForPincode(req.getShipPincode());
    int discount = pricing.discountPaise(req.getCouponCode(), subtotal);
    int tax = pricing.taxPaiseOnSubtotal(subtotal - discount);
    int total = subtotal + shipping + tax - discount;
    o.setSubtotalPaise(subtotal); o.setShippingPaise(shipping); o.setDiscountPaise(discount); o.setTaxPaise(tax); o.setTotalPaise(total);


    orders.saveAndFlush(o); // cascade saves items and flush to DB


// TODO: Reserve inventory via Logistics InventoryService
// TODO: Create payment intent via Payments module and set paymentRef
    return o;
}


@Transactional(readOnly = true)
public Order getByOrderNo(String orderNo){
    return orders.findByOrderNo(orderNo)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
}


@Transactional
public Order markPaid(String orderNo, String gateway, String paymentRef){
    Order o = getByOrderNo(orderNo);
    o.setPaymentGateway(gateway); o.setPaymentRef(paymentRef); o.setPaymentStatus(PaymentStatus.CAPTURED); o.setStatus(OrderStatus.PAID); o.setPaidAt(java.time.OffsetDateTime.now());
    return orders.save(o);
}


@Transactional
public Order cancel(String orderNo, String reason){
    Order o = getByOrderNo(orderNo);
    if (o.getStatus() == OrderStatus.CANCELLED || o.getStatus() == OrderStatus.REFUNDED) return o;
    if (o.getStatus() == OrderStatus.PAID) throw new IllegalStateException("Paid order requires refund flow");
    o.setStatus(OrderStatus.CANCELLED);
    orders.save(o);
// TODO: Unreserve inventory via Logistics
    return o;
}

private String firstNonBlank(String primary, String fallback){
    if (primary != null && !primary.trim().isEmpty()) {
        return primary.trim();
    }
    if (fallback != null && !fallback.trim().isEmpty()) {
        return fallback.trim();
    }
    return null;
}

private String trimOrNull(String value){
    if (value == null) {
        return null;
    }
    String trimmed = value.trim();
    return trimmed.isEmpty() ? null : trimmed;
}

private String normalizeCountry(String country){
    String value = trimOrNull(country);
    if (value == null) {
        return "IN";
    }
    if ("india".equalsIgnoreCase(value)) {
        return "IN";
    }
    return value.length() == 2 ? value.toUpperCase() : value;
}

private String normalizeGateway(String gateway){
    String value = trimOrNull(gateway);
    return value == null ? null : value.toUpperCase();
}
}
