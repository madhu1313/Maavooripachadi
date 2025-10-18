package com.maavooripachadi.disputes;


import com.maavooripachadi.disputes.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin/disputes")
@Validated
public class DisputeAdminController {
  private final DisputeService service;


  public DisputeAdminController(DisputeService service){ this.service = service; }


  @PostMapping
  @PreAuthorize("hasAuthority('PAYMENT_DISPUTE_WRITE') or hasRole('ADMIN')")
  public DisputeResponse create(@Valid @RequestBody CreateDisputeRequest req){
    return DisputeResponse.from(service.create(req));
  }


  @PostMapping("/{id}/status")
  @PreAuthorize("hasAuthority('PAYMENT_DISPUTE_WRITE') or hasRole('ADMIN')")
  public DisputeResponse updateStatus(@PathVariable long id, @Valid @RequestBody UpdateDisputeStatusRequest req){
    return DisputeResponse.from(service.updateStatus(id, req.status(), req.note()));
  }


  @PostMapping("/{id}/note")
  @PreAuthorize("hasAuthority('PAYMENT_DISPUTE_WRITE') or hasRole('ADMIN')")
  public DisputeResponse addNote(@PathVariable long id, @Valid @RequestBody AddDisputeNoteRequest req){
    return DisputeResponse.from(service.addNote(id, req.note()));
  }


  @GetMapping
  @PreAuthorize("hasAuthority('PAYMENT_DISPUTE_READ') or hasRole('ADMIN')")
  public Page<DisputeResponse> list(@RequestParam(value = "gateway", required = false) String gateway,
                                    @RequestParam(value = "status", required = false) DisputeStatus status,
                                    @RequestParam(value = "q", required = false) String q,
                                    @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,
                                    @RequestParam(value = "size", defaultValue = "20") @Min(1) @Max(200) int size){
    return service.list(gateway == null ? null : gateway.toUpperCase(), status, q, page, size)
            .map(DisputeResponse::from);
  }


  @GetMapping("/{id}/timeline")
  @PreAuthorize("hasAuthority('PAYMENT_DISPUTE_READ') or hasRole('ADMIN')")
  public java.util.List<DisputeEventResponse> timeline(@PathVariable long id){
    return service.timeline(id).stream().map(DisputeEventResponse::from).toList();
  }
}
