package com.maavooripachadi.reviews.stubs;


import com.maavooripachadi.reviews.OrderPort;
import org.springframework.stereotype.Component;


@Component
public class OrderStub implements OrderPort {
    @Override public boolean hasPurchased(String subjectId, Long productId, Long variantId){ return true; }
}