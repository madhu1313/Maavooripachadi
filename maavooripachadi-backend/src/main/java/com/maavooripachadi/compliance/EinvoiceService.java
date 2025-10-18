package com.maavooripachadi.compliance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EinvoiceService {
  private final EinvoiceMetaRepository einvoiceMetaRepository;

    /** Map an order to E-Invoice DTO. Replace with real Order repo lookups. */
  public EinvoiceDTO map(String orderNo) {
    // TODO: load order + items + taxes. Here we stub minimal payload.
    var items = List.of(new EinvoiceDTO.Item("200290", "Mango Pickle 500g", 1, 29900, 29900));
    return new EinvoiceDTO(orderNo, "36ABCDE1234F1Z5", "", "Guest",
        "Hyderabad", "36", 29900, 29900, 0, 0, 0, items);
  }

  /** Register the invoice with IRP (stub). Persist IRN/QR. */
  @Transactional
  public EinvoiceMeta register(EinvoiceDTO dto) {
    var meta = einvoiceMetaRepository.findByOrderNo(dto.orderNo()).orElseGet(EinvoiceMeta::new);
    meta.setOrderNo(dto.orderNo());
    meta.setIrn("IRN-" + dto.orderNo());
    meta.setAckNo("ACK-" + System.currentTimeMillis());
    meta.setAckDt(OffsetDateTime.now());
    meta.setSignedQr("QR://stub/" + dto.orderNo());
    meta.setPayloadJson("{}"); // TODO archive real IRP request/response
    return einvoiceMetaRepository.save(meta);
  }
}
