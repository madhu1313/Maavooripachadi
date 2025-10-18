import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService, QueryParams } from './api.service';
import { Product } from '../models/storefront.models';

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

interface ProductCardResponse {
  id: number;
  title: string;
  slug: string;
  imageUrl: string;
  pricePaise: number;
  mrpPaise?: number | null;
  inStock: boolean;
  badge?: string | null;
}

interface ProductDetailResponse {
  id: number;
  title: string;
  slug: string;
  descriptionHtml: string;
  heroImageUrl: string;
  pricePaise: number;
  mrpPaise?: number | null;
  inStock: boolean;
  badge?: string | null;
  variants: Array<{
    id: number;
    sku: string;
    label: string;
    pricePaise: number;
    inStock: boolean;
  }>;
}

interface ProductSuggestionResponse {
  id: number;
  title: string;
  slug: string;
}

export interface ProductDetailModel extends Product {
  descriptionHtml: string;
  pricePaise: number;
  mrpPaise?: number | null;
  inStock: boolean;
  heroImageUrl: string;
  variants: Array<{
    id: number;
    sku: string;
    label: string;
    pricePaise: number;
    inStock: boolean;
  }>;
}

export interface CatalogListParams {
  page?: number;
  size?: number;
  sort?: string;
  category?: string;
  tag?: string;
  q?: string;
  minPricePaise?: number;
  maxPricePaise?: number;
}

@Injectable({ providedIn: 'root' })
export class CatalogService {
  constructor(private readonly api: ApiService) {}

  list(params: CatalogListParams = {}): Observable<Product[]> {
    const query = this.buildListQuery(params);
    if (!query['size']) {
      query['size'] = 60;
    }
    return this.api
      .get<PageResponse<ProductCardResponse>>({ key: 'catalog', query })
      .pipe(map((page) => page.content.map((card) => this.mapCard(card))));
  }

  listCollection(category: string, limitOrParams?: number | Omit<CatalogListParams, 'category'>): Observable<Product[]> {
    let params: CatalogListParams = { category };
    if (typeof limitOrParams === 'number') {
      params.size = limitOrParams;
    } else if (limitOrParams) {
      params = { ...params, ...limitOrParams };
    }
    return this.list(params);
  }

  bestSellers(limit = 8): Observable<Product[]> {
    return this.list({ tag: 'best-seller', size: limit, sort: 'popularity,desc' });
  }

  newArrivals(limit = 8): Observable<Product[]> {
    return this.list({ size: limit, sort: 'createdAt,desc' });
  }

  search(term: string): Observable<Product[]> {
    return this.list({ q: term, size: 12 });
  }

  suggest(term: string): Observable<ProductSuggestionResponse[]> {
    return this.api.get<ProductSuggestionResponse[]>({ key: 'search', query: { q: term } });
  }

  getById(slug: string): Observable<ProductDetailModel> {
    return this.api
      .get<ProductDetailResponse>(this.api.url('product', { productId: slug }))
      .pipe(map((detail) => this.mapDetail(detail)));
  }

  private buildListQuery(params: CatalogListParams): QueryParams {
    const query: QueryParams = {};
    if (params.page !== undefined) query['page'] = params.page;
    if (params.size !== undefined) query['size'] = params.size;
    if (params.sort) query['sort'] = params.sort;
    if (params.category) query['category'] = params.category;
    if (params.tag) query['tag'] = params.tag;
    if (params.q) query['q'] = params.q;
    if (params.minPricePaise !== undefined) query['minPricePaise'] = params.minPricePaise;
    if (params.maxPricePaise !== undefined) query['maxPricePaise'] = params.maxPricePaise;
    return query;
  }

  private mapCard(card: ProductCardResponse): Product {
    return {
      id: card.slug,
      title: card.title,
      description: '',
      image: card.imageUrl,
      price: this.toCurrency(card.pricePaise),
      compareAtPrice: card.mrpPaise != null ? this.toCurrency(card.mrpPaise) : undefined,
      badge: card.badge ?? undefined,
      tags: [],
      collectionIds: [],
      rating: undefined,
      reviewsCount: undefined,
      isNew: false,
      isBestSeller: false
    };
  }

  private mapDetail(detail: ProductDetailResponse): ProductDetailModel {
    const baseProduct: ProductDetailModel = {
      id: detail.slug,
      title: detail.title,
      description: this.stripHtml(detail.descriptionHtml),
      image: detail.heroImageUrl,
      price: this.toCurrency(detail.pricePaise),
      compareAtPrice: detail.mrpPaise != null ? this.toCurrency(detail.mrpPaise) : undefined,
      badge: detail.badge ?? undefined,
      tags: [],
      collectionIds: [],
      rating: undefined,
      reviewsCount: undefined,
      isNew: false,
      isBestSeller: false,
      descriptionHtml: detail.descriptionHtml,
      pricePaise: detail.pricePaise,
      mrpPaise: detail.mrpPaise ?? undefined,
      inStock: detail.inStock,
      heroImageUrl: detail.heroImageUrl,
      variants: detail.variants.map((variant) => ({
        id: variant.id,
        sku: variant.sku,
        label: variant.label,
        pricePaise: variant.pricePaise,
        inStock: variant.inStock
      }))
    };
    return baseProduct;
  }

  private toCurrency(paise: number): number {
    return Math.round(paise) / 100;
  }

  private stripHtml(html: string): string {
    if (!html) {
      return '';
    }
    return html.replace(/<[^>]+>/g, '').trim();
  }
}
