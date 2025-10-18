package com.maavooripachadi.order;
import org.springframework.web.bind.annotation.*; import lombok.RequiredArgsConstructor; import org.springframework.http.*; import java.util.*;
@RestController @RequestMapping("/api/v1/orders") @RequiredArgsConstructor
public class OrderController {
  private final OrderRepository orders; private final InvoiceService invoices;
  @GetMapping("/{orderNo}/invoice.pdf") public ResponseEntity<byte[]> pdf(@PathVariable String orderNo){ var pdf=invoices.render(orderNo); return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdf); }
}
