import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BehaviorSubject, EMPTY } from 'rxjs';
import { catchError, finalize, tap } from 'rxjs/operators';
import { ContactChannel } from '../../core/models/storefront.models';
import { SupportService } from '../../core/services/support.service';

type ContactStatus = { kind: 'idle' | 'success' | 'error'; message: string | null };

interface SupportHighlight {
  id: string;
  title: string;
  description: string;
}

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [NgIf, NgFor, AsyncPipe, ReactiveFormsModule],
  templateUrl: './contact.page.html',
  styleUrls: ['./contact.page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContactPage {
  private readonly fb: FormBuilder = inject(FormBuilder);
  private readonly support: SupportService = inject(SupportService);

  readonly contactChannels: ContactChannel[] = [
    {
      id: 'phone',
      label: 'Call us',
      value: '+91 85558 59667',
      icon: 'PH',
      href: 'tel:+918555859667'
    },
    {
      id: 'whatsapp',
      label: 'WhatsApp',
      value: '+91 85558 59667',
      icon: 'WA',
      href: 'https://wa.me/918555859667'
    },
    {
      id: 'email',
      label: 'Email',
      value: 'hello@maavooripachadi.com',
      icon: 'EM',
      href: 'mailto:hello@maavooripachadi.com'
    }
  ];

  readonly supportHighlights: SupportHighlight[] = [
    {
      id: 'delivery',
      title: 'Delivery support',
      description: 'Need help with an order? Share your order number and our team will respond within 12 hours.'
    },
    {
      id: 'wholesale',
      title: 'Bulk & corporate orders',
      description: 'Planning an event or gifting spree? We curate bespoke pickle hampers shipped pan-India.'
    },
    {
      id: 'feedback',
      title: 'Feedback & collaborations',
      description: 'We love hearing from our community. Drop in ideas, feedback, or partnership opportunities.'
    }
  ];

  readonly contactForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    phone: ['', [Validators.pattern(/^(?:[0-9]{10})?$/)]],
    subject: ['', [Validators.required, Validators.minLength(6)]],
    message: ['', [Validators.required, Validators.minLength(20)]],
    consent: [true, [Validators.requiredTrue]]
  });

  private readonly submittingSubject = new BehaviorSubject<boolean>(false);
  private readonly statusSubject = new BehaviorSubject<ContactStatus>({ kind: 'idle', message: null });

  readonly submitting$ = this.submittingSubject.asObservable();
  readonly status$ = this.statusSubject.asObservable();

  readonly businessHours = 'Monday - Saturday | 10:00 AM to 7:00 PM IST';
  readonly studioAddress = 'Maavoori Production Kitchen, Madhapur, Hyderabad';

  submit(): void {
    if (this.contactForm.invalid || this.submittingSubject.value) {
      this.contactForm.markAllAsTouched();
      return;
    }

    this.submittingSubject.next(true);
    this.statusSubject.next({ kind: 'idle', message: null });

    const payload = this.buildPayload();

    this.support
      .openTicket(payload)
      .pipe(
        tap(() => {
          this.statusSubject.next({
            kind: 'success',
            message: 'Thank you! Our team will reach out within a few hours.'
          });
          this.contactForm.reset({
            name: '',
            email: '',
            phone: '',
            subject: '',
            message: '',
            consent: true
          });
        }),
        catchError((error) => {
          console.error('Failed to submit contact form', error);
          this.statusSubject.next({
            kind: 'error',
            message: error?.error?.message ?? 'We could not send your message right now. Please try again soon.'
          });
          return EMPTY;
        }),
        finalize(() => this.submittingSubject.next(false))
      )
      .subscribe();
  }

  dismissStatus(): void {
    this.statusSubject.next({ kind: 'idle', message: null });
  }

  trackByChannel(_: number, channel: ContactChannel) {
    return channel.id;
  }

  trackByHighlight(_: number, highlight: SupportHighlight) {
    return highlight.id;
  }

  private buildPayload() {
    const { name, email, phone, subject, message } = this.contactForm.getRawValue();

    const descriptionLines = [
      message.trim(),
      '',
      `From: ${name.trim()} (${email.trim()})`,
      phone ? `Phone: ${phone.trim()}` : null,
      `Channel: Web contact form`
    ].filter((line): line is string => Boolean(line));

    return {
      subject: subject.trim(),
      description: descriptionLines.join('\n'),
      requesterEmail: email.trim(),
      requesterName: name.trim(),
      priority: 'MEDIUM' as const,
      channel: 'WEB' as const
    };
  }
}
