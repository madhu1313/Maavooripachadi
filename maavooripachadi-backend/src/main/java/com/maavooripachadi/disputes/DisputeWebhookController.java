package com.maavooripachadi.disputes;


import com.maavooripachadi.disputes.dto.CreateDisputeRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/webhooks/payments")
public class DisputeWebhookController {
    private final DisputeService service;
    public DisputeWebhookController(DisputeService service){ this.service = service; }


    /** Generic webhook: `/api/v1/webhooks/payments/{gateway}/disputes` */
    @PostMapping("/{gateway}/disputes")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, Object> ingest(@PathVariable String gateway, @RequestBody Map<String, Object> payload, HttpServletRequest req){
// TODO: verify signature header per gateway
        var providerCaseId = String.valueOf(payload.getOrDefault("case_id", payload.getOrDefault("id", "")));
        var orderNo = (String) payload.getOrDefault("order_no", null);
        var amountPaise = ((Number) payload.getOrDefault("amount_paise", 0)).intValue();
        var reason = (String) payload.getOrDefault("reason", "other");
        var type = (String) payload.getOrDefault("type", "chargeback");


        var reqDto = new CreateDisputeRequest(gateway.toUpperCase(), providerCaseId, orderNo, null, reason, type, amountPaise, "INR");
        var d = service.create(reqDto);
        return Map.of("ok", true, "disputeId", d.getId());
    }
}