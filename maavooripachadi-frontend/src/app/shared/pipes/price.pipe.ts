import { Pipe, PipeTransform } from '@angular/core';
import { CurrencyService } from '../../core/services/currency.service';

type PricePipeOptions = {
  hideSymbol?: boolean;
  locale?: string;
  minimumFractionDigits?: number;
  maximumFractionDigits?: number;
};

const FALLBACK_LOCALE_BY_CURRENCY: Record<string, string> = {
  INR: 'en-IN',
  USD: 'en-US',
  SGD: 'en-SG',
  EUR: 'en-DE',
  GBP: 'en-GB',
  AUD: 'en-AU'
};

@Pipe({
  name: 'price',
  standalone: true,
  pure: false
})
export class PricePipe implements PipeTransform {
  constructor(private readonly currency: CurrencyService) {}

  transform(
    amount: number | null | undefined,
    currencyCode?: string,
    options: PricePipeOptions = {}
  ): string {
    if (amount === null || amount === undefined || Number.isNaN(amount)) {
      return '';
    }

    const targetCurrency = currencyCode?.toUpperCase?.() || this.currency.current.code || 'INR';
    const locale =
      options.locale ||
      this.currency.getLocale(targetCurrency) ||
      FALLBACK_LOCALE_BY_CURRENCY[targetCurrency] ||
      'en-IN';

    let value = amount;
    if (!currencyCode) {
      value = this.currency.convertFromInr(amount, targetCurrency);
    }

    const formatter = new Intl.NumberFormat(locale, {
      style: options.hideSymbol ? 'decimal' : 'currency',
      currency: targetCurrency,
      minimumFractionDigits: options.minimumFractionDigits ?? 0,
      maximumFractionDigits: options.maximumFractionDigits ?? 2
    });

    const formatted = formatter.format(value);
    const rupeeSymbol = '\u20B9';
    if (!options.hideSymbol && targetCurrency === 'INR' && !formatted.includes(rupeeSymbol)) {
      return `${rupeeSymbol}${formatted}`.trim();
    }
    return formatted.trim();
  }
}
