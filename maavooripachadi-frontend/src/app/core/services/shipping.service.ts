import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { ShippingQuotePayload, ShippingQuote, ShipmentTracking } from './checkout.service';

@Injectable({ providedIn: 'root' })
export class ShippingService {
  constructor(private readonly api: ApiService) {}

  quote(payload: ShippingQuotePayload): Observable<ShippingQuote[]> {
    return this.api.post<ShippingQuote[]>({ key: 'shippingQuote', body: payload });
  }

  track(orderNo: string): Observable<ShipmentTracking> {
    const safeOrderNo = encodeURIComponent(orderNo);
    return this.api.get<ShipmentTracking>(this.api.url('shippingTrack', { orderNo: safeOrderNo }));
  }
}
