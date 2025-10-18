import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BehaviorSubject, Observable, combineLatest, of } from 'rxjs';
import { catchError, map, shareReplay, startWith, switchMap, tap } from 'rxjs/operators';
import { CatalogService, CatalogListParams } from '../../core/services/catalog.service';
import { StorefrontService } from '../../core/services/storefront.service';
import { Collection, Product } from '../../core/models/storefront.models';
import { ProductCardComponent } from '../../shared/components/product-card/product-card.component';

type SortOptionId = 'featured' | 'price-asc' | 'price-desc' | 'newest';
type PriceFilterId = 'all' | 'under-400' | 'between-400-600' | 'above-600';

interface SortOption {
  id: SortOptionId;
  label: string;
  helper?: string;
}

interface PriceFilter {
  id: PriceFilterId;
  label: string;
  helper?: string;
  min?: number;
  max?: number;
}

interface HeroStat {
  value: string;
  label: string;
}

interface MerchHighlight {
  id: string;
  title: string;
  description: string;
}

interface ShopViewModel {
  products: Product[];
  total: number;
  hasResults: boolean;
  category: string;
  sort: SortOptionId;
  price: PriceFilterId;
  query: string;
  activeCollection: Collection | null;
  error: string | null;
}

const SORT_OPTIONS: SortOption[] = [
  { id: 'featured', label: 'Featured', helper: 'Popular across the Maavoori community' },
  { id: 'price-asc', label: 'Price: Low to High' },
  { id: 'price-desc', label: 'Price: High to Low' },
  { id: 'newest', label: 'Latest additions', helper: 'Fresh batches and seasonal releases' }
];

const PRICE_FILTERS: PriceFilter[] = [
  { id: 'all', label: 'Any price' },
  { id: 'under-400', label: 'Under ₹400', max: 399.99 },
  { id: 'between-400-600', label: '₹400 - ₹600', min: 400, max: 600 },
  { id: 'above-600', label: 'Above ₹600', min: 600.01 }
];

const HERO_STATS: HeroStat[] = [
  { value: '48 kg', label: 'Micro batches per run' },
  { value: '120 hrs', label: 'Sun curing & resting' },
  { value: '50K+', label: 'Families served' }
];

const MERCH_HIGHLIGHTS: MerchHighlight[] = [
  {
    id: 'small-batch',
    title: 'Small-batch freshness',
    description: 'We craft in 48 kg micro batches so every jar tastes like a weekend at home.'
  },
  {
    id: 'cold-pressed',
    title: 'Cold pressed oils',
    description: 'Sesame and groundnut oils seal each jar, just the way Maavoori Ammamma taught us.'
  },
  {
    id: 'leak-proof',
    title: 'Leak-proof packing',
    description: 'Secure glass jars with double sealing deliver safely across India.'
  }
];

const CATEGORY_ALIAS_MAP: Record<string, string> = {
  all: 'all',
  'veg-pickles': 'veg-pickles',
  'non-veg-pickles': 'non-veg-pickles',
  podi: 'podi',
  'snacks-sweets': 'snacks-sweets',
  'sweets-n-snacks': 'snacks-sweets'
};

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [NgFor, NgIf, NgClass, AsyncPipe, ReactiveFormsModule, ProductCardComponent],
  templateUrl: './shop.page.html',
  styleUrls: ['./shop.page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ShopPage {
  private readonly catalog = inject(CatalogService);
  private readonly storefront = inject(StorefrontService);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  readonly sortOptions = SORT_OPTIONS;
  readonly priceFilters = PRICE_FILTERS;
  readonly heroStats = HERO_STATS;
  readonly merchHighlights = MERCH_HIGHLIGHTS;

  private readonly categorySubject = new BehaviorSubject<string>('all');
  readonly selectedCategory$ = this.categorySubject.asObservable();
  private readonly productsErrorSubject = new BehaviorSubject<string | null>(null);
  private readonly productsError$ = this.productsErrorSubject.asObservable();

  readonly filterForm = this.fb.nonNullable.group({
    query: [''],
    sort: ['featured' as SortOptionId],
    price: ['all' as PriceFilterId]
  });

  private readonly query$ = this.filterForm.controls.query.valueChanges.pipe(startWith(this.filterForm.controls.query.value));
  private readonly sort$ = this.filterForm.controls.sort.valueChanges.pipe(startWith(this.filterForm.controls.sort.value));
  private readonly price$ = this.filterForm.controls.price.valueChanges.pipe(startWith(this.filterForm.controls.price.value));

  private readonly collectionsInternal$ = this.storefront.getCollections().pipe(shareReplay(1));
  readonly collections$ = this.collectionsInternal$;

  private readonly activeCollection$ = combineLatest([this.collectionsInternal$, this.selectedCategory$]).pipe(
    map(([collections, category]) => (category === 'all' ? null : collections.find((c) => c.id === category) ?? null)),
    shareReplay(1)
  );

  private readonly baseProducts$: Observable<Product[]> = combineLatest([this.selectedCategory$, this.sort$]).pipe(
    switchMap(([category, sort]) => {
      const params: CatalogListParams = { size: 60 };
      const sortParam = this.mapSortToApi(sort);
      if (sortParam) {
        params.sort = sortParam;
      }
      if (category !== 'all') {
        params.category = category;
      }
      return this.catalog.list(params).pipe(
        tap(() => this.productsErrorSubject.next(null)),
        catchError((error) => {
          console.error('Failed to load catalog products', error);
          this.productsErrorSubject.next('We could not load the products right now. Please try again in a moment.');
          return of<Product[]>([]);
        })
      );
    }),
    shareReplay(1)
  );

  private readonly filteredProducts$ = combineLatest([this.baseProducts$, this.query$, this.price$, this.sort$]).pipe(
    map(([products, query, priceId, sortId]) => {
      let working = products;

      if (query) {
        const lowered = query.trim().toLowerCase();
        working = working.filter((product) =>
          [product.title, product.description, product.badge]
            .filter(Boolean)
            .some((value) => (value ?? '').toLowerCase().includes(lowered))
        );
      }

      const priceFilter = PRICE_FILTERS.find((option) => option.id === priceId) ?? PRICE_FILTERS[0];
      if (priceFilter.id !== 'all') {
        working = working.filter((product) => {
          const price = product.price ?? 0;
          if (priceFilter.min !== undefined && price < priceFilter.min) {
            return false;
          }
          if (priceFilter.max !== undefined && price > priceFilter.max) {
            return false;
          }
          return true;
        });
      }

      return this.applyClientSort(working, sortId);
    })
  );

  readonly bestSellers$ = this.catalog.bestSellers(4).pipe(shareReplay(1));
  readonly newArrivals$ = this.catalog.newArrivals(4).pipe(shareReplay(1));

  readonly vm$: Observable<ShopViewModel> = combineLatest([
    this.filteredProducts$,
    this.selectedCategory$,
    this.sort$,
    this.price$,
    this.query$,
    this.activeCollection$,
    this.productsError$
  ]).pipe(
    map(([products, category, sort, price, query, activeCollection, error]) => ({
      products,
      total: products.length,
      hasResults: products.length > 0,
      category,
      sort,
      price,
      query,
      activeCollection,
      error
    }))
  );

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      const handle = params.get('handle');
      this.setCategory(handle ?? 'all');
    });
  }

  setCategory(categoryId: string): void {
    const normalized = this.normalizeCategory(categoryId);
    if (this.categorySubject.value === normalized) {
      return;
    }
    this.categorySubject.next(normalized);
  }

  onPriceSelect(priceId: PriceFilterId): void {
    this.filterForm.patchValue({ price: priceId });
  }

  onSortSelect(sortId: SortOptionId): void {
    this.filterForm.patchValue({ sort: sortId });
  }

  clearSearch(): void {
    this.filterForm.patchValue({ query: '' });
  }

  trackProductBy(_: number, product: Product): string {
    return product.id;
  }

  private mapSortToApi(sort: SortOptionId): string | undefined {
    switch (sort) {
      case 'price-asc':
        return 'pricePaise,asc';
      case 'price-desc':
        return 'pricePaise,desc';
      case 'newest':
        return 'createdAt,desc';
      default:
        return 'popularity,desc';
    }
  }

  private applyClientSort(products: Product[], sort: SortOptionId): Product[] {
    if (sort === 'price-asc') {
      return [...products].sort((a, b) => (a.price ?? 0) - (b.price ?? 0));
    }
    if (sort === 'price-desc') {
      return [...products].sort((a, b) => (b.price ?? 0) - (a.price ?? 0));
    }
    return products;
  }

  private normalizeCategory(category: string): string {
    if (!category) {
      return 'all';
    }
    const lowered = category.trim().toLowerCase();
    return CATEGORY_ALIAS_MAP[lowered] ?? lowered;
  }
}
