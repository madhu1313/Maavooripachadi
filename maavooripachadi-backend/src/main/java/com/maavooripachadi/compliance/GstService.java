package com.maavooripachadi.compliance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GstService {
  private final Gstr1SummaryRepository repo;

  /** Return a simple GSTR-1 summary map for the period. */
  public Map<String, Object> gstr1(String period) {
    var summary = repo.findByPeriod(period).orElseGet(() -> seed(period));
    return Map.of(
        "period", summary.getPeriod(),
        "b2cCount", summary.getB2cCount(),
        "b2cTaxablePaise", summary.getB2cTaxablePaise(),
        "b2cTaxPaise", summary.getB2cTaxPaise(),
        "b2bCount", summary.getB2bCount(),
        "b2bTaxablePaise", summary.getB2bTaxablePaise(),
        "b2bTaxPaise", summary.getB2bTaxPaise()
    );
  }

  private Gstr1Summary seed(String period) {
    var summary = new Gstr1Summary();
    summary.setPeriod(period);
    summary.setB2cCount(0);
    summary.setB2cTaxablePaise(0);
    summary.setB2cTaxPaise(0);
    summary.setB2bCount(0);
    summary.setB2bTaxablePaise(0);
    summary.setB2bTaxPaise(0);
    return repo.save(summary);
  }
}
