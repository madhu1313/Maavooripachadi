package com.maavooripachadi.payments.gateway;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class GatewayRouter {

    private final PaymentRouterService routerService;

    public GatewayRouter(PaymentRouterService routerService) {
        this.routerService = routerService;
    }

    public PaymentGateway choose(String hint) {
        GatewayName gateway = Arrays.stream(GatewayName.values())
                .filter(value -> value.name().equalsIgnoreCase(hint))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown gateway: " + hint));
        return routerService.pick(gateway);
    }
}
