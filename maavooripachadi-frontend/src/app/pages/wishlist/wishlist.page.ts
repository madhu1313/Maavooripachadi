import { AsyncPipe, DatePipe, NgFor, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { WishlistService, WishlistItem } from '../../core/services/wishlist.service';
import { PricePipe } from '../../shared/pipes/price.pipe';
import { CatalogService } from '../../core/services/catalog.service';
import { CartService } from '../../core/services/cart.service';

@Component({
  selector: 'app-wishlist',
  standalone: true,
  templateUrl: './wishlist.page.html',
  styleUrls: ['./wishlist.page.css'],
  imports: [NgIf, NgFor, AsyncPipe, DatePipe, RouterLink, PricePipe]
})
export class WishlistPage {
  private readonly wishlist: WishlistService = inject(WishlistService);
  private readonly catalog: CatalogService = inject(CatalogService);
  private readonly cart: CartService = inject(CartService);

  readonly items$ = this.wishlist.items$;
  readonly count$ = this.wishlist.count$;
  statusMessage = '';
  private readonly processing = new Set<string>();

  remove(id: string): void {
    this.wishlist.remove(id);
    if (this.processing.has(id)) {
      this.processing.delete(id);
    }
    this.statusMessage = 'Removed from wishlist.';
  }

  clear(): void {
    this.wishlist.clear();
    this.processing.clear();
    this.statusMessage = 'Wishlist cleared.';
  }

  async moveToCart(item: WishlistItem): Promise<void> {
    if (this.processing.has(item.id)) {
      return;
    }
    this.processing.add(item.id);
    this.statusMessage = '';
    try {
      const detail = await firstValueFrom(this.catalog.getById(item.id));
      const variant =
        detail.variants.find((v) => v.inStock) ?? detail.variants[0];
      if (!variant) {
        throw new Error('This product is currently unavailable. Please try another item.');
      }
      const unitPricePaiseRaw = variant.pricePaise ?? detail.pricePaise ?? item.pricePaise ?? Math.round(item.price * 100);
      const unitPricePaise = Math.max(0, Math.round(unitPricePaiseRaw));
      if (!unitPricePaise) {
        throw new Error('Could not determine the product price. Please try again later.');
      }
      await firstValueFrom(this.cart.addItem(variant.id, 1, unitPricePaise));
      this.wishlist.remove(item.id);
      this.statusMessage = `${item.title} moved to your cart.`;
    } catch (error) {
      this.statusMessage =
        (error as Error)?.message ?? 'Could not move the product to cart right now.';
    } finally {
      this.processing.delete(item.id);
    }
  }

  isProcessing(id: string): boolean {
    return this.processing.has(id);
  }

  trackById(_index: number, item: WishlistItem): string {
    return item.id;
  }
}
