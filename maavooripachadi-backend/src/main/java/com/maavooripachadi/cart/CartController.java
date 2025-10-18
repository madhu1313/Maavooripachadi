package com.maavooripachadi.cart;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Validated
public class CartController {
  private final CartService svc;


  @PostMapping("/add")
  @ResponseStatus(HttpStatus.CREATED)
  public CartResponse add(@Valid @RequestBody AddToCartRequest req){
    var cart = svc.add(req.sessionId(), req.variantId(), req.qty(), req.unitPricePaise());
    return CartResponse.from(cart);
  }


  @PostMapping("/remove")
  public CartResponse remove(@Valid @RequestBody RemoveFromCartRequest req){
    var cart = svc.remove(req.sessionId(), req.variantId());
    return CartResponse.from(cart);
  }


  @PostMapping("/clear")
  public CartResponse clear(@RequestParam("sessionId") @NotBlank String sessionId){
    return CartResponse.from(svc.clear(sessionId));
  }


  @GetMapping("/view")
  public CartResponse view(@RequestParam("sessionId") @NotBlank String sessionId){
    return CartResponse.from(svc.view(sessionId));
  }
}
