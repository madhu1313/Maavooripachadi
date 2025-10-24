import { Injectable, isDevMode } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { CheckoutService } from './checkout.service';

interface CurrencyOption {
  code: string;
  label: string;
  locale: string;
}

interface CurrencyState {
  code: string;
  /**
   * Multiplier that converts an amount expressed in INR paise into the minor units of the target currency.
   * Example: for USD the multiplier is roughly 0.012, meaning 1 INR paise becomes 0.012 US cents.
   */
  paiseToMinorRate: number;
}

const STORAGE_KEY = 'maavoori.currency';
const BASE_SAMPLE_PAISE = 100_000; // Rs 1,000 worth of paise gives a stable conversion ratio.

@Injectable({ providedIn: 'root' })
export class CurrencyService {
  readonly options: CurrencyOption[] = [
    { code: 'INR', label: 'INR (Rs)', locale: 'en-IN' },
    { code: 'USD', label: 'USD ($)', locale: 'en-US' },
    { code: 'SGD', label: 'SGD ($)', locale: 'en-SG' }
  ];

  private readonly rates = new Map<string, number>([['INR', 1]]);
  private readonly stateSubject = new BehaviorSubject<CurrencyState>(this.loadInitialState());

  readonly state$: Observable<CurrencyState> = this.stateSubject.asObservable();

  constructor(private readonly checkout: CheckoutService) {
    const current = this.stateSubject.value;
    if (current.code !== 'INR' && current.paiseToMinorRate === 1) {
      this.refreshRate(current.code);
    }
    if (current.code !== 'INR') {
      this.updateState({ code: 'INR', paiseToMinorRate: 1 });
    }
  }

  get current(): CurrencyState {
    return this.stateSubject.value;
  }

  getLocale(code: string): string {
    return this.options.find((option) => option.code === code)?.locale ?? 'en-IN';
  }

  convertFromInr(amountInInr: number, targetCode?: string): number {
    const code = (targetCode ?? this.current.code).toUpperCase();
    if (!Number.isFinite(amountInInr)) {
      return 0;
    }
    const safeAmount = amountInInr || 0;
    if (code === 'INR') {
      return safeAmount;
    }

    const rate = this.rates.get(code) ?? this.current.paiseToMinorRate;
    const paise = Math.round(safeAmount * 100);
    const targetMinor = Math.round(paise * rate);
    return targetMinor / 100;
  }

  setCurrency(code: string): void {
    const normalized = code?.toUpperCase?.() || 'INR';
    if (normalized === this.current.code) {
      return;
    }
    if (!this.options.some((option) => option.code === normalized)) {
      if (isDevMode()) {
        console.warn(`Unsupported currency selection: ${normalized}`);
      }
      return;
    }
    if (normalized === 'INR') {
      this.updateState({ code: 'INR', paiseToMinorRate: 1 });
      return;
    }

    const cachedRate = this.rates.get(normalized);
    if (cachedRate) {
      this.updateState({ code: normalized, paiseToMinorRate: cachedRate });
      return;
    }

    const previous = this.current;
    const fallbackRate = previous.code === 'INR' ? 1 : previous.paiseToMinorRate;
    this.updateState({ code: normalized, paiseToMinorRate: fallbackRate });
    this.refreshRate(normalized);
  }

  private refreshRate(code: string): void {
    this.checkout.convertPricing(BASE_SAMPLE_PAISE, code).subscribe({
      next: (quote) => {
        const rate = quote.amount > 0 ? quote.amount / BASE_SAMPLE_PAISE : 0;
        if (rate > 0) {
          this.rates.set(code, rate);
          this.updateState({ code, paiseToMinorRate: rate });
        } else if (isDevMode()) {
          console.warn(`Received zero conversion rate for currency ${code}, keeping previous state.`);
        }
      },
      error: (error) => {
        if (isDevMode()) {
          console.error('Failed to load currency rate', error);
        }
      }
    });
  }

  private updateState(state: CurrencyState): void {
    this.stateSubject.next(state);
    this.persistState(state);
  }

  private loadInitialState(): CurrencyState {
    const defaultState: CurrencyState = { code: 'INR', paiseToMinorRate: 1 };
    if (!this.hasStorage()) {
      return defaultState;
    }
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) {
        return defaultState;
      }
      const parsed = JSON.parse(raw) as Partial<CurrencyState>;
      if (parsed?.code && typeof parsed.paiseToMinorRate === 'number' && parsed.paiseToMinorRate > 0) {
        this.rates.set(parsed.code, parsed.paiseToMinorRate);
        return {
          code: parsed.code,
          paiseToMinorRate: parsed.paiseToMinorRate
        };
      }
    } catch (error) {
      if (isDevMode()) {
        console.warn('Unable to restore currency preference', error);
      }
    }
    return defaultState;
  }

  private persistState(state: CurrencyState): void {
    if (!this.hasStorage()) {
      return;
    }
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(state));
    } catch (error) {
      if (isDevMode()) {
        console.warn('Unable to persist currency preference', error);
      }
    }
  }

  private hasStorage(): boolean {
    return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined';
  }
}
