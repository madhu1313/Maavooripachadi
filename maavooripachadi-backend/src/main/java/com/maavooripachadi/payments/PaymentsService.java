package com.maavooripachadi.payments;

import com.maavooripachadi.order.Order;
import com.maavooripachadi.order.OrderService;
import com.maavooripachadi.order.PaymentStatus;
import com.maavooripachadi.payments.dto.CaptureRequest;
import com.maavooripachadi.payments.dto.CreateIntentRequest;
import com.maavooripachadi.payments.dto.CreateIntentResponse;
import com.maavooripachadi.payments.dto.RecoveryIssueRequest;
import com.maavooripachadi.payments.dto.RecoveryIssueResponse;
import com.maavooripachadi.payments.dto.RecoveryValidateResponse;
import com.maavooripachadi.payments.dto.RefundRequest;
import com.maavooripachadi.payments.gateway.AttemptStatus;
import com.maavooripachadi.payments.gateway.GatewayName;
import com.maavooripachadi.payments.gateway.PaymentGateway;
import com.maavooripachadi.payments.gateway.PaymentRefund;
import com.maavooripachadi.payments.gateway.PaymentRefundRepository;
import com.maavooripachadi.payments.gateway.PaymentRouterService;
import com.maavooripachadi.payments.gateway.RefundStatus;
import com.maavooripachadi.payments.recovery.RecoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Orchestrates payment intents, capture, refunds, and recovery links.
 */
@Service
public class PaymentsService {
    private static final Logger log = LoggerFactory.getLogger(PaymentsService.class);

    private final com.maavooripachadi.payments.gateway.PaymentAttemptRepository attempts;
    private final PaymentRefundRepository refunds;
    private final PaymentRouterService router;
    private final RecoveryService recoveryService;
    private final OrderService orders;

    public PaymentsService(com.maavooripachadi.payments.gateway.PaymentAttemptRepository attempts,
                           PaymentRefundRepository refunds,
                           PaymentRouterService router,
                           RecoveryService recoveryService,
                           OrderService orders){
        this.attempts = attempts;
        this.refunds = refunds;
        this.router = router;
        this.recoveryService = recoveryService;
        this.orders = orders;
    }

    @Transactional
    public CreateIntentResponse createIntent(CreateIntentRequest req){
        var order = findOrderWithRetry(req.getOrderNo());
        if (order.isPresent() && order.get().getPaymentStatus() == PaymentStatus.CAPTURED) {
            throw new IllegalStateException("Order already paid");
        }

        var attempt = new com.maavooripachadi.payments.gateway.PaymentAttempt();
        attempt.setOrderNo(req.getOrderNo());
        GatewayName gateway = Optional.ofNullable(req.getGateway()).orElse(GatewayName.RAZORPAY);
        attempt.setGateway(gateway);

        if (order.isPresent()) {
            attempt.setAmountPaise(order.get().getTotalPaise());
            attempt.setCurrency(Optional.ofNullable(order.get().getCurrency()).orElse(req.getCurrency()));
        } else {
            log.warn("Payment intent requested before order {} became visible. Using client-supplied amount.", req.getOrderNo());
            attempt.setAmountPaise(req.getAmountPaise());
            attempt.setCurrency(Optional.ofNullable(req.getCurrency()).orElse("INR"));
        }
        if (attempt.getAmountPaise() <= 0) {
            throw new IllegalStateException("Invalid payment amount");
        }
        attempt.setStatus(AttemptStatus.CREATED);
        PaymentGateway gw = router.pick(gateway);
        String gwOrderId;
        try {
            gwOrderId = gw.createGatewayOrder(attempt);
        } catch (IllegalStateException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Unable to initiate payment with " + gateway.name() + ".", ex);
        }
        attempt.setGatewayOrderId(gwOrderId);
        attempts.save(attempt);

        CreateIntentResponse resp = new CreateIntentResponse();
        resp.setGatewayOrderId(gwOrderId);
        resp.setOrderNo(attempt.getOrderNo());
        resp.setAmountPaise(attempt.getAmountPaise());
        resp.setCurrency(attempt.getCurrency());
        resp.setGateway(attempt.getGateway().name());
        return resp;
    }

    @Transactional
    public com.maavooripachadi.payments.gateway.PaymentAttempt capture(CaptureRequest req){
        var attempt = attempts.findFirstByOrderNoOrderByCreatedAtDesc(req.getOrderNo()).orElseThrow();
        PaymentGateway gw = router.pick(attempt.getGateway());
        String payload = attempt.getGatewayOrderId() + "|" + req.getGatewayPaymentId();
        boolean ok = gw.verifySignature(payload, req.getGatewaySignature(), null);
        if (!ok) {
            throw new IllegalArgumentException("Signature verification failed");
        }
        attempt.setGatewayPaymentId(req.getGatewayPaymentId());
        attempt.setGatewaySignature(req.getGatewaySignature());
        attempt.setStatus(AttemptStatus.CAPTURED);
        var savedAttempt = attempts.save(attempt);
        try {
            orders.markPaid(savedAttempt.getOrderNo(), savedAttempt.getGateway().name(), savedAttempt.getGatewayPaymentId());
        } catch (NoSuchElementException ex) {
            log.warn("Captured payment for order {} but could not mark it paid: {}", savedAttempt.getOrderNo(), ex.getMessage());
        }
        return savedAttempt;
    }

    @Transactional
    public PaymentRefund refund(RefundRequest req){
        var attempt = attempts.findFirstByOrderNoOrderByCreatedAtDesc(req.getOrderNo()).orElseThrow();
        PaymentGateway gw = router.pick(attempt.getGateway());
        String rid = gw.refund(attempt.getGatewayPaymentId(), req.getAmountPaise(), req.getReason());
        PaymentRefund refund = new PaymentRefund();
        refund.setAttempt(attempt);
        refund.setAmountPaise(req.getAmountPaise());
        refund.setStatus(RefundStatus.PROCESSED);
        refund.setGatewayRefundId(rid);
        refund.setReason(req.getReason());
        return refunds.save(refund);
    }

    private Optional<Order> findOrderWithRetry(String orderNo) {
        final int maxAttempts = 5;
        final long backoffMillis = 50;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                return Optional.of(orders.getByOrderNo(orderNo));
            } catch (NoSuchElementException ex) {
                if (attempt == maxAttempts - 1) {
                    return Optional.empty();
                }
                try {
                    Thread.sleep(backoffMillis * (attempt + 1L));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return Optional.empty();
                }
            }
        }
        return Optional.empty();
    }

    // Recovery helpers (delegates to submodule service)
    public RecoveryIssueResponse issueRecovery(RecoveryIssueRequest req){
        var subReq = new com.maavooripachadi.payments.recovery.dto.RecoveryIssueRequest();
        subReq.setOrderNo(req.getOrderNo());
        var subResp = recoveryService.issue(subReq);
        RecoveryIssueResponse out = new RecoveryIssueResponse();
        out.setToken(subResp.getToken());
        out.setUrl(subResp.getUrl());
        return out;
    }

    public RecoveryValidateResponse validateRecovery(String token){
        var subResp = recoveryService.validate(token);
        RecoveryValidateResponse out = new RecoveryValidateResponse();
        out.setOk(subResp.isOk());
        out.setOrderNo(subResp.getOrderNo());
        return out;
    }

    public void consumeRecovery(String token){
        recoveryService.consume(token);
    }
}
