package com.maavooripachadi.payments;

import com.maavooripachadi.payments.gateway.AttemptStatus;
import com.maavooripachadi.payments.gateway.PaymentAttemptRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks/payments")
public class PaymentsWebhookController {

    private final PaymentAttemptRepository attempts;

    public PaymentsWebhookController(PaymentAttemptRepository attempts){ this.attempts = attempts; }

    @PostMapping("/{gateway}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String,Object> webhook(@PathVariable String gateway, @RequestBody Map<String,Object> payload){
        String gwOrderId = String.valueOf(payload.getOrDefault("order_id", ""));
        String paymentId = String.valueOf(payload.getOrDefault("payment_id", ""));
        attempts.findByGatewayOrderId(gwOrderId).ifPresent(a -> {
            a.setGatewayPaymentId(paymentId);
            a.setStatus(AttemptStatus.CAPTURED);
            attempts.save(a);
        });
        return Map.of("ok", true);
    }
}
