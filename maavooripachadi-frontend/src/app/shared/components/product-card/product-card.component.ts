import { NgClass, NgFor, NgIf } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Product } from '../../../core/models/storefront.models';
import { PricePipe } from '../../pipes/price.pipe';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [RouterLink, PricePipe, NgIf, NgFor, NgClass],
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.css']
})
export class ProductCardComponent {
  @Input({ required: true }) product!: Product;
  @Input() compact = false;

  get primaryBadge(): string | undefined {
    if (this.product.badge) {
      return this.product.badge;
    }
    if (this.product.isBestSeller) {
      return 'Best seller';
    }
    if (this.product.isNew) {
      return 'New arrival';
    }
    return undefined;
  }

  get secondaryBadge(): string | undefined {
    if (this.product.tags?.includes('limited')) {
      return 'Limited batch';
    }
    if (this.product.tags?.includes('spicy')) {
      return 'Spicy';
    }
    return undefined;
  }

  get description(): string {
    return this.product.description?.trim() || 'Handcrafted in micro batches with heritage Maavoori recipes.';
  }

  get tags(): string[] {
    return (this.product.tags ?? []).slice(0, 3);
  }
}
