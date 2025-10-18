package com.maavooripachadi.compliance;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@Validated
public class ComplianceAdminController {


  private final EinvoiceService einv;
  private final EwaybillService ewb;
  private final AccountingSyncService acc;
  private final GstService gst;


  @PostMapping("/einvoice/{orderNo}")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('COMPLIANCE_WRITE') or hasRole('ADMIN')")
  public EinvoiceResponse generateEinvoice(@PathVariable @NotBlank String orderNo){
    var dto = einv.map(orderNo);
    var meta = einv.register(dto);
    return new EinvoiceResponse(meta.getIrn(), meta.getAckNo(), String.valueOf(meta.getAckDt()), meta.getSignedQr());
  }


  @PostMapping("/ewaybill/{orderNo}")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAuthority('COMPLIANCE_WRITE') or hasRole('ADMIN')")
  public EwaybillResponse createEwaybill(@PathVariable @NotBlank String orderNo,
                                         @RequestParam @NotBlank String vehicleNo,
                                         @RequestParam @Min(1) int distanceKm){
    var e = ewb.create(orderNo, vehicleNo, distanceKm);
    return new EwaybillResponse(e.getEwbNo(), String.valueOf(e.getValidUpto()));
  }


  @PostMapping("/accounting/queue")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PreAuthorize("hasAuthority('COMPLIANCE_WRITE') or hasRole('ADMIN')")
  public AccountingQueueResponse queueAccounting(@RequestParam @NotBlank String type,
                                                 @RequestParam @NotBlank String id){
    var a = acc.queue(type, id, "ZOHO");
    return new AccountingQueueResponse(true, a.getId());
  }


  @GetMapping("/gst/gstr1")
  @PreAuthorize("hasAuthority('COMPLIANCE_READ') or hasRole('ADMIN')")
  public java.util.Map<String, Object> gstr1(@RequestParam @NotBlank String period){
    return gst.gstr1(period);
  }


  // ---- DTOs ----
  public record EinvoiceResponse(String irn, String ackNo, String ackDt, String qr) {}
  public record EwaybillResponse(String ewbNo, String validUpto) {}
  public record AccountingQueueResponse(boolean queued, Long id) {}
}
