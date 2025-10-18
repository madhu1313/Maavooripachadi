import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { BehaviorSubject, EMPTY, combineLatest, of } from 'rxjs';
import { catchError, finalize, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { CartItem, CartService, CartSummary } from '../../core/services/cart.service';
import { CatalogService } from '../../core/services/catalog.service';
import { QuantityInputComponent } from '../../shared/components/quantity-input/quantity-input.component';
import { PricePipe } from '../../shared/pipes/price.pipe';
import { ProductCardComponent } from '../../shared/components/product-card/product-card.component';
import { AuthService } from '../../core/services/auth.service';

interface CartItemViewModel {
  variantId: number;
  title: string;
  quantity: number;
  unitPrice: number;
  unitPricePaise: number;
  lineTotal: number;
  lineTotalPaise: number;
  isProcessing: boolean;
  imageUrl?: string | null;
}

interface CartViewModel {
  hasItems: boolean;
  itemsCount: number;
  items: CartItemViewModel[];
  subtotal: number;
  subtotalPaise: number;
  isClearing: boolean;
}

const MAX_PER_ITEM = 12;

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [NgIf, NgFor, AsyncPipe, RouterLink, QuantityInputComponent, PricePipe, ProductCardComponent],
  templateUrl: './cart.page.html',
  styleUrls: ['./cart.page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CartPage {
  private readonly cart = inject(CartService);
  private readonly catalog = inject(CatalogService);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  private readonly processingIds$ = new BehaviorSubject<ReadonlySet<number>>(new Set());
  private readonly clearing$ = new BehaviorSubject<boolean>(false);
  private readonly infoSubject = new BehaviorSubject<string | null>(null);
  private readonly errorSubject = new BehaviorSubject<string | null>(null);

  readonly info$ = this.infoSubject.asObservable();
  readonly error$ = this.errorSubject.asObservable();

  readonly recommended$ = this.catalog.bestSellers(4).pipe(
    catchError((error) => {
      console.error('Failed to load recommended products', error);
      return of([]);
    }),
    shareReplay(1)
  );

  readonly vm$ = combineLatest([this.cart.cart$, this.processingIds$, this.clearing$]).pipe(
    map(([cart, processing, clearing]) => this.toViewModel(cart, processing, clearing)),
    shareReplay(1)
  );

  updateQuantity(variantId: number, nextQuantity: number) {
    if (this.processingIds$.value.has(variantId)) {
      return;
    }

    const snapshot = this.cart.getCartSnapshot();
    const item = snapshot.items.find((entry) => entry.variantId === variantId);
    if (!item) {
      return;
    }

    const sanitizedQty = Math.max(1, Math.min(MAX_PER_ITEM, Math.floor(nextQuantity)));
    if (sanitizedQty === item.qty) {
      return;
    }

    this.setProcessing(variantId, true);

    const request$ =
      sanitizedQty > item.qty
        ? this.cart.addItem(variantId, sanitizedQty - item.qty, item.unitPricePaise)
        : this.cart.removeItem(variantId).pipe(
            switchMap(() => this.cart.addItem(variantId, sanitizedQty, item.unitPricePaise))
          );

    request$
      .pipe(
        tap(() => {
          const title = item.title ?? 'item';
          this.infoSubject.next(`Updated ${title} to ${sanitizedQty} ${sanitizedQty > 1 ? 'packs' : 'pack'}.`);
        }),
        catchError((error) => {
          console.error('Failed to update quantity', error);
          this.errorSubject.next('Could not update the quantity. Please try again.');
          return EMPTY;
        }),
        finalize(() => this.setProcessing(variantId, false))
      )
      .subscribe();
  }

  remove(variantId: number) {
    if (this.processingIds$.value.has(variantId)) {
      return;
    }

    const snapshot = this.cart.getCartSnapshot();
    const item = snapshot.items.find((entry) => entry.variantId === variantId);

    this.setProcessing(variantId, true);
    this.cart
      .removeItem(variantId)
      .pipe(
        tap(() => {
          const title = item?.title ?? 'Item';
          this.infoSubject.next(`${title} removed from your cart.`);
        }),
        catchError((error) => {
          console.error('Failed to remove cart item', error);
          this.errorSubject.next('Unable to remove this product. Please refresh and try again.');
          return EMPTY;
        }),
        finalize(() => this.setProcessing(variantId, false))
      )
      .subscribe();
  }

  clearCart() {
    if (this.clearing$.value) {
      return;
    }

    this.clearing$.next(true);
    this.cart
      .clear()
      .pipe(
        tap(() => this.infoSubject.next('Your basket is now empty.')),
        catchError((error) => {
          console.error('Failed to clear cart', error);
          this.errorSubject.next('We could not clear the cart right now. Please try once more.');
          return EMPTY;
        }),
        finalize(() => this.clearing$.next(false))
      )
      .subscribe();
  }

  dismissFlash(kind: 'info' | 'error') {
    if (kind === 'info') {
      this.infoSubject.next(null);
    } else {
      this.errorSubject.next(null);
    }
  }

  trackItemById(_: number, item: CartItemViewModel) {
    return item.variantId;
  }

  goToCheckout(): void {
    if (!this.auth.isAuthenticated()) {
      this.errorSubject.next('Please sign in or create an account before placing your order.');
      this.router.navigate(['/account'], {
        queryParams: { redirectTo: '/checkout' }
      });
      return;
    }
    this.router.navigate(['/checkout']).catch(() => {
      this.errorSubject.next('We could not open checkout right now. Please try again.');
    });
  }

  private toViewModel(cart: CartSummary, processing: ReadonlySet<number>, isClearing: boolean): CartViewModel {
    const items = cart.items.map((item) => this.toItemViewModel(item, processing));
    return {
      hasItems: items.length > 0,
      items,
      itemsCount: cart.itemsCount ?? items.reduce((total, entry) => total + entry.quantity, 0),
      subtotalPaise: cart.subtotalPaise,
      subtotal: this.toRupees(cart.subtotalPaise),
      isClearing
    };
  }

  private toItemViewModel(item: CartItem, processing: ReadonlySet<number>): CartItemViewModel {
    const lineTotalPaise = item.qty * item.unitPricePaise;
    return {
      variantId: item.variantId,
      title: item.title ?? 'Maavoori Pachadi',
      quantity: item.qty,
      unitPricePaise: item.unitPricePaise,
      unitPrice: this.toRupees(item.unitPricePaise),
      lineTotalPaise,
      lineTotal: this.toRupees(lineTotalPaise),
      isProcessing: processing.has(item.variantId),
      imageUrl: item.imageUrl ?? null
    };
  }

  private toRupees(paise: number): number {
    return Math.round(paise) / 100;
  }

  private setProcessing(variantId: number, active: boolean) {
    const next = new Set(this.processingIds$.value);
    if (active) {
      next.add(variantId);
    } else {
      next.delete(variantId);
    }
    this.processingIds$.next(next);
  }
}
