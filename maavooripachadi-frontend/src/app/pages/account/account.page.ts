import { AsyncPipe, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { AuthService, LoginRequest, ProfileResponse, RegisterRequest } from '../../core/services/auth.service';
import { SupportService, CreateTicketPayload, TicketChannel, TicketPriority } from '../../core/services/support.service';
import { CheckoutService } from '../../core/services/checkout.service';
import { CartService } from '../../core/services/cart.service';
import { Observable, of } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';

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
  private readonly auth = inject(AuthService);
  private readonly checkout = inject(CheckoutService);
  private readonly support = inject(SupportService);
  readonly cart = inject(CartService);
  private readonly fb = inject(FormBuilder);

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
  }

  get profileLoaded(): boolean {
    return Boolean(this.currentProfile);
  }

  currentProfile: ProfileResponse | undefined;

  private fetchAccountSnapshot(): void {
    this.accountState$ = this.auth.me().pipe(
      tap((profile) => {
        this.currentProfile = profile;
        this.loadRecentOrders();
      }),
      map((profile) => ({
        profile,
        recentOrders: [],
        cartSessionId: this.cart.getSessionId()
      })),
      catchError(() => {
        this.currentProfile = undefined;
        return of({
          profile: undefined,
          recentOrders: [],
          cartSessionId: this.cart.getSessionId()
        } as AccountViewState);
      })
    );
  }

  private loadRecentOrders(): void {
    this.checkout.getCheckout('latest').pipe(catchError(() => of(undefined))).subscribe();
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
    }
    this.statusMessage = '';
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
        this.statusMessage = 'Account created successfully.';
        this.registerMode = false;
        this.registerForm.reset({ fullName: '', identifier: '', password: '' });
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
