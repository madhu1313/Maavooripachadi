import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { BehaviorSubject, Observable, combineLatest, of } from 'rxjs';
import { catchError, finalize, map, shareReplay, switchMap, tap } from 'rxjs/operators';
import { CatalogService, ProductDetailModel } from '../../core/services/catalog.service';
import { CartService } from '../../core/services/cart.service';
import { QuantityInputComponent } from '../../shared/components/quantity-input/quantity-input.component';
import { PricePipe } from '../../shared/pipes/price.pipe';
import { ProductCardComponent } from '../../shared/components/product-card/product-card.component';
import { Product } from '../../core/models/storefront.models';

type StatusKind = 'idle' | 'success' | 'error';

interface StatusState {
  kind: StatusKind;
  message: string | null;
}

interface VariantOption {
  id: number;
  label: string;
  inStock: boolean;
  price: number;
  isActive: boolean;
}

interface ProductViewModel {
  product: ProductDetailModel;
  selectedVariant: ProductDetailModel['variants'][number] | null;
  price: number;
  comparePrice: number | null;
  savings: number;
  hasDiscount: boolean;
  inStock: boolean;
  availabilityLabel: string;
  availabilityDescription: string;
  variantOptions: VariantOption[];
  breadcrumbLink: string[];
  breadcrumbLabel: string;
}

@Component({
  selector: 'app-product',
  standalone: true,
  imports: [NgIf, NgFor, NgClass, AsyncPipe, RouterLink, QuantityInputComponent, PricePipe, ProductCardComponent],
  templateUrl: './product.page.html',
  styleUrls: ['./product.page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ProductPage {
  private readonly route = inject(ActivatedRoute);
  private readonly catalog = inject(CatalogService);
  private readonly cart = inject(CartService);
  private readonly router = inject(Router);

  private readonly selectedVariantId$ = new BehaviorSubject<number | null>(null);
  private readonly addingToCartSubject = new BehaviorSubject<boolean>(false);
  private readonly statusSubject = new BehaviorSubject<StatusState>({ kind: 'idle', message: null });

  readonly addingToCart$ = this.addingToCartSubject.asObservable();
  readonly status$ = this.statusSubject.asObservable();

  quantity = 1;

  readonly heroHighlights = [
    {
      id: 'oils',
      title: 'Cold pressed oils',
      description: 'Sealed with gingelly and groundnut oils for authentic aroma.'
    },
    {
      id: 'sun-cured',
      title: 'Sun cured batches',
      description: 'Slow cured on our terrace for a minimum of 3 sunny days.'
    },
    {
      id: 'handcrafted',
      title: 'Handcrafted',
      description: 'Prepared in 48kg micro batches by our Maavoori kitchen team.'
    }
  ];

  readonly assurancePoints = [
    { id: 'safety', icon: 'verified', title: 'Lab tested safety', description: 'Every batch is tested for shelf life and microbial safety.' },
    { id: 'packaging', icon: 'package_2', title: 'Tamper-proof seal', description: 'Double sealed glass jars; leak-proof during transit.' },
    { id: 'support', icon: 'support_agent', title: 'Concierge support', description: 'Ping us on WhatsApp for pairing tips & gifting ideas.' }
  ];

  readonly tasteNotes = [
    'Balanced tang, spice and umami notes.',
    'Stone-ground masalas for a coarse, rustic texture.',
    'Finishes with gingelly oil richness on the palate.'
  ];

  readonly pairingSuggestions = [
    'Steaming hot rice topped with ghee.',
    'Millet dosa, pesarattu or idli platters.',
    'Slow cooked non-veg curries for festive spreads.'
  ];

  readonly careTips = [
    'Refrigerate after opening for longer freshness.',
    'Always use a dry spoon and close the lid tightly.',
    'Best consumed within 45 days of opening the jar.'
  ];

  product$: Observable<ProductDetailModel | undefined> = this.route.paramMap.pipe(
    switchMap((params) => {
      const id = params.get('id');
      return id ? this.catalog.getById(id) : of(undefined);
    }),
    tap((product) => {
      if (!product) {
        this.selectedVariantId$.next(null);
        return;
      }
      const currentId = this.selectedVariantId$.value;
      if (currentId && product.variants.some((variant) => variant.id === currentId)) {
        return;
      }
      const defaultVariant = product.variants.find((variant) => variant.inStock) ?? product.variants[0] ?? null;
      this.selectedVariantId$.next(defaultVariant?.id ?? null);
    }),
    shareReplay(1)
  );

  private readonly selectedVariant$ = combineLatest([this.product$, this.selectedVariantId$]).pipe(
    map(([product, selectedId]) => {
      if (!product) {
        return null;
      }
      return product.variants.find((variant) => variant.id === selectedId) ?? null;
    }),
    shareReplay(1)
  );

  readonly vm$: Observable<ProductViewModel | null> = combineLatest([this.product$, this.selectedVariant$]).pipe(
    map(([product, selectedVariant]) => {
      if (!product) {
        return null;
      }

      const variantOptions: VariantOption[] = product.variants.map((variant) => ({
        id: variant.id,
        label: variant.label,
        inStock: variant.inStock,
        price: this.toRupees(variant.pricePaise) ?? product.price,
        isActive: Boolean(selectedVariant && selectedVariant.id === variant.id)
      }));

      const price = this.toRupees(selectedVariant?.pricePaise) ?? product.price;
      const comparePrice = this.toRupees(product.mrpPaise) ?? product.compareAtPrice ?? null;
      const savings = comparePrice ? Math.max(comparePrice - price, 0) : 0;
      const inStock = selectedVariant ? selectedVariant.inStock : product.inStock;

      const availabilityLabel = inStock ? 'In stock' : 'Currently curing';
      const availabilityDescription = inStock
        ? 'Ships within 24 hours from our Hyderabad kitchen.'
        : 'This batch is in curing. Leave your details with us to be the first to know when it returns.';

      const collectionHandle = product.collectionIds?.[0];
      const breadcrumbLink = collectionHandle ? ['/collections', collectionHandle] : ['/shop'];
      const breadcrumbLabel = collectionHandle ? 'Back to collection' : 'Back to shop';

      return {
        product,
        selectedVariant,
        price,
        comparePrice,
        savings,
        hasDiscount: savings > 0.01,
        inStock,
        availabilityLabel,
        availabilityDescription,
        variantOptions,
        breadcrumbLink,
        breadcrumbLabel
      };
    }),
    shareReplay(1)
  );

  readonly relatedProducts$ = this.product$.pipe(
    switchMap((product) => {
      if (!product) {
        return of([] as Product[]);
      }
      return this.catalog.list({ size: 8, sort: 'popularity,desc' }).pipe(
        map((products) => products.filter((item) => item.id !== product.id).slice(0, 4))
      );
    })
  );

  updateQuantity(value: number) {
    this.quantity = Math.max(1, value);
  }

  selectVariant(variantId: number) {
    this.selectedVariantId$.next(variantId);
  }

  addToCart(product: ProductDetailModel | undefined) {
    if (!product) {
      return;
    }
    const variant = this.getSelectedVariant(product);
    if (!variant) {
      this.statusSubject.next({
        kind: 'error',
        message: 'Unable to add this product right now. Please refresh and try again.'
      });
      return;
    }
    if (!variant.inStock) {
      this.statusSubject.next({
        kind: 'error',
        message: 'This variant is currently curing. Please choose another option or check back soon.'
      });
      return;
    }
    if (this.addingToCartSubject.value) {
      return;
    }

    const quantityToAdd = Math.max(1, this.quantity);
    this.addingToCartSubject.next(true);
    this.cart
      .addItem(variant.id, quantityToAdd, variant.pricePaise)
      .pipe(
        tap(() => {
          const name = variant.label || product.title;
          this.statusSubject.next({
            kind: 'success',
            message: `${name} added to your cart.`
          });
          this.router.navigate(['/cart']);
        }),
        catchError((error) => {
          console.error('Failed to add item to cart', error);
          this.statusSubject.next({
            kind: 'error',
            message: 'Could not add to cart. Please try again in a moment.'
          });
          return of(null);
        }),
        finalize(() => this.addingToCartSubject.next(false))
      )
      .subscribe();
  }

  dismissStatus(): void {
    this.statusSubject.next({ kind: 'idle', message: null });
  }

  trackVariant(_: number, option: VariantOption) {
    return option.id;
  }

  private getSelectedVariant(product: ProductDetailModel) {
    const variantId = this.selectedVariantId$.value;
    return product.variants.find((variant) => variant.id === variantId) ?? product.variants[0] ?? null;
  }

  private toRupees(value: number | null | undefined): number | null {
    if (value === null || value === undefined) {
      return null;
    }
    return Math.round(value) / 100;
  }
}
