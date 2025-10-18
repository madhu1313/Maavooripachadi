package com.maavooripachadi.returns;


import org.springframework.stereotype.Component;


@Component
public class InventoryStub implements InventoryPort {
    @Override public void incrementOnHand(Long variantId, int qty){ /* TODO: call inventory service */ }
}