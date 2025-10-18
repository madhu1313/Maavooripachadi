import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';

type HttpParamPrimitive = string | number | boolean;
type HttpParamRecord = Record<string, HttpParamPrimitive | ReadonlyArray<HttpParamPrimitive>>;
type QueryValue = HttpParamPrimitive | ReadonlyArray<HttpParamPrimitive> | null | undefined;
export type QueryParams = Record<string, QueryValue>;

export interface ApiHttpOptions {
  headers?: HttpHeaders | Record<string, string | string[]>;
  params?: HttpParams | HttpParamRecord;
  observe?: 'body';
  responseType?: 'json';
  withCredentials?: boolean;
  reportProgress?: boolean;
}

interface ApiRequestConfig<K extends EndpointKey = EndpointKey> {
  key?: K;
  url?: string;
  pathParams?: EndpointParams<K>;
  query?: QueryParams;
  body?: unknown;
  options?: ApiHttpOptions;
}

type EndpointFn<TParams extends Record<string, string | number>> = (params: TParams) => string;

interface EndpointRegistry {
  catalog: string;
  product: EndpointFn<{ productId: string | number }>;
  collections: string;
  collection: EndpointFn<{ handle: string }>;
  search: string;
  featured: string;
  bestSellers: string;
  newArrivals: string;
  cartAdd: string;
  cartRemove: string;
  cartClear: string;
  cartView: string;
  checkout: string;
  checkoutOrder: EndpointFn<{ orderNo: string | number }>;
  orders: string;
  order: EndpointFn<{ orderId: string | number }>;
  authLogin: string;
  authRegister: string;
  authRefresh: string;
  authMe: string;
  supportTickets: string;
  supportMessage: string;
  supportCsat: string;
  reviews: string;
  reviewsVote: string;
  reviewsFlag: string;
  newsletter: string;
  cmsPage: EndpointFn<{ slug: string }>;
  cmsBlocks: string;
  pricingQuote: string;
  pricingConvert: string;
  shippingQuote: string;
  shippingTrack: EndpointFn<{ orderNo: string | number }>;
  paymentsIntent: string;
  paymentsCapture: string;
  paymentsRefund: string;
  analyticsSnapshot: string;
  adminProducts: string;
  adminStats: string;
  adminNote: string;
  adminAuditLogs: string;
}

type EndpointKey = keyof EndpointRegistry;
type EndpointParams<K extends EndpointKey> = EndpointRegistry[K] extends EndpointFn<infer P> ? P : undefined;
type EndpointParamMap<K extends EndpointKey> =
  EndpointParams<K> extends Record<string, string | number> ? EndpointParams<K> : never;

@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly baseUrl = this.sanitizeBaseUrl(environment.apiBase);

  private readonly endpoints: EndpointRegistry = {
    catalog: '/catalog/products',
    product: ({ productId }) => `/catalog/products/${productId}`,
    collections: '/catalog/collections',
    collection: ({ handle }) => `/catalog/collections/${handle}`,
    search: '/catalog/search',
    featured: '/catalog/products/featured',
    bestSellers: '/catalog/products/best-sellers',
    newArrivals: '/catalog/products/new-arrivals',
    cartAdd: '/cart/add',
    cartRemove: '/cart/remove',
    cartClear: '/cart/clear',
    cartView: '/cart/view',
    checkout: '/checkout',
    checkoutOrder: ({ orderNo }) => `/checkout/${orderNo}`,
    orders: '/account/orders',
    order: ({ orderId }) => `/account/orders/${orderId}`,
    authLogin: '/auth/login',
    authRegister: '/auth/register',
    authRefresh: '/auth/refresh',
    authMe: '/auth/me',
    supportTickets: '/support/tickets',
    supportMessage: '/support/message',
    supportCsat: '/support/csat',
    reviews: '/reviews',
    reviewsVote: '/reviews/vote',
    reviewsFlag: '/reviews/flag',
    newsletter: '/marketing/newsletter',
    cmsPage: ({ slug }) => `/content/pages/${slug}`,
    cmsBlocks: '/content/blocks',
    pricingQuote: '/pricing/quote',
    pricingConvert: '/pricing/convert',
    shippingQuote: '/shipping/quote',
    shippingTrack: ({ orderNo }) => `/shipping/track/${orderNo}`,
    paymentsIntent: '/payments/intent',
    paymentsCapture: '/payments/capture',
    paymentsRefund: '/payments/refund',
    analyticsSnapshot: '/analytics/dashboard/snapshot',
    adminProducts: '/admin/catalog/products',
    adminStats: '/admin/stats',
    adminNote: '/admin/note',
    adminAuditLogs: '/admin/audit-logs'
  };

  constructor(private readonly http: HttpClient) {}

  get base(): string {
    return this.baseUrl;
  }

  path<K extends EndpointKey>(key: K, pathParams?: EndpointParams<K>): string {
    return this.resolveEndpointPath(key, pathParams);
  }

  url<K extends EndpointKey>(key: K, pathParams?: EndpointParams<K>, query?: QueryParams): string {
    const absolute = this.ensureAbsolute(this.resolveEndpointPath(key, pathParams));
    return query ? this.appendQuery(absolute, query) : absolute;
  }

  get<T>(config: ApiRequestConfig | string, options?: ApiHttpOptions): Observable<T> {
    if (typeof config === 'string') {
      return this.request<T>('GET', { url: config, options });
    }
    return this.request<T>('GET', config);
  }

  post<T>(config: ApiRequestConfig | string, body?: unknown, options?: ApiHttpOptions): Observable<T> {
    if (typeof config === 'string') {
      return this.request<T>('POST', { url: config, body, options });
    }
    return this.request<T>('POST', { ...config });
  }

  put<T>(config: ApiRequestConfig | string, body?: unknown, options?: ApiHttpOptions): Observable<T> {
    if (typeof config === 'string') {
      return this.request<T>('PUT', { url: config, body, options });
    }
    return this.request<T>('PUT', { ...config });
  }

  patch<T>(config: ApiRequestConfig | string, body?: unknown, options?: ApiHttpOptions): Observable<T> {
    if (typeof config === 'string') {
      return this.request<T>('PATCH', { url: config, body, options });
    }
    return this.request<T>('PATCH', { ...config });
  }

  delete<T>(config: ApiRequestConfig | string, options?: ApiHttpOptions): Observable<T> {
    if (typeof config === 'string') {
      return this.request<T>('DELETE', { url: config, options });
    }
    return this.request<T>('DELETE', config);
  }

  private request<T>(method: HttpMethod, config: ApiRequestConfig): Observable<T> {
    const url = this.ensureAbsolute(this.resolveConfigPath(config));
    const options = this.buildOptions(config);

    switch (method) {
      case 'GET':
        return this.http.get<T>(url, options);
      case 'POST':
        return this.http.post<T>(url, config.body, options);
      case 'PUT':
        return this.http.put<T>(url, config.body, options);
      case 'PATCH':
        return this.http.patch<T>(url, config.body, options);
      case 'DELETE':
        return this.http.delete<T>(url, { ...options, body: config.body });
      default:
        throw new Error(`Unsupported HTTP method: ${method}`);
    }
  }

  private resolveConfigPath(config: ApiRequestConfig): string {
    if (config.url) {
      return config.url;
    }
    if (config.key) {
      return this.resolveEndpointPath(config.key, config.pathParams);
    }
    throw new Error('ApiService request requires either a key or a url property.');
  }

  private resolveEndpointPath<K extends EndpointKey>(key: K, params?: EndpointParams<K>): string {
    const endpoint = this.endpoints[key];
    if (typeof endpoint === 'string') {
      return endpoint;
    }
    if (params == null) {
      throw new Error(`Endpoint "${String(key)}" expects path parameters.`);
    }
    const typedParams = params as EndpointParamMap<K>;
    const fn = endpoint as unknown as EndpointFn<EndpointParamMap<K>>;
    return fn(typedParams);
  }

  private buildOptions(config: ApiRequestConfig): ApiHttpOptions {
    const options: ApiHttpOptions = { ...(config.options ?? {}) };

    if (config.query) {
      const queryParams = this.toHttpParams(config.query);
      if (options.params) {
        const existing = options.params instanceof HttpParams
          ? options.params
          : new HttpParams({ fromObject: this.normalizeParamObject(options.params) });
        options.params = this.mergeHttpParams(existing, queryParams);
      } else {
        options.params = queryParams;
      }
    }

    return options;
  }

  private toHttpParams(query: QueryParams): HttpParams {
    let params = new HttpParams();
    Object.entries(query).forEach(([key, value]) => {
      if (value === undefined || value === null) {
        return;
      }
      if (Array.isArray(value)) {
        value.forEach(item => {
          if (item === undefined || item === null) {
            return;
          }
          params = params.append(key, String(item));
        });
      } else {
        params = params.append(key, String(value));
      }
    });
    return params;
  }

  private mergeHttpParams(target: HttpParams, addition: HttpParams): HttpParams {
    let merged = target;
    addition.keys().forEach(key => {
      const values = addition.getAll(key) ?? [];
      values.forEach(value => {
        merged = merged.append(key, value);
      });
    });
    return merged;
  }

  private normalizeParamObject(params: HttpParamRecord): Record<string, string | string[]> {
    const normalized: Record<string, string | string[]> = {};
    Object.entries(params).forEach(([key, value]) => {
      if (Array.isArray(value)) {
        normalized[key] = value.map(item => String(item));
      } else {
        normalized[key] = String(value);
      }
    });
    return normalized;
  }

  private appendQuery(url: string, query: QueryParams): string {
    const queryString = this.buildQueryString(query);
    if (!queryString) {
      return url;
    }
    const separator = url.includes('?') ? '&' : '?';
    return `${url}${separator}${queryString}`;
  }

  private buildQueryString(query: QueryParams): string {
    const searchParams = new URLSearchParams();
    Object.entries(query).forEach(([key, value]) => {
      if (value === undefined || value === null) {
        return;
      }
      if (Array.isArray(value)) {
        value.forEach(item => {
          if (item === undefined || item === null) {
            return;
          }
          searchParams.append(key, String(item));
        });
      } else {
        searchParams.append(key, String(value));
      }
    });
    return searchParams.toString();
  }

  private ensureAbsolute(pathOrUrl: string): string {
    if (/^https?:\/\//i.test(pathOrUrl)) {
      return pathOrUrl;
    }

    const trimmed = pathOrUrl.replace(/^\/+/g, '');

    if (!this.baseUrl) {
      return trimmed ? `/${trimmed}` : '';
    }

    return trimmed ? `${this.baseUrl}/${trimmed}` : this.baseUrl;
  }

  private sanitizeBaseUrl(url?: string): string {
    if (!url) {
      return '';
    }
    return url.replace(/\/+$/, '');
  }
}










