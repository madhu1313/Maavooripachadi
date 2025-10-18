package com.maavooripachadi.risk;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class VelocityService {
  private final VelocityWindowRepository windows; private final RiskEventRepository events;
  public VelocityService(VelocityWindowRepository windows, RiskEventRepository events){ this.windows = windows; this.events = events; }

  @Transactional(readOnly = true)
  public String check(String ip, String email, String deviceId){
    List<VelocityWindow> all = windows.findAll();
    for (VelocityWindow w : all){
      OffsetDateTime since = OffsetDateTime.now().minusSeconds(w.getWindowSeconds());
      long c = events.countSinceForAny(since, ip, email, deviceId);
      if (c > w.getMaxCount()){
        return "velocity:"+w.getKeyExpression()+">"+w.getMaxCount();
      }
    }
    return null;
  }
}
