import { Pipe, PipeTransform } from '@angular/core';

type PricePipeOptions = {
  hideSymbol?: boolean;
  locale?: string;
  minimumFractionDigits?: number;
  maximumFractionDigits?: number;
};

const DEFAULT_LOCALE_BY_CURRENCY: Record<string, string> = {
  INR: 'en-IN',
  USD: 'en-US',
  SGD: 'en-SG',
  EUR: 'en-DE',
  GBP: 'en-GB',
  AUD: 'en-AU'
};

@Pipe({
  name: 'price',
  standalone: true
})
export class PricePipe implements PipeTransform {
  transform(
    amount: number | null | undefined,
    currencyCode: string = 'INR',
    options: PricePipeOptions = {}
  ): string {
    if (amount === null || amount === undefined || Number.isNaN(amount)) {
      return '';
    }

    const safeCurrency = currencyCode?.toUpperCase?.() || 'INR';
    const locale = options.locale || DEFAULT_LOCALE_BY_CURRENCY[safeCurrency] || 'en-IN';

    const formatter = new Intl.NumberFormat(locale, {
      style: options.hideSymbol ? 'decimal' : 'currency',
      currency: safeCurrency,
      minimumFractionDigits: options.minimumFractionDigits ?? 0,
      maximumFractionDigits: options.maximumFractionDigits ?? 2
    });

    let formatted = formatter.format(amount);

    if (!options.hideSymbol && safeCurrency === 'INR' && !formatted.includes('₹')) {
      formatted = `₹${formatted}`;
    }

    return formatted.trim();
  }
}
