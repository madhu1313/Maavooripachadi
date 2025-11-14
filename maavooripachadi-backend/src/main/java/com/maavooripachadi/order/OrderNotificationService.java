package com.maavooripachadi.order;

import com.maavooripachadi.engage.WhatsappClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class OrderNotificationService {

    private static final Logger log = LoggerFactory.getLogger(OrderNotificationService.class);
    private static final Locale INR_LOCALE = new Locale("en", "IN");

    private final JavaMailSender mailSender;
    private final OrderNotificationProperties properties;
    private final WhatsappClient whatsappClient;

    public OrderNotificationService(JavaMailSender mailSender,
                                    OrderNotificationProperties properties,
                                    WhatsappClient whatsappClient) {
        this.mailSender = mailSender;
        this.properties = properties;
        this.whatsappClient = whatsappClient;
    }

    public void notifyOrderPlaced(Order order) {
        if (order == null) {
            return;
        }
        if (properties.isEmailEnabled()) {
            sendCustomerEmail(order);
            sendOwnerEmails(order);
        }
        if (properties.isWhatsappEnabled()) {
            sendCustomerWhatsapp(order);
            sendOwnerWhatsapp(order);
        }
    }

    private void sendCustomerEmail(Order order) {
        if (!hasText(order.getCustomerEmail())) {
            return;
        }
        sendEmail(order, order.getCustomerEmail(), Audience.CUSTOMER);
    }

    private void sendOwnerEmails(Order order) {
        List<String> targets = properties.getOwnerEmails();
        if (targets.isEmpty()) {
            return;
        }
        for (String email : targets) {
            sendEmail(order, email, Audience.OWNER);
        }
    }

    private void sendEmail(Order order, String to, Audience audience) {
        SimpleMailMessage message = new SimpleMailMessage();
        if (hasText(properties.getFromEmail())) {
            message.setFrom(properties.getFromEmail());
        }
        message.setTo(to);
        message.setSubject(buildSubject(order, audience));
        message.setText(buildBody(order, audience));
        try {
            mailSender.send(message);
            log.info("Order {} notification emailed to {} ({})", order.getOrderNo(), to, audience);
        } catch (MailException ex) {
            log.warn("Failed to send order {} email to {}: {}", order.getOrderNo(), to, ex.getMessage());
        }
    }

    private void sendCustomerWhatsapp(Order order) {
        if (!hasText(order.getCustomerPhone()) || !whatsappClient.canSend()) {
            return;
        }
        sendWhatsapp(order, order.getCustomerPhone(), Audience.CUSTOMER);
    }

    private void sendOwnerWhatsapp(Order order) {
        if (!whatsappClient.canSend()) {
            return;
        }
        List<String> targets = properties.getOwnerWhatsappNumbers();
        for (String phone : targets) {
            sendWhatsapp(order, phone, Audience.OWNER);
        }
    }

    private void sendWhatsapp(Order order, String phone, Audience audience) {
        try {
            whatsappClient.sendText(phone, buildBody(order, audience));
            log.info("Order {} notification sent via WhatsApp to {} ({})", order.getOrderNo(), phone, audience);
        } catch (Exception ex) {
            log.warn("Failed to send WhatsApp notification for order {} to {}: {}", order.getOrderNo(), phone, ex.getMessage());
        }
    }

    private String buildSubject(Order order, Audience audience) {
        return audience == Audience.CUSTOMER
                ? "We received order " + safe(order.getOrderNo())
                : "New order " + safe(order.getOrderNo()) + " to pack";
    }

    private String buildBody(Order order, Audience audience) {
        StringBuilder sb = new StringBuilder();
        if (audience == Audience.CUSTOMER) {
            sb.append("Hi ").append(firstNonBlank(order.getCustomerName(), "there")).append(",\n");
            sb.append("Thank you for ordering from Maavoori Pachadi. Here are your order details:\n\n");
        } else {
            sb.append(properties.getOpsDisplayName()).append(",\n");
            sb.append("New order to prep and pack:\n\n");
        }
        appendOrderSnapshot(order, sb);
        sb.append("\n");
        if (audience == Audience.CUSTOMER) {
            sb.append("We will notify you when the parcel is packed and handed to the courier.");
            if (hasText(properties.getSupportWhatsappLink())) {
                sb.append(" Need help? ").append(properties.getSupportWhatsappLink());
            }
        } else {
            sb.append("Please stage this order for dispatch once ready.");
        }
        return sb.toString();
    }

    private void appendOrderSnapshot(Order order, StringBuilder sb) {
        sb.append("Order #: ").append(safe(order.getOrderNo())).append("\n");
        sb.append("Status: ").append(order.getStatus()).append(" / ").append(order.getPaymentStatus()).append("\n");
        sb.append("Total: ").append(formatMoney(order.getTotalPaise())).append(" ").append(safe(order.getCurrency())).append("\n\n");

        sb.append("Items:\n");
        if (order.getItems().isEmpty()) {
            sb.append(" - Items will be added from cart.\n");
        } else {
            order.getItems().forEach(item -> sb.append(" - ")
                    .append(safe(firstNonBlank(item.getTitle(), item.getSku())))
                    .append(" x").append(item.getQty())
                    .append(" = ").append(formatMoney(item.getLineTotalPaise()))
                    .append("\n"));
        }
        sb.append("\nCharges:\n");
        sb.append(" - Subtotal: ").append(formatMoney(order.getSubtotalPaise())).append("\n");
        sb.append(" - Shipping: ").append(formatMoney(order.getShippingPaise())).append("\n");
        sb.append(" - Tax: ").append(formatMoney(order.getTaxPaise())).append("\n");
        if (order.getDiscountPaise() > 0) {
            sb.append(" - Discount: -").append(formatMoney(order.getDiscountPaise())).append("\n");
        }
        sb.append(" - Payable: ").append(formatMoney(order.getTotalPaise())).append(" ").append(safe(order.getCurrency())).append("\n\n");

        sb.append("Ship to:\n");
        formatAddress(order.getShipTo()).forEach(line -> sb.append(" - ").append(line).append("\n"));
        if (hasText(order.getNotes())) {
            sb.append("\nCustomer note: ").append(order.getNotes()).append("\n");
        }
    }

    private List<String> formatAddress(OrderAddress address) {
        List<String> lines = new ArrayList<>();
        if (address == null) {
            lines.add("Not provided");
            return lines;
        }
        if (hasText(address.getName())) lines.add(address.getName());
        if (hasText(address.getLine1())) lines.add(address.getLine1());
        if (hasText(address.getLine2())) lines.add(address.getLine2());

        StringBuilder cityLine = new StringBuilder();
        if (hasText(address.getCity())) cityLine.append(address.getCity());
        if (hasText(address.getState())) {
            if (cityLine.length() > 0) cityLine.append(", ");
            cityLine.append(address.getState());
        }
        if (hasText(address.getPincode())) {
            if (cityLine.length() > 0) cityLine.append(" - ");
            cityLine.append(address.getPincode());
        }
        if (cityLine.length() > 0) {
            lines.add(cityLine.toString());
        }

        if (hasText(address.getCountry())) {
            lines.add(address.getCountry());
        }
        if (hasText(address.getPhone())) {
            lines.add("Phone: " + address.getPhone());
        }
        return lines.isEmpty() ? List.of("Not provided") : lines;
    }

    private String formatMoney(int paise) {
        BigDecimal rupees = BigDecimal.valueOf(paise, 2);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(INR_LOCALE);
        return formatter.format(rupees);
    }

    private String firstNonBlank(String primary, String fallback) {
        if (hasText(primary)) {
            return primary.trim();
        }
        return hasText(fallback) ? fallback.trim() : "";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private enum Audience { CUSTOMER, OWNER }
}
