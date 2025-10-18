package com.maavooripachadi.order;


import com.maavooripachadi.order.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/checkout")
@Validated
public class CheckoutController {
    private final OrderService orders;
    public CheckoutController(OrderService orders){ this.orders = orders; }


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout(@Valid @RequestBody CheckoutRequest req){
        return OrderResponse.from(orders.checkout(req));
    }


    @GetMapping("/{orderNo}")
    public OrderResponse get(@PathVariable String orderNo){ return OrderResponse.from(orders.getByOrderNo(orderNo)); }
}