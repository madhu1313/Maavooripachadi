import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { ApiService } from './api.service';

export interface CartItem {
  variantId: number;
  title: string;
  qty: number;
  unitPricePaise: number;
  imageUrl?: string | null;
}

export interface CartSummary {
  id?: number;
  sessionId: string;
  itemsCount: number;
  subtotalPaise: number;
  items: CartItem[];
}

interface CartResponse {
  id?: number;
  sessionId: string;
  itemsCount: number;
  subtotalPaise: number;
  items: CartItemResponse[];
}

interface CartItemResponse {
  variantId: number;
  title: string;
  qty: number;
  unitPricePaise: number;
  imageUrl?: string | null;
}

interface AddToCartPayload {
  sessionId: string;
  variantId: number;
  qty: number;
  unitPricePaise: number;
}

interface RemoveFromCartPayload {
  sessionId: string;
  variantId: number;
}

const CART_SESSION_KEY = 'maavoori.cart.session';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly sessionId = this.ensureSessionId();
  private readonly cartSubject = new BehaviorSubject<CartSummary>(this.emptyState(this.sessionId));

  readonly cart$ = this.cartSubject.asObservable();

  constructor(private readonly api: ApiService) {
    this.refresh().subscribe();
  }

  getCartSnapshot(): CartSummary {
    return this.cartSubject.value;
  }

  refresh(): Observable<CartSummary> {
    return this.api
      .get<CartResponse>({ key: 'cartView', query: { sessionId: this.sessionId } })
      .pipe(map((res) => this.toSummary(res)), tap((summary) => this.cartSubject.next(summary)), catchError(() => {
        const empty = this.emptyState(this.sessionId);
        this.cartSubject.next(empty);
        return of(empty);
      }));
  }

  addItem(variantId: number, qty: number, unitPricePaise: number): Observable<CartSummary> {
    const payload: AddToCartPayload = {
      sessionId: this.sessionId,
      variantId,
      qty,
      unitPricePaise
    };

    return this.api
      .post<CartResponse>({ key: 'cartAdd', body: payload })
      .pipe(map((res) => this.toSummary(res)), tap((summary) => this.cartSubject.next(summary)));
  }

  removeItem(variantId: number): Observable<CartSummary> {
    const payload: RemoveFromCartPayload = {
      sessionId: this.sessionId,
      variantId
    };

    return this.api
      .post<CartResponse>({ key: 'cartRemove', body: payload })
      .pipe(map((res) => this.toSummary(res)), tap((summary) => this.cartSubject.next(summary)));
  }

  clear(): Observable<CartSummary> {
    return this.api
      .post<CartResponse>({ key: 'cartClear', query: { sessionId: this.sessionId } })
      .pipe(map((res) => this.toSummary(res)), tap((summary) => this.cartSubject.next(summary)));
  }

  getSessionId(): string {
    return this.sessionId;
  }

  private toSummary(response: CartResponse | null | undefined): CartSummary {
    if (!response) {
      return this.emptyState(this.sessionId);
    }

    const items: CartItem[] = (response.items ?? []).map((item) => ({
      variantId: item.variantId,
      title: item.title ?? 'Maavoori Pachadi',
      qty: item.qty,
      unitPricePaise: item.unitPricePaise,
      imageUrl: item.imageUrl ?? undefined
    }));

    return {
      id: response.id ?? undefined,
      sessionId: response.sessionId ?? this.sessionId,
      itemsCount: response.itemsCount ?? items.length,
      subtotalPaise: response.subtotalPaise ?? this.computeSubtotal(items),
      items
    };
  }

  private emptyState(sessionId: string): CartSummary {
    return {
      sessionId,
      itemsCount: 0,
      subtotalPaise: 0,
      items: []
    };
  }

  private computeSubtotal(items: CartItem[]): number {
    return items.reduce((total, item) => total + item.qty * item.unitPricePaise, 0);
  }

  private ensureSessionId(): string {
    if (typeof localStorage === 'undefined') {
      return this.generateSessionId();
    }

    const existing = localStorage.getItem(CART_SESSION_KEY);
    if (existing) {
      return existing;
    }
    const generated = this.generateSessionId();
    localStorage.setItem(CART_SESSION_KEY, generated);
    return generated;
  }

  private generateSessionId(): string {
    if (typeof crypto !== 'undefined' && 'randomUUID' in crypto) {
      return crypto.randomUUID();
    }
    return `maavoori_${Math.random().toString(36).slice(2, 12)}`;
  }
}

