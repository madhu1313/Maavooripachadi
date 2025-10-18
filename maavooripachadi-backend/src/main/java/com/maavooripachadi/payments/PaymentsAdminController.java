package com.maavooripachadi.payments;

import com.maavooripachadi.payments.dto.RefundRequest;
import com.maavooripachadi.payments.gateway.PaymentAttempt;
import com.maavooripachadi.payments.gateway.PaymentAttemptRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/admin/payments")
@Validated
public class PaymentsAdminController {

  private final PaymentsService service;
  private final PaymentAttemptRepository attempts;

  public PaymentsAdminController(PaymentsService service, PaymentAttemptRepository attempts){
    this.service = service; this.attempts = attempts;
  }

  @GetMapping("/attempts/{orderNo}")
  @PreAuthorize("hasAuthority('PAYMENT_READ') or hasRole('ADMIN')")
  public Optional<PaymentAttempt> get(@PathVariable String orderNo){
    return attempts.findFirstByOrderNoOrderByCreatedAtDesc(orderNo);
  }

  @PostMapping("/refund")
  @PreAuthorize("hasAuthority('PAYMENT_WRITE') or hasRole('ADMIN')")
  public com.maavooripachadi.payments.gateway.PaymentRefund refund(@RequestBody @Validated RefundRequest req){
    return service.refund(req);
  }
}
