import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export type OrderStatus =
  | 'DRAFT'
  | 'PENDING'
  | 'PAID'
  | 'PACKED'
  | 'SHIPPED'
  | 'DELIVERED'
  | 'CANCELLED'
  | 'REFUNDED';

export type PaymentStatus =
  | 'NOT_APPLICABLE'
  | 'PENDING'
  | 'AUTHORIZED'
  | 'CAPTURED'
  | 'FAILED'
  | 'REFUNDED';

export type PaymentGateway = 'RAZORPAY' | 'CASHFREE';
export type PaymentAttemptStatus =
  | 'CREATED'
  | 'PENDING'
  | 'AUTHORIZED'
  | 'CAPTURED'
  | 'FAILED'
  | 'CANCELLED';

export type PaymentRefundStatus = 'REQUESTED' | 'PROCESSED' | 'FAILED';

export type ShippingCarrier = 'SHIPROCKET' | 'DTDC' | 'BLUEDART' | 'XPRESSBEES' | 'EKART';
export type ShippingServiceLevel = 'STANDARD' | 'EXPRESS' | 'SAME_DAY';
export type ShipmentStatus =
  | 'DRAFT'
  | 'RATE_REQUESTED'
  | 'LABEL_PURCHASED'
  | 'DISPATCHED'
  | 'IN_TRANSIT'
  | 'DELIVERED'
  | 'CANCELLED'
  | 'RETURNED';
export type LabelStatus = 'NONE' | 'REQUESTED' | 'READY' | 'FAILED' | 'CANCELLED';

export interface CheckoutPayload {
  sessionId: string;
  customerEmail: string;
  customerPhone: string;
  customerName: string;
  shipName: string;
  shipPhone: string;
  shipLine1: string;
  shipLine2?: string | null;
  shipCity: string;
  shipState: string;
  shipPincode: string;
  shipCountry?: string;
  couponCode?: string | null;
  notes?: string | null;
  paymentGateway?: PaymentGateway | string;
}

export interface OrderItem {
  id?: number;
  variantId?: number;
  sku?: string;
  title?: string;
  qty: number;
  unitPricePaise: number;
  lineTotalPaise: number;
}

export interface OrderResponse {
  orderNo: string;
  status: OrderStatus;
  paymentStatus: PaymentStatus;
  totalPaise: number;
  currency: string;
  items: OrderItem[];
}

export interface PriceQuoteItem {
  variantId: number;
  qty: number;
}

export interface PriceQuotePayload {
  items: PriceQuoteItem[];
  priceListName?: string;
  pincode: string;
  couponCode?: string;
  region?: string;
}

export interface PriceQuote {
  subtotalPaise: number;
  discountPaise: number;
  taxPaise: number;
  shippingPaise: number;
  totalPaise: number;
  currency: string;
}

export interface ConversionQuote {
  amount: number;
  currency: string;
}

export interface PaymentIntentRequest {
  orderNo: string;
  amountPaise: number;
  gateway?: PaymentGateway;
  currency?: string;
}

export interface PaymentIntent {
  gatewayOrderId: string;
  orderNo: string;
  amountPaise: number;
  currency: string;
  gateway: PaymentGateway | string;
}

export interface PaymentCaptureRequest {
  orderNo: string;
  gatewayPaymentId: string;
  gatewaySignature: string;
}

export interface PaymentAttempt {
  orderNo: string;
  gateway: PaymentGateway | string;
  status: PaymentAttemptStatus;
  amountPaise: number;
  currency: string;
  gatewayOrderId?: string;
  gatewayPaymentId?: string;
  gatewaySignature?: string;
  metaJson?: string;
}

export interface PaymentRefundRequest {
  orderNo: string;
  amountPaise: number;
  reason?: string;
}

export interface PaymentRefund {
  amountPaise: number;
  status: PaymentRefundStatus;
  gatewayRefundId?: string;
  reason?: string;
  attempt?: PaymentAttempt;
}

export interface ShippingQuotePayload {
  fromPincode?: string;
  toPincode: string;
  weightGrams: number;
  lengthCm?: number;
  widthCm?: number;
  heightCm?: number;
  serviceLevel?: ShippingServiceLevel;
}

export interface ShippingQuote {
  carrier: ShippingCarrier;
  serviceLevel: ShippingServiceLevel;
  amountPaise: number;
  currency: string;
}

export interface Shipment {
  orderNo: string;
  status: ShipmentStatus;
  carrier?: ShippingCarrier;
  serviceLevel: ShippingServiceLevel;
  trackingNo?: string;
  labelStatus: LabelStatus;
  labelUrl?: string;
  weightGrams?: number;
  lengthCm?: number;
  widthCm?: number;
  heightCm?: number;
  fromPincode?: string;
  toPincode?: string;
  toName?: string;
  toPhone?: string;
  toAddress1?: string;
  toAddress2?: string;
  toCity?: string;
  toState?: string;
  codPaise?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface TrackingEvent {
  id?: number;
  status?: string;
  location?: string;
  details?: string;
  createdAt?: string;
}

export interface ShipmentTracking {
  shipment: Shipment | null;
  events: TrackingEvent[];
}

@Injectable({ providedIn: 'root' })
export class CheckoutService {
  constructor(private readonly api: ApiService) {}

  checkout(payload: CheckoutPayload): Observable<OrderResponse> {
    return this.api.post<OrderResponse>({ key: 'checkout', body: payload });
  }

  getCheckout(orderNo: string): Observable<OrderResponse> {
    const safeOrderNo = encodeURIComponent(orderNo);
    return this.api.get<OrderResponse>(this.api.url('checkoutOrder', { orderNo: safeOrderNo }));
  }

  quotePricing(payload: PriceQuotePayload): Observable<PriceQuote> {
    return this.api.post<PriceQuote>({ key: 'pricingQuote', body: payload });
  }

  convertPricing(inrAmount: number, currency: string): Observable<ConversionQuote> {
    return this.api.get<ConversionQuote>({
      key: 'pricingConvert',
      query: { inr: inrAmount, to: currency }
    });
  }

  requestShippingQuote(payload: ShippingQuotePayload): Observable<ShippingQuote[]> {
    return this.api.post<ShippingQuote[]>({ key: 'shippingQuote', body: payload });
  }

  trackShipment(orderNo: string): Observable<ShipmentTracking> {
    const safeOrderNo = encodeURIComponent(orderNo);
    return this.api.get<ShipmentTracking>(this.api.url('shippingTrack', { orderNo: safeOrderNo }));
  }

  createPaymentIntent(payload: PaymentIntentRequest): Observable<PaymentIntent> {
    return this.api.post<PaymentIntent>({ key: 'paymentsIntent', body: payload });
  }

  capturePayment(payload: PaymentCaptureRequest): Observable<PaymentAttempt> {
    return this.api.post<PaymentAttempt>({ key: 'paymentsCapture', body: payload });
  }

  refundPayment(payload: PaymentRefundRequest): Observable<PaymentRefund> {
    return this.api.post<PaymentRefund>({ key: 'paymentsRefund', body: payload });
  }
}
