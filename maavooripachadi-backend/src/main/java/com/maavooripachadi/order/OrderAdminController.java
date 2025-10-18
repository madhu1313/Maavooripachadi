package com.maavooripachadi.order;


import com.maavooripachadi.order.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page; import org.springframework.data.domain.PageRequest; import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/orders")
@Validated
public class OrderAdminController {
    private final OrderRepository orders; private final OrderService svc; private final OrderNoteService notes;
    public OrderAdminController(OrderRepository orders, OrderService svc, OrderNoteService notes){ this.orders = orders; this.svc = svc; this.notes = notes; }


    @GetMapping
    @PreAuthorize("hasAuthority('ORDER_READ') or hasRole('ADMIN')")
    public Page<Order> list(@RequestParam(value = "status", required=false) OrderStatus status,
                            @RequestParam(value = "page", defaultValue="0") @Min(0) int page,
                            @RequestParam(value = "size", defaultValue="50") @Min(1) @Max(200) int size){
        var pr = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return status == null ? orders.findAll(pr) : orders.findByStatus(status, pr);
    }


    @PostMapping("/mark-paid")
    @PreAuthorize("hasAuthority('ORDER_WRITE') or hasRole('ADMIN')")
    public Order markPaid(@Valid @RequestBody MarkPaidRequest req){ return svc.markPaid(req.getOrderNo(), req.getGateway(), req.getPaymentRef()); }


    @PostMapping("/cancel")
    @PreAuthorize("hasAuthority('ORDER_WRITE') or hasRole('ADMIN')")
    public Order cancel(@Valid @RequestBody CancelRequest req){ return svc.cancel(req.getOrderNo(), req.getReason()); }


    @PostMapping("/{orderNo}/notes")
    @PreAuthorize("hasAuthority('ORDER_WRITE') or hasRole('ADMIN')")
    public OrderNote addNote(@PathVariable String orderNo, @RequestParam(value = "note") @NotBlank String note, @RequestHeader(value="X-Actor", required=false) String actor){
        return notes.add(orderNo, note, actor == null ? "system" : actor);
    }


    @GetMapping("/{orderNo}/notes")
    @PreAuthorize("hasAuthority('ORDER_READ') or hasRole('ADMIN')")
    public java.util.List<OrderNote> listNotes(@PathVariable String orderNo){ return notes.list(orderNo); }
}
