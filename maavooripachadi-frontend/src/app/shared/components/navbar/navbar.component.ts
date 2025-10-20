import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { debounceTime, distinctUntilChanged, map, startWith, switchMap } from 'rxjs';
import { CatalogService } from '../../../core/services/catalog.service';
import { StorefrontService } from '../../../core/services/storefront.service';
import { NavigationLink, Product } from '../../../core/models/storefront.models';
import { PricePipe } from '../../pipes/price.pipe';
import { CartService } from '../../../core/services/cart.service';
import { WishlistService } from '../../../core/services/wishlist.service';

interface Announcement {
  id: string;
  message: string;
  icon: string;
}

interface NavQuickLink {
  label: string;
  path: string;
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [
    NgFor,
    NgIf,
    NgClass,
    AsyncPipe,
    ReactiveFormsModule,
    RouterLink,
    RouterLinkActive,
    PricePipe
  ],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent {
  private readonly storefront: StorefrontService = inject(StorefrontService);
  private readonly catalog: CatalogService = inject(CatalogService);
  private readonly cart: CartService = inject(CartService);
  private readonly wishlist: WishlistService = inject(WishlistService);

  readonly announcements: Announcement[] = [
    { id: 'no-preservatives', message: 'No preservatives • HACCP certified • Made fresh every fortnight', icon: 'verified' },
    { id: 'shipping', message: 'Pan-India delivery in 3-5 days • Free Hyderabad delivery on orders over ₹999', icon: 'local_shipping' }
  ];

  readonly hotline = { label: 'Concierge', value: '+91 85558 59667', icon: 'support_agent', href: 'https://wa.me/918555859667' };
  readonly email = { label: 'Email', value: 'support@maavooripachadi.com', icon: 'mail', href: 'mailto:support@maavooripachadi.com' };

  readonly quickLinks: NavQuickLink[] = [
    { label: 'Track Order', path: '/track' },
    { label: 'Support', path: '/support' },
    { label: 'Recipes', path: '/recipes' },
    { label: 'Gifting', path: '/contact' }
  ];

  readonly trendingSearches: string[] = [
    'Mango Avakai',
    'Gongura Mutton',
    'Sweet Avakai',
    'Millet Murukulu',
    'Festival Hampers'
  ];

  readonly links$ = this.storefront.getNavigation();
  readonly highlightedProducts$ = this.catalog.bestSellers(6);
  readonly cartCount$ = this.cart.cart$.pipe(map((summary) => summary.itemsCount ?? 0));
  readonly wishlistCount$ = this.wishlist.count$;

  menuOpen = false;
  searchOpen = false;

  readonly searchControl = new FormControl('', { nonNullable: true });
  readonly searchResults$ = this.searchControl.valueChanges.pipe(
    startWith(''),
    debounceTime(200),
    distinctUntilChanged(),
    switchMap((term) => (term.trim().length ? this.catalog.search(term.trim()) : this.catalog.bestSellers(6)))
  );

  toggleMenu(): void {
    this.menuOpen = !this.menuOpen;
  }

  closeMenu(): void {
    this.menuOpen = false;
  }

  toggleSearch(): void {
    this.searchOpen = !this.searchOpen;
    if (!this.searchOpen) {
      this.searchControl.setValue('', { emitEvent: true });
    }
  }

  openSearchWith(term: string): void {
    this.searchOpen = true;
    this.searchControl.setValue(term, { emitEvent: true });
  }

  trackByLink(_index: number, item: NavigationLink): string {
    return item.label;
  }

  trackByProduct(_index: number, item: Product): string {
    return item.id;
  }
}
