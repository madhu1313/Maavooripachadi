import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { Product } from '../models/storefront.models';

export interface WishlistItem {
  id: string;
  title: string;
  image: string;
  description: string;
  price: number;
  pricePaise: number;
  addedAt: string;
}

@Injectable({ providedIn: 'root' })
export class WishlistService {
  private readonly storageKey = 'maavoori.wishlist';
  private readonly itemsSubject = new BehaviorSubject<WishlistItem[]>(this.loadInitialState());

  readonly items$ = this.itemsSubject.asObservable();
  readonly count$ = this.items$.pipe(map((items) => items.length));

  add(product: Product): void {
    const items = this.itemsSubject.value;
    if (items.some((item) => item.id === product.id)) {
      return;
    }
    const next = [...items, this.toWishlistItem(product)];
    this.persist(next);
  }

  remove(id: string): void {
    const next = this.itemsSubject.value.filter((item) => item.id !== id);
    this.persist(next);
  }

  toggle(product: Product): void {
    if (this.has(product.id)) {
      this.remove(product.id);
    } else {
      this.add(product);
    }
  }

  clear(): void {
    this.persist([]);
  }

  has(id: string): boolean {
    return this.itemsSubject.value.some((item) => item.id === id);
  }

  private toWishlistItem(product: Product): WishlistItem {
    const rawDescription = (product.description ?? '').toString().trim();
    const description = rawDescription || 'Handcrafted in micro batches with heritage Maavoori recipes.';
    return {
      id: product.id,
      title: product.title,
      image: product.image,
      description,
      price: product.price,
      pricePaise: Math.max(0, Math.round((product as any).pricePaise ?? product.price * 100)),
      addedAt: new Date().toISOString()
    };
  }

  private loadInitialState(): WishlistItem[] {
    if (typeof localStorage === 'undefined') {
      return [];
    }
    try {
      const raw = localStorage.getItem(this.storageKey);
      if (!raw) {
        return [];
      }
      const parsed = JSON.parse(raw);
      if (!Array.isArray(parsed)) {
        return [];
      }
      return parsed
        .filter((item) => typeof item?.id === 'string' && typeof item?.title === 'string')
        .map((item) => ({
          ...item,
          description:
            typeof item?.description === 'string' && item.description.trim().length
              ? item.description
              : 'Handcrafted in micro batches with heritage Maavoori recipes.'
        }));
    } catch {
      return [];
    }
  }

  private persist(items: WishlistItem[]): void {
    this.itemsSubject.next(items);
    if (typeof localStorage === 'undefined') {
      return;
    }
    try {
      localStorage.setItem(this.storageKey, JSON.stringify(items));
    } catch {
      // noop – storage might be unavailable (private browsing etc.)
    }
  }
}
