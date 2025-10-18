package com.maavooripachadi.shipping;
import org.springframework.web.bind.annotation.*; import lombok.RequiredArgsConstructor; import java.util.*;
@RestController @RequestMapping("/api/v1/shipping") @RequiredArgsConstructor
public class ShippingController {
  private final PincodeService svc;
  @GetMapping("/pincode/{pin}") public Map<String,Object> check(@PathVariable String pin){ return Map.of("pincode", pin, "serviceable", svc.serviceable(pin)); }
}
