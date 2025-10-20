import { AsyncPipe, NgIf } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { AuthService, LoginRequest, ProfileResponse, RegisterRequest } from '../../core/services/auth.service';
import { SupportService, CreateTicketPayload, TicketChannel, TicketPriority } from '../../core/services/support.service';
import { CheckoutService } from '../../core/services/checkout.service';
import { CartService } from '../../core/services/cart.service';
import { Observable, of } from 'rxjs';
import { catchError, filter, map, shareReplay, tap, take } from 'rxjs/operators';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

interface AccountViewState {
  profile?: ProfileResponse;
  recentOrders: any[];
  cartSessionId: string;
}

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [NgIf, AsyncPipe, RouterLink, ReactiveFormsModule],
  templateUrl: './account.page.html',
  styleUrls: ['./account.page.css']
})
export class AccountPage {
  private readonly auth: AuthService = inject(AuthService);
  private readonly checkout: CheckoutService = inject(CheckoutService);
  private readonly support: SupportService = inject(SupportService);
  readonly cart: CartService = inject(CartService);
  private readonly fb: FormBuilder = inject(FormBuilder);
  private readonly destroyRef: DestroyRef = inject(DestroyRef);
  private readonly router: Router = inject(Router);

  showAuthForms = !this.auth.isAuthenticated();
  accountState$: Observable<AccountViewState> = of({
    profile: undefined,
    cartSessionId: this.cart.getSessionId(),
    recentOrders: []
  });

  loginForm = this.fb.group({
    identifier: ['', [Validators.required, emailOrPhoneValidator]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    remember: [true]
  });

  registerForm = this.fb.group({
    fullName: ['', [Validators.maxLength(120)]],
    identifier: ['', [Validators.required, emailOrPhoneValidator]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  supportForm = this.fb.nonNullable.group({
    subject: ['', [Validators.required, Validators.maxLength(120)]],
    description: ['', [Validators.required, Validators.maxLength(2000)]],
    priority: ['MEDIUM' as TicketPriority],
    channel: ['WEB' as TicketChannel]
  });

  statusMessage = '';
  submittingLogin = false;
  submittingTicket = false;
  submittingRegister = false;
  registerMode = false;

  constructor() {
    this.fetchAccountSnapshot();

    this.syncModeWithUrl(this.router.url);
    this.router.events
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        filter((event): event is NavigationEnd => event instanceof NavigationEnd)
      )
      .subscribe(event => this.syncModeWithUrl(event.urlAfterRedirects));
  }

  get profileLoaded(): boolean {
    return Boolean(this.currentProfile);
  }

  currentProfile: ProfileResponse | undefined;

  private fetchAccountSnapshot(): void {
    if (!this.auth.isAuthenticated()) {
      this.currentProfile = undefined;
      const fallback$ = of({
        profile: undefined,
        recentOrders: [],
        cartSessionId: this.cart.getSessionId()
      }).pipe(shareReplay(1));
      this.accountState$ = fallback$;
      this.showAuthForms = true;
      return;
    }

    const snapshot$ = this.auth.me().pipe(
      tap((profile) => {
        this.currentProfile = profile;
        this.loadRecentOrders();
        this.showAuthForms = false;
      }),
      map((profile) => ({
        profile,
        recentOrders: [],
        cartSessionId: this.cart.getSessionId()
      })),
      catchError(() => {
        this.currentProfile = undefined;
        this.auth.logout();
        this.showAuthForms = true;
        this.statusMessage = '';
        return of({
          profile: undefined,
          recentOrders: [],
          cartSessionId: this.cart.getSessionId()
        } as AccountViewState);
      }),
      shareReplay(1)
    );

    this.accountState$ = snapshot$;

    snapshot$
      .pipe(takeUntilDestroyed(this.destroyRef), take(1))
      .subscribe();
  }

  private loadRecentOrders(): void {
    // Recent order history will be fetched once the backend endpoint is available.
  }

  private syncModeWithUrl(url: string): void {
    if (!url) {
      return;
    }
    if (url.includes('/wishlist')) {
      this.registerMode = false;
      if (!this.auth.isAuthenticated()) {
        this.showAuthForms = true;
        this.statusMessage = 'Sign in to view and save wishlist items.';
      }
    }
  }

  login(): void {
    if (this.loginForm.invalid || this.submittingLogin) {
      return;
    }

    this.submittingLogin = true;
    const payload: LoginRequest = {
      identifier: normalizeIdentifier(this.loginForm.value.identifier ?? ''),
      password: this.loginForm.value.password ?? '',
      deviceId: this.cart.getSessionId()
    };

    this.auth.login(payload).pipe(
      tap(() => {
        this.statusMessage = 'Logged in successfully.';
        this.fetchAccountSnapshot();
        this.showAuthForms = false;
      }),
      catchError((err) => {
        this.statusMessage = err?.error?.message ?? 'Unable to sign in. Please verify your details.';
        this.submittingLogin = false;
        return of(undefined);
      })
    ).subscribe({
      complete: () => (this.submittingLogin = false)
    });
  }

  toggleMode(mode: 'login' | 'register'): void {
    if (mode === 'login') {
      this.registerMode = false;
    } else {
      this.registerMode = true;
      if (this.auth.isAuthenticated()) {
        this.auth.logout();
        this.currentProfile = undefined;
        this.accountState$ = of({
          profile: undefined,
          recentOrders: [],
          cartSessionId: this.cart.getSessionId()
        });
      }
    }
    this.showAuthForms = true;
    this.statusMessage = '';
  }

  signOut(): void {
    if (this.submittingLogin || this.submittingRegister) {
      return;
    }
    this.auth.logout();
    this.currentProfile = undefined;
    this.registerMode = false;
    this.statusMessage = '';
    this.loginForm.reset({
      identifier: '',
      password: '',
      remember: true
    });
    this.showAuthForms = true;
    this.fetchAccountSnapshot();
    this.router.navigate(['/account']).catch(() => undefined);
  }

  register(): void {
    if (this.registerForm.invalid || this.submittingRegister) {
      return;
    }

    this.submittingRegister = true;
    const payload: RegisterRequest = {
      identifier: normalizeIdentifier(this.registerForm.value.identifier ?? ''),
      password: this.registerForm.value.password ?? '',
      fullName: this.registerForm.value.fullName ?? undefined
    };

    this.auth.register(payload).pipe(
      tap(() => {
        this.registerMode = false;
        this.registerForm.reset({ fullName: '', identifier: '', password: '' });
        this.showAuthForms = false;
        this.statusMessage = 'Account created successfully.';
        this.fetchAccountSnapshot();
      }),
      catchError((err) => {
        this.statusMessage = err?.error?.message ?? 'We could not create your account right now.';
        this.submittingRegister = false;
        return of(undefined);
      })
    ).subscribe({
      complete: () => (this.submittingRegister = false)
    });
  }

  openTicket(): void {
    if (this.supportForm.invalid || this.submittingTicket) {
      return;
    }

    this.submittingTicket = true;
    const { subject, description, priority, channel } = this.supportForm.getRawValue();
    const payload: CreateTicketPayload = {
      subject,
      description,
      priority,
      channel,
      requesterEmail: this.currentProfile?.email ?? this.currentProfile?.identifier
    };

    this.support.openTicket(payload).pipe(
      tap(() => {
        this.statusMessage = 'Support ticket created. Our team will reach out soon.';
        this.supportForm.reset({
          subject: '',
          description: '',
          priority: 'MEDIUM' as TicketPriority,
          channel: 'WEB' as TicketChannel
        });
      }),
      catchError((err) => {
        this.statusMessage = err?.error?.message ?? 'Could not submit your request right now.';
        return of(undefined);
      })
    ).subscribe({
      complete: () => (this.submittingTicket = false)
    });
  }
}

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/i;
const MIN_PHONE_DIGITS = 10;
const MAX_PHONE_DIGITS = 15;

const emailOrPhoneValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const raw = (control.value ?? '').toString().trim();
  if (!raw) {
    return null; // handled by required validator
  }

  if (EMAIL_REGEX.test(raw)) {
    return null;
  }

  const digits = raw.replace(/\D+/g, '');
  if (digits.length >= MIN_PHONE_DIGITS && digits.length <= MAX_PHONE_DIGITS) {
    return null;
  }

  return { identifier: true };
};

function normalizeIdentifier(raw: string): string {
  const trimmed = (raw ?? '').trim();
  if (!trimmed) {
    return '';
  }
  if (EMAIL_REGEX.test(trimmed)) {
    return trimmed.toLowerCase();
  }
  return trimmed.replace(/\D+/g, '');
}
