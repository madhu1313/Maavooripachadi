package com.maavooripachadi.metrics;
import io.micrometer.core.instrument.*;
import org.springframework.stereotype.Component;
@Component public class Metrics
{ private final Counter checkoutStart, paymentSuccess, webhookOk;
    public Metrics(MeterRegistry r){ checkoutStart=Counter.builder("checkout.start").register(r);
        paymentSuccess=Counter.builder("payment.success").register(r);
        webhookOk=Counter.builder("webhook.ok").register(r);
    } public void incCheckout(){checkoutStart.increment();} public void incPaid(){paymentSuccess.increment();} public void incWebhook(){webhookOk.increment();} }
