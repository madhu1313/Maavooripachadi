package com.maavooripachadi.payments;

import com.maavooripachadi.payments.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@Validated
public class PaymentsController {

    private final PaymentsService service;

    public PaymentsController(PaymentsService service){ this.service = service; }

    @PostMapping("/intent")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateIntentResponse intent(@Valid @RequestBody CreateIntentRequest req){
        return service.createIntent(req);
    }

    @PostMapping("/capture")
    public com.maavooripachadi.payments.gateway.PaymentAttempt capture(@Valid @RequestBody CaptureRequest req){
        return service.capture(req);
    }

    @PostMapping("/refund")
    public com.maavooripachadi.payments.gateway.PaymentRefund refund(@Valid @RequestBody RefundRequest req){
        return service.refund(req);
    }

}
