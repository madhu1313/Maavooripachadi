package com.maavooripachadi.returns;


import com.maavooripachadi.returns.dto.*;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/returns")
public class ReturnsAdminController {
    private final ReturnsService service;
    public ReturnsAdminController(ReturnsService service){ this.service = service; }


    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('RETURNS_WRITE') or hasRole('ADMIN')")
    public ReturnRequest approve(@PathVariable Long id, @RequestBody @Valid ApproveReturnRequest body){ return service.approve(id, body); }


    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('RETURNS_WRITE') or hasRole('ADMIN')")
    public ReturnRequest reject(@PathVariable Long id, @RequestParam(value = "note", required=false) String note){ return service.reject(id, note); }


    @PostMapping("/receive")
    @PreAuthorize("hasAuthority('RETURNS_WRITE') or hasRole('ADMIN')")
    public ReturnItem receive(@RequestBody @Valid ReceiveItemsRequest body){ return service.receive(body); }


    @PostMapping("/{id}/refund")
    @PreAuthorize("hasAuthority('RETURNS_WRITE') or hasRole('ADMIN')")
    public ReturnRequest refund(@PathVariable Long id, @RequestBody @Valid RefundDecisionRequest body){ return service.refund(id, body); }


    @PostMapping("/{id}/exchange")
    @PreAuthorize("hasAuthority('RETURNS_WRITE') or hasRole('ADMIN')")
    public ReturnRequest exchange(@PathVariable Long id, @RequestBody @Valid ExchangeDecisionRequest body){ return service.exchange(id, body); }
}
