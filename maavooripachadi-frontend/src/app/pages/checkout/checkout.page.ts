import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, Observable, combineLatest, of } from 'rxjs';
import { catchError, distinctUntilChanged, finalize, map, shareReplay, startWith, switchMap } from 'rxjs/operators';
import { CartItem, CartService, CartSummary } from '../../core/services/cart.service';
import {
  CheckoutPayload,
  CheckoutService,
  OrderResponse,
  PaymentIntent,
  PriceQuote,
  PriceQuotePayload
} from '../../core/services/checkout.service';
import { PricePipe } from '../../shared/pipes/price.pipe';
import { environment } from '../../../environments/environment';

type PaymentMethod = 'ONLINE' | 'COD';

interface CheckoutItemViewModel {
  variantId: number;
  title: string;
  qty: number;
  unitPrice: number;
  lineTotal: number;
  imageUrl?: string | null;
}

interface CheckoutSummaryViewModel {
  subtotal: number;
  subtotalPaise: number;
  shipping: number;
  shippingPaise: number;
  discount: number;
  discountPaise: number;
  tax: number;
  taxPaise: number;
  total: number;
  totalPaise: number;
  quoteStatus: QuoteState['status'];
  quoteError?: string | null;
}

interface CheckoutViewModel {
  hasItems: boolean;
  itemsCount: number;
  items: CheckoutItemViewModel[];
  summary: CheckoutSummaryViewModel;
  codAvailable: boolean;
}

interface QuoteState {
  status: 'idle' | 'loading' | 'success' | 'error';
  quote: PriceQuote | null;
  error?: string | null;
}

interface QuoteContext {
  signature: string;
  payload: PriceQuotePayload;
}

interface RazorpaySuccessResponse {
  razorpay_payment_id: string;
  razorpay_order_id: string;
  razorpay_signature: string;
}

interface CheckoutStatus {
  kind: 'idle' | 'info' | 'error';
  message: string | null;
}

interface CheckoutUiState extends CheckoutViewModel {
  placingOrder: boolean;
  status: CheckoutStatus;
  lastOrderNo: string | null;
  deliveryMessage: string;
  hasValidPincode: boolean;
  shipPincode: string;
}

const PINCODE_REGEX = /^[1-9][0-9]{5}$/;
const FREE_SHIPPING_THRESHOLD_PAISE = 99900;
const STANDARD_SHIPPING_PAISE = 7900;
const COD_LIMIT_PAISE = 200000;
const COD_LIMIT_RUPEES = COD_LIMIT_PAISE / 100;
const DEFAULT_COUNTRY = 'India';
const IDLE_QUOTE_STATE: QuoteState = { status: 'idle', quote: null };

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [NgIf, NgFor, AsyncPipe, RouterLink, ReactiveFormsModule, PricePipe],
  templateUrl: './checkout.page.html',
  styleUrls: ['./checkout.page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CheckoutPage {
  private readonly cart = inject(CartService);
  private readonly checkout = inject(CheckoutService);
  private readonly fb = inject(FormBuilder);

  readonly states: string[] = [
    'Andhra Pradesh',
    'Telangana',
    'Karnataka',
    'Tamil Nadu',
    'Kerala',
    'Maharashtra',
    'Goa',
    'Delhi',
    'Gujarat',
    'West Bengal',
    'Madhya Pradesh',
    'Uttar Pradesh',
    'Rajasthan',
    'Odisha',
    'Bihar',
    'Assam',
    'Chhattisgarh',
    'Haryana',
    'Himachal Pradesh',
    'Jammu & Kashmir',
    'Jharkhand',
    'Manipur',
    'Meghalaya',
    'Mizoram',
    'Nagaland',
    'Punjab',
    'Sikkim',
    'Tripura',
    'Uttarakhand'
  ];

  readonly paymentMethods: Array<{ value: PaymentMethod; label: string; description: string }> = [
    {
      value: 'ONLINE',
      label: 'Pay online (UPI / Cards / Netbanking)',
      description: 'Secure Razorpay checkout for instant confirmation.'
    },
    {
      value: 'COD',
      label: 'Cash on delivery',
      description: `Available for orders up to INR ${COD_LIMIT_RUPEES.toFixed(0)}.`
    }
  ];

  readonly checkoutForm = this.fb.nonNullable.group({
    contactName: ['', [Validators.required, Validators.minLength(3)]],
    contactEmail: ['', [Validators.required, Validators.email]],
    contactPhone: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],

    shipName: [''],
    shipPhone: [''],
    shipLine1: ['', [Validators.required, Validators.minLength(5)]],
    shipLine2: [''],
    shipCity: ['', [Validators.required]],
    shipState: ['Telangana', [Validators.required]],
    shipPincode: ['', [Validators.required, Validators.pattern(PINCODE_REGEX)]],

    couponCode: [''],
    notes: [''],
    paymentMethod: ['ONLINE' as PaymentMethod]
  });

  readonly controls = this.checkoutForm.controls;
  readonly codLimitRupees = COD_LIMIT_RUPEES;

  private readonly placingSubject = new BehaviorSubject<boolean>(false);
  private readonly statusSubject = new BehaviorSubject<CheckoutStatus>({ kind: 'idle', message: null });
  private readonly lastOrderIdSubject = new BehaviorSubject<string | null>(null);
  private razorpayLoader?: Promise<void>;

  readonly placing$ = this.placingSubject.asObservable();
  readonly status$ = this.statusSubject.asObservable();
  readonly lastOrderId$ = this.lastOrderIdSubject.asObservable();

  private readonly shipPincode$ = this.controls.shipPincode.valueChanges.pipe(
    startWith(this.controls.shipPincode.value),
    map((value) => value?.trim() ?? '')
  );
  private readonly couponCode$ = this.controls.couponCode.valueChanges.pipe(
    startWith(this.controls.couponCode.value),
    map((value) => value?.trim() ?? '')
  );
  private readonly shipState$ = this.controls.shipState.valueChanges.pipe(
    startWith(this.controls.shipState.value),
    map((value) => value ?? '')
  );

  private readonly quote$ = combineLatest<[CartSummary, string, string]>([
    this.cart.cart$,
    this.shipPincode$,
    this.couponCode$
  ]).pipe(
    map(([cart, pincode, coupon]) => this.createQuoteContext(cart, pincode, coupon)),
    distinctUntilChanged((prev, curr) => prev?.signature === curr?.signature),
    switchMap((context): Observable<QuoteState> => {
      if (!context) {
        return of(IDLE_QUOTE_STATE);
      }
      return this.checkout.quotePricing(context.payload).pipe(
        map((quote) => ({
          status: 'success' as const,
          quote
        })),
        startWith<QuoteState>({ status: 'loading', quote: null }),
        catchError((error) => {
          console.error('Failed to refresh checkout pricing', error);
          return of<QuoteState>({
            status: 'error',
            quote: null,
            error: error?.error?.message ?? 'Unable to refresh pricing right now.'
          });
        })
      );
    }),
    startWith(IDLE_QUOTE_STATE),
    shareReplay(1)
  );

  private readonly vm$ = combineLatest<[CartSummary, QuoteState]>([
    this.cart.cart$,
    this.quote$
  ]).pipe(
    map(([cart, quoteState]) => this.buildViewModel(cart, quoteState)),
    shareReplay(1)
  );

  readonly state$ = combineLatest<[CheckoutViewModel, boolean, CheckoutStatus, string | null, string, string]>([
    this.vm$,
    this.placing$,
    this.status$,
    this.lastOrderId$,
    this.shipState$,
    this.shipPincode$
  ]).pipe(
    map(([vm, placing, status, lastOrderNo, shipState, shipPincode]) => {
      const normalizedPincode = shipPincode ?? '';
      return {
        ...vm,
        placingOrder: placing,
        status,
        lastOrderNo,
        deliveryMessage: this.estimateDelivery(shipState),
        hasValidPincode: PINCODE_REGEX.test(normalizedPincode),
        shipPincode: normalizedPincode
      } as CheckoutUiState;
    }),
    shareReplay(1)
  );

  useContactForShipping(): void {
    this.checkoutForm.patchValue({
      shipName: this.controls.contactName.value,
      shipPhone: this.controls.contactPhone.value
    });
  }

  dismissStatus(): void {
    this.statusSubject.next({ kind: 'idle', message: null });
  }

  placeOrder(): void {
    const cartSnapshot = this.cart.getCartSnapshot();
    if (!cartSnapshot.items.length) {
      this.statusSubject.next({
        kind: 'error',
        message: 'Your cart is empty. Add a few pickles to continue.'
      });
      return;
    }

    if (this.checkoutForm.invalid) {
      this.checkoutForm.markAllAsTouched();
      this.statusSubject.next({
        kind: 'error',
        message: 'Please review the highlighted fields before placing your order.'
      });
      return;
    }

    if (this.placingSubject.value) {
      return;
    }

    this.placingSubject.next(true);
    this.statusSubject.next({ kind: 'idle', message: null });
    this.lastOrderIdSubject.next(null);

    const raw = this.checkoutForm.getRawValue();
    const paymentMethod = raw.paymentMethod;
    const shipName = raw.shipName.trim() || raw.contactName.trim();
    const shipPhone = raw.shipPhone.trim() || raw.contactPhone.trim();
    const coupon = raw.couponCode.trim();
    const notes = raw.notes.trim();

    const payload: CheckoutPayload = {
      sessionId: this.cart.getSessionId(),
      customerName: raw.contactName.trim(),
      customerEmail: raw.contactEmail.trim(),
      customerPhone: raw.contactPhone.trim(),
      shipName,
      shipPhone,
      shipLine1: raw.shipLine1.trim(),
      shipLine2: raw.shipLine2.trim() || undefined,
      shipCity: raw.shipCity.trim(),
      shipState: raw.shipState.trim(),
      shipPincode: raw.shipPincode.trim(),
      shipCountry: DEFAULT_COUNTRY,
      couponCode: coupon ? coupon.toUpperCase() : undefined,
      notes: notes || undefined,
      paymentGateway: paymentMethod === 'COD' ? 'COD' : 'RAZORPAY'
    };

    this.checkout
      .checkout(payload)
      .pipe(
        catchError((error) => {
          console.error('Checkout failed', error);
          const message = error?.error?.message ?? 'We could not place your order right now. Please try again.';
          this.statusSubject.next({ kind: 'error', message });
          return of(null);
        }),
        finalize(() => this.placingSubject.next(false))
      )
      .subscribe((order) => {
        if (!order) {
          return;
        }
        if (paymentMethod === 'COD') {
          this.lastOrderIdSubject.next(order.orderNo);
          const amountFormatted = this.toRupees(order.totalPaise).toLocaleString('en-IN', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
          });
          this.statusSubject.next({
            kind: 'info',
            message: `Order ${order.orderNo} confirmed. Please keep INR ${amountFormatted} ready for Cash on Delivery.`
          });
          this.clearCartAfterSuccess();
          return;
        }

        this.initiateOnlinePayment(order, payload);
      });
  }

  private initiateOnlinePayment(order: OrderResponse, payload: CheckoutPayload): void {
    this.statusSubject.next({
      kind: 'info',
      message: 'Redirecting to secure payment...'
    });

    this.checkout
      .createPaymentIntent({
        orderNo: order.orderNo,
        amountPaise: order.totalPaise,
        gateway: 'RAZORPAY',
        currency: order.currency ?? 'INR'
      })
      .pipe(
        catchError((error) => {
          console.error('Failed to create payment intent', error);
          const message =
            error?.error?.message ??
            'We could not start the online payment. Please try again or choose Cash on Delivery.';
          this.statusSubject.next({
            kind: 'error',
            message
          });
          return of(null);
        })
      )
      .subscribe((intent) => {
        if (!intent) {
          return;
        }
        this.openRazorpayCheckout(order, payload, intent);
      });
  }

  private openRazorpayCheckout(order: OrderResponse, payload: CheckoutPayload, intent: PaymentIntent): void {
    const key = environment.payments?.razorpayKey?.trim();
    if (!key) {
      this.statusSubject.next({
        kind: 'error',
        message: 'Online payments are unavailable right now. Please contact support or choose Cash on Delivery.'
      });
      return;
    }

    this.loadRazorpayScript()
      .then(() => {
        const RazorpayCtor = (window as any).Razorpay;
        if (!RazorpayCtor) {
          this.statusSubject.next({
            kind: 'error',
            message: 'Unable to open Razorpay checkout. Please refresh the page and try again.'
          });
          return;
        }

        const options = {
          key,
          amount: intent.amountPaise,
          currency: intent.currency ?? order.currency ?? 'INR',
          name: environment.site.name,
          description: `Order ${order.orderNo}`,
          order_id: intent.gatewayOrderId,
          prefill: {
            name: payload.customerName,
            email: payload.customerEmail,
            contact: payload.customerPhone
          },
          notes: {
            orderNo: order.orderNo
          },
          theme: { color: '#f97316' },
          handler: (response: RazorpaySuccessResponse) => {
            this.capturePayment(order.orderNo, response.razorpay_payment_id, response.razorpay_signature);
          },
          modal: {
            ondismiss: () => {
              this.statusSubject.next({
                kind: 'info',
                message: 'Payment window closed. You can try online payment again or switch to Cash on Delivery.'
              });
            }
          }
        };

        const razorpay = new RazorpayCtor(options);
        if (typeof razorpay.on === 'function') {
          razorpay.on('payment.failed', (event: { error?: { description?: string } }) => {
            console.error('Razorpay payment failed', event);
            const description = event?.error?.description ?? 'Payment was not completed.';
            this.statusSubject.next({
              kind: 'error',
              message: `${description} Please try again or choose Cash on Delivery.`
            });
          });
        }

        razorpay.open();
      })
      .catch((error) => {
        console.error('Failed to load Razorpay checkout', error);
        this.statusSubject.next({
          kind: 'error',
          message: 'Unable to load the payment gateway. Please check your connection and try again.'
        });
      });
  }

  private capturePayment(orderNo: string, paymentId: string, signature: string): void {
    this.statusSubject.next({
      kind: 'info',
      message: 'Confirming your payment with Razorpay...'
    });

    this.checkout
      .capturePayment({
        orderNo,
        gatewayPaymentId: paymentId,
        gatewaySignature: signature
      })
      .pipe(
        catchError((error) => {
          console.error('Payment capture failed', error);
          this.statusSubject.next({
            kind: 'error',
            message: `We received a response from Razorpay but could not verify it. Please contact support with payment ID ${paymentId}.`
          });
          return of(null);
        })
      )
      .subscribe((attempt) => {
        if (!attempt) {
          return;
        }
        const amountFormatted = this.toRupees(attempt.amountPaise).toLocaleString('en-IN', {
          minimumFractionDigits: 2,
          maximumFractionDigits: 2
        });
        this.statusSubject.next({
          kind: 'info',
          message: `Payment received! Order ${orderNo} is confirmed. Receipt for INR ${amountFormatted} is on its way to your inbox.`
        });
        this.lastOrderIdSubject.next(orderNo);
        this.clearCartAfterSuccess();
      });
  }

  private loadRazorpayScript(): Promise<void> {
    if (typeof window === 'undefined' || typeof document === 'undefined') {
      return Promise.reject(new Error('Razorpay is only available in the browser.'));
    }
    if ((window as any).Razorpay) {
      return Promise.resolve();
    }
    if (this.razorpayLoader) {
      return this.razorpayLoader;
    }

    this.razorpayLoader = new Promise<void>((resolve, reject) => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.async = true;
      script.onload = () => resolve();
      script.onerror = () => reject(new Error('Failed to load Razorpay SDK'));
      document.body.appendChild(script);
    }).catch((error) => {
      this.razorpayLoader = undefined;
      throw error;
    });

    return this.razorpayLoader;
  }

  private clearCartAfterSuccess(): void {
    this.cart
      .clear()
      .pipe(
        catchError((err) => {
          console.warn('Failed to clear cart after checkout', err);
          return of(null);
        })
      )
      .subscribe();
  }

  private buildViewModel(cart: CartSummary, quoteState: QuoteState): CheckoutViewModel {
    const items = cart.items.map((item) => this.toItemViewModel(item));
    const subtotalPaise = cart.subtotalPaise ?? this.computeSubtotal(cart.items);
    const discountPaise = quoteState.quote?.discountPaise ?? 0;
    const shippingPaise = this.resolveShipping(subtotalPaise, quoteState);
    const taxPaise = quoteState.quote?.taxPaise ?? 0;
    const totalPaise =
      quoteState.quote?.totalPaise ?? Math.max(0, subtotalPaise - discountPaise + shippingPaise + taxPaise);
    const codAvailable = totalPaise <= COD_LIMIT_PAISE;

    return {
      hasItems: items.length > 0,
      itemsCount: cart.itemsCount ?? items.length,
      items,
      codAvailable,
      summary: {
        subtotal: this.toRupees(subtotalPaise),
        subtotalPaise,
        discount: this.toRupees(discountPaise),
        discountPaise,
        shipping: this.toRupees(shippingPaise),
        shippingPaise,
        tax: this.toRupees(taxPaise),
        taxPaise,
        total: this.toRupees(totalPaise),
        totalPaise,
        quoteStatus: quoteState.status,
        quoteError: quoteState.error ?? null
      }
    };
  }

  private resolveShipping(subtotalPaise: number, quoteState: QuoteState): number {
    if (quoteState.quote?.shippingPaise !== undefined) {
      return quoteState.quote.shippingPaise;
    }
    return subtotalPaise >= FREE_SHIPPING_THRESHOLD_PAISE ? 0 : STANDARD_SHIPPING_PAISE;
  }

  private toItemViewModel(item: CartItem): CheckoutItemViewModel {
    const lineTotalPaise = item.qty * item.unitPricePaise;
    return {
      variantId: item.variantId,
      title: item.title ?? 'Maavoori Pachadi',
      qty: item.qty,
      unitPrice: this.toRupees(item.unitPricePaise),
      lineTotal: this.toRupees(lineTotalPaise),
      imageUrl: item.imageUrl ?? null
    };
  }

  private createQuoteContext(cart: CartSummary, pincode: string, coupon: string): QuoteContext | null {
    if (!cart.items.length || !PINCODE_REGEX.test(pincode.trim())) {
      return null;
    }

    const normalizedCoupon = coupon.trim() ? coupon.trim().toUpperCase() : undefined;
    const items = cart.items.map((item) => ({
      variantId: item.variantId,
      qty: item.qty
    }));

    const signature = [
      pincode.trim(),
      normalizedCoupon ?? '',
      items
        .map((item) => `${item.variantId}:${item.qty}`)
        .sort()
        .join(',')
    ].join('|');

    const payload: PriceQuotePayload = {
      items,
      pincode: pincode.trim(),
      couponCode: normalizedCoupon
    };

    return { signature, payload };
  }

  private estimateDelivery(state: string): string {
    const fastTrackStates = new Set(['Telangana', 'Andhra Pradesh', 'Karnataka', 'Tamil Nadu']);
    if (!state) {
      return 'Doorstep delivery in 3-5 business days across India.';
    }
    if (fastTrackStates.has(state)) {
      return 'Delivery in 1-3 days for South India metros.';
    }
    return 'Delivery in 3-6 business days via trusted courier partners.';
  }

  private computeSubtotal(items: CartItem[]): number {
    return items.reduce((total, item) => total + item.qty * item.unitPricePaise, 0);
  }

  private toRupees(paise: number): number {
    return Math.round(paise) / 100;
  }
}
