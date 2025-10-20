import { AsyncPipe, DatePipe, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BehaviorSubject, Observable, combineLatest, of } from 'rxjs';
import { catchError, finalize, map, startWith } from 'rxjs/operators';
import { CatalogService } from '../../core/services/catalog.service';
import { ShippingService } from '../../core/services/shipping.service';
import { ShipmentTracking, TrackingEvent } from '../../core/services/checkout.service';
import { Product } from '../../core/models/storefront.models';
import { ProductCardComponent } from '../../shared/components/product-card/product-card.component';

type TrackStatusKind = 'idle' | 'success' | 'error';

interface TrackStatus {
  kind: TrackStatusKind;
  message: string | null;
}

interface TimelineEvent {
  id: string;
  title: string;
  description: string;
  location?: string | null;
  timestamp?: string | null;
  status: 'completed' | 'current' | 'upcoming';
}

interface TrackingSummary {
  orderNo: string;
  courier?: string | null;
  trackingNo?: string | null;
  destination?: string | null;
  currentStatus?: string | null;
  updatedAt?: string | null;
}

interface TrackViewModel {
  hasTracking: boolean;
  loading: boolean;
  status: TrackStatus;
  summary: TrackingSummary | null;
  timeline: TimelineEvent[];
  recommended: Product[];
  hasRecommended: boolean;
}

@Component({
  selector: 'app-track',
  standalone: true,
  imports: [NgIf, NgFor, NgClass, AsyncPipe, ReactiveFormsModule, DatePipe, ProductCardComponent],
  templateUrl: './track.page.html',
  styleUrls: ['./track.page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TrackPage {
  private readonly fb: FormBuilder = inject(FormBuilder);
  private readonly shipping: ShippingService = inject(ShippingService);
  private readonly catalog: CatalogService = inject(CatalogService);

  private readonly trackingSubject = new BehaviorSubject<ShipmentTracking | null>(null);
  private readonly orderRefSubject = new BehaviorSubject<string | null>(this.readStoredOrder());
  private readonly loadingSubject = new BehaviorSubject<boolean>(false);
  private readonly statusSubject = new BehaviorSubject<TrackStatus>({ kind: 'idle', message: null });

  readonly tracking$ = this.trackingSubject.asObservable();
  readonly loading$ = this.loadingSubject.asObservable();
  readonly status$ = this.statusSubject.asObservable();
  readonly recommended$ = this.catalog.bestSellers(4);

  readonly trackForm = this.fb.nonNullable.group({
    orderNo: ['', [Validators.required, Validators.minLength(3)]],
    email: ['']
  });

  readonly vm$: Observable<TrackViewModel> = combineLatest([
    this.tracking$,
    this.loading$,
    this.status$,
    this.recommended$.pipe(startWith([] as Product[]))
  ]).pipe(
    map(([tracking, loading, status, recommended]) => {
      const summary = this.buildSummary(tracking);
      return {
        hasTracking: Boolean(tracking?.shipment),
        loading,
        status,
        summary,
        timeline: this.buildTimeline(tracking),
        recommended,
        hasRecommended: recommended.length > 0
      };
    })
  );

  constructor() {
    const storedOrder = this.orderRefSubject.value;
    if (storedOrder) {
      this.trackForm.patchValue({ orderNo: storedOrder });
      this.track(storedOrder, false);
    }
  }

  submit(): void {
    if (this.trackForm.invalid) {
      this.trackForm.markAllAsTouched();
      return;
    }
    const orderNo = this.trackForm.controls.orderNo.value.trim();
    this.track(orderNo);
  }

  dismissStatus(): void {
    this.statusSubject.next({ kind: 'idle', message: null });
  }

  private track(orderNo: string, persist = true): void {
    if (!orderNo) {
      return;
    }
    this.loadingSubject.next(true);
    this.statusSubject.next({ kind: 'idle', message: null });

    this.shipping
      .track(orderNo)
      .pipe(
        catchError((error) => {
          console.error('Failed to track shipment', error);
          this.statusSubject.next({
            kind: 'error',
            message: error?.error?.message ?? 'We could not find this order. Please double-check the order number.'
          });
          this.trackingSubject.next(null);
          return of(null);
        }),
        finalize(() => this.loadingSubject.next(false))
      )
      .subscribe((tracking) => {
        if (!tracking) {
          return;
        }
        this.trackingSubject.next(tracking);
        this.statusSubject.next({
          kind: 'success',
          message: 'Found your order! Scroll down for live updates.'
        });
        if (persist) {
          this.orderRefSubject.next(orderNo);
          localStorage.setItem('maavoori.latestOrderRef', orderNo);
        }
      });
  }

  private buildSummary(tracking: ShipmentTracking | null): TrackingSummary | null {
    if (!tracking?.shipment) {
      return null;
    }
    const shipment = tracking.shipment;
    return {
      orderNo: shipment.orderNo,
      courier: shipment.carrier ?? undefined,
      trackingNo: shipment.trackingNo ?? undefined,
      destination: this.buildDestination(shipment),
      currentStatus: shipment.status ?? undefined,
      updatedAt: shipment.updatedAt ?? shipment.createdAt ?? undefined
    };
  }

  private buildDestination(shipment: ShipmentTracking['shipment']): string | undefined {
    if (!shipment) {
      return undefined;
    }
    const parts = [shipment.toCity, shipment.toState, shipment.toPincode].filter(Boolean);
    return parts.join(', ') || undefined;
  }

  private buildTimeline(tracking: ShipmentTracking | null): TimelineEvent[] {
    const events = tracking?.events ?? [];
    if (!events.length) {
      return this.defaultTimeline();
    }
    return events.map((event, index) => this.mapEvent(event, index, events.length));
  }

  private mapEvent(event: TrackingEvent, index: number, total: number): TimelineEvent {
    const status = index === 0 ? 'completed' : index === total - 1 ? 'current' : 'completed';
    const title = event.status ?? 'Update';
    const description = event.details ?? 'Status update shared by courier partner.';
    return {
      id: event.id?.toString() ?? `${index}`,
      title,
      description,
      location: event.location,
      timestamp: event.createdAt,
      status
    };
  }

  private defaultTimeline(): TimelineEvent[] {
    return [
      {
        id: 'confirmed',
        title: 'Order confirmed',
        description: 'We are preparing your jars for dispatch.',
        status: 'current'
      },
      {
        id: 'courier',
        title: 'Handed to courier',
        description: 'Tracking updates will appear here once the courier scans the package.',
        status: 'upcoming'
      },
      {
        id: 'delivery',
        title: 'Out for delivery',
        description: 'Expect delivery soon. Our team ensures leak-proof packaging.',
        status: 'upcoming'
      }
    ];
  }

  private readStoredOrder(): string | null {
    if (typeof localStorage === 'undefined') {
      return null;
    }
    return localStorage.getItem('maavoori.latestOrderRef');
  }
}
