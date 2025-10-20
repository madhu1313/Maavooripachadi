import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, Observable, combineLatest, of } from 'rxjs';
import { map, shareReplay } from 'rxjs/operators';
import { CatalogService } from '../../core/services/catalog.service';
import { CartService, CartSummary } from '../../core/services/cart.service';
import { Product } from '../../core/models/storefront.models';
import { ProductCardComponent } from '../../shared/components/product-card/product-card.component';
import { PricePipe } from '../../shared/pipes/price.pipe';

interface OrderHighlight {
  id: string;
  title: string;
  description: string;
  icon: string;
}

interface TimelineStep {
  id: string;
  title: string;
  description: string;
  status: 'completed' | 'upcoming';
}

interface SuccessViewModel {
  reference: string | null;
  summary: CartSummary | null;
  total: number;
  itemsCount: number;
  highlights: OrderHighlight[];
  timeline: TimelineStep[];
  recommended: Product[];
  hasRecommended: boolean;
  whatsappUrl: string;
}

@Component({
  selector: 'app-success',
  standalone: true,
  imports: [NgIf, NgFor, NgClass, AsyncPipe, RouterLink, ProductCardComponent, PricePipe],
  templateUrl: './success.page.html',
  styleUrls: ['./success.page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SuccessPage {
  private readonly cart: CartService = inject(CartService);
  private readonly catalog: CatalogService = inject(CatalogService);

  private readonly orderRefSubject = new BehaviorSubject<string | null>(null);
  readonly orderReference$ = this.orderRefSubject.asObservable();

  readonly highlights: OrderHighlight[] = [
    {
      id: 'curing',
      title: 'Fresh batch queued',
      description: 'Your jars join the next micro batch for a final quality check and sealing.',
      icon: 'auto_fix_high'
    },
    {
      id: 'dispatch',
      title: 'Dispatch within 24 hrs',
      description: 'Maavoori team packs each order in leak-proof jars and books a temperature-safe courier.',
      icon: 'local_shipping'
    },
    {
      id: 'support',
      title: 'Concierge support',
      description: 'Need pairing tips or subscription upgrades? Chat with us anytime on WhatsApp.',
      icon: 'support_agent'
    }
  ];

  readonly whatsappUrl = 'https://wa.me/918555859667?text=Hi%20Maavoori%20team,%20could%20you%20help%20me%20with%20my%20recent%20order%3F';

  private readonly timelineSteps: TimelineStep[] = [
    {
      id: 'confirmed',
      title: 'Order confirmed',
      description: 'We have the green signal to start prepping your jars.',
      status: 'completed'
    },
    {
      id: 'curing',
      title: 'Batches curing',
      description: 'Pickles rest under the sun and are sealed with cold-pressed oils.',
      status: 'upcoming'
    },
    {
      id: 'packed',
      title: 'Packed & dispatched',
      description: 'Leak-proof jars are packed with ice gel and handed to our delivery partner.',
      status: 'upcoming'
    },
    {
      id: 'delivered',
      title: 'Delivered',
      description: 'Your Maavoori favourites arrive ready to enjoy.',
      status: 'upcoming'
    }
  ];

  private readonly cartSnapshot$ = of(this.cart.getCartSnapshot()).pipe(shareReplay(1));

  private readonly recommended$ = this.catalog.bestSellers(4).pipe(shareReplay(1));

  readonly vm$: Observable<SuccessViewModel> = combineLatest([
    this.orderReference$,
    this.cartSnapshot$,
    this.recommended$
  ]).pipe(
    map(([reference, summary, recommended]) => {
      const itemsCount = summary?.itemsCount ?? 0;
      const total = summary?.subtotalPaise ? Math.round(summary.subtotalPaise) / 100 : 0;

      const timeline: TimelineStep[] = this.timelineSteps.map((step, index) => ({
        ...step,
        status: index === 0 ? 'completed' : 'upcoming'
      }));

      return {
        reference,
        summary,
        total,
        itemsCount,
        highlights: this.highlights,
        timeline,
        recommended,
        hasRecommended: recommended.length > 0,
        whatsappUrl: this.whatsappUrl
      };
    })
  );

  constructor() {
    const maybeRef = localStorage.getItem('maavoori.latestOrderRef');
    this.orderRefSubject.next(maybeRef);
    this.cart.clear().subscribe();
  }
}
