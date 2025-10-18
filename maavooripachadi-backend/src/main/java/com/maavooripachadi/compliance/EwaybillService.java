package com.maavooripachadi.compliance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class EwaybillService {

  private final EwaybillRepository repo;

  @Transactional
  public Ewaybill create(String orderNo, String vehicleNo, int distanceKm) {
    var ewaybill = repo.findByOrderNo(orderNo).orElseGet(Ewaybill::new);
    ewaybill.setOrderNo(orderNo);
    ewaybill.setVehicleNo(vehicleNo);
    ewaybill.setDistanceKm(distanceKm);
    if (ewaybill.getEwbNo() == null) {
      ewaybill.setEwbNo("EWB-" + System.currentTimeMillis());
    }
    ewaybill.setValidUpto(OffsetDateTime.now().plusDays(1));
    return repo.save(ewaybill);
  }
}
