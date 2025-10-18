package com.maavooripachadi.pricing;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PriceListService {
  private final CurrencyRateRepository rates;

  public int convert(int inrPaise, String to) {
    if (to == null || to.isBlank() || "INR".equalsIgnoreCase(to)) {
      return inrPaise;
    }
    double rate = rates.findByFromAndTo("INR", to.toUpperCase()).map(CurrencyRate::getRate).orElse(0.0d);
    if (rate <= 0.0d) {
      return inrPaise;
    }
    long minorUnits = Math.round((inrPaise / 100.0d) * rate * 100.0d);
    return (int) minorUnits;
  }
}
