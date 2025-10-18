import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, Observable } from 'rxjs';
import { catchError, finalize, map } from 'rxjs/operators';
import { SupportService, CreateTicketPayload } from '../../core/services/support.service';

type TicketStatusKind = 'idle' | 'success' | 'error';
type SupportPriority = 'LOW' | 'MEDIUM' | 'HIGH';
type SupportTopic = 'ORDER' | 'PRODUCT' | 'PAYMENT' | 'WHOLESALE' | 'GENERAL';

interface TicketStatus {
  kind: TicketStatusKind;
  message: string | null;
}

interface SupportTopicCard {
  id: SupportTopic;
  title: string;
  description: string;
  icon: string;
  link?: string;
}

interface FaqItem {
  question: string;
  answer: string;
}

interface SupportChannel {
  id: string;
  label: string;
  value: string;
  icon: string;
  href?: string;
  description?: string;
}

@Component({
  selector: 'app-support',
  standalone: true,
  imports: [NgIf, NgFor, AsyncPipe, RouterLink, ReactiveFormsModule],
  templateUrl: './support.page.html',
  styleUrls: ['./support.page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SupportPage {
  private readonly fb = inject(FormBuilder);
  private readonly support = inject(SupportService);

  readonly topics: SupportTopicCard[] = [
    {
      id: 'ORDER',
      title: 'Order & tracking',
      description: 'Delivery ETA, address change and tracking updates for your Maavoori jars.',
      icon: 'local_shipping',
      link: '/track'
    },
    {
      id: 'PRODUCT',
      title: 'Product guidance',
      description: 'Help with storage, flavour pairing or ingredients for our pickles and podis.',
      icon: 'restaurant'
    },
    {
      id: 'PAYMENT',
      title: 'Payments & refunds',
      description: 'Invoice copies, payment status and refund timelines.',
      icon: 'receipt_long'
    },
    {
      id: 'WHOLESALE',
      title: 'Bulk & gifting',
      description: 'Corporate hampers, events and retail partnerships.',
      icon: 'storefront',
      link: '/contact'
    },
    {
      id: 'GENERAL',
      title: 'Anything else',
      description: 'Feedback, recipe ideas or support about your Maavoori experience.',
      icon: 'chat'
    }
  ];

  readonly faq: FaqItem[] = [
    {
      question: 'When will my order ship?',
      answer: 'Orders placed before 4 PM ship the same day. Fresh batches are sealed every morning and dispatched with temperature-safe packaging.'
    },
    {
      question: 'How do I store Maavoori pickles?',
      answer: 'Use a dry spoon each time, keep the lid tightly closed and refrigerate after opening for maximum freshness.'
    },
    {
      question: 'Can I request customised gifting hampers?',
      answer: 'Absolutely! Share the quantity and delivery date—we curate hampers with personalised sleeves and handwritten notes.'
    },
    {
      question: 'What if a jar leaks during transit?',
      answer: 'Ping us on WhatsApp with your order number and photos. We’ll arrange a replacement or refund right away.'
    }
  ];

  readonly channels: SupportChannel[] = [
    {
      id: 'whatsapp',
      label: 'WhatsApp concierge',
      value: '+91 93900 12345',
      icon: 'phone_iphone',
      href: 'https://wa.me/919390012345',
      description: 'Quickest support 10 AM – 9 PM IST'
    },
    {
      id: 'email',
      label: 'Email Maavoori',
      value: 'support@maavooripachadi.com',
      icon: 'mail',
      href: 'mailto:support@maavooripachadi.com',
      description: 'We respond within 12 working hours'
    },
    {
      id: 'phone',
      label: 'Call us',
      value: '+91 98765 43210',
      icon: 'call',
      description: 'Monday to Saturday, 10 AM – 7 PM'
    }
  ];

  readonly priorities: Array<{ id: SupportPriority; label: string }> = [
    { id: 'LOW', label: 'Low' },
    { id: 'MEDIUM', label: 'Normal' },
    { id: 'HIGH', label: 'Urgent' }
  ];

  readonly supportForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    phone: [''],
    orderId: [''],
    topic: ['ORDER' as SupportTopic],
    priority: ['MEDIUM' as SupportPriority],
    message: ['', [Validators.required, Validators.minLength(20)]],
    consent: [true, Validators.requiredTrue]
  });

  private readonly submittingSubject = new BehaviorSubject<boolean>(false);
  private readonly statusSubject = new BehaviorSubject<TicketStatus>({ kind: 'idle', message: null });

  readonly submitting$ = this.submittingSubject.asObservable();
  readonly status$ = this.statusSubject.asObservable();

  submit(): void {
    if (this.supportForm.invalid || this.submittingSubject.value) {
      this.supportForm.markAllAsTouched();
      return;
    }

    this.submittingSubject.next(true);
    this.statusSubject.next({ kind: 'idle', message: null });

    const payload = this.buildPayload();

    this.support
      .openTicket(payload)
      .pipe(
        map(() => {
          this.statusSubject.next({
            kind: 'success',
            message: 'Ticket created! Our team will respond shortly.'
          });
          this.supportForm.reset({
            name: '',
            email: '',
            phone: '',
            orderId: '',
            topic: 'ORDER' as SupportTopic,
            priority: 'MEDIUM' as SupportPriority,
            message: '',
            consent: true
          });
        }),
        catchError((error) => {
          console.error('Failed to open support ticket', error);
          this.statusSubject.next({
            kind: 'error',
            message: error?.error?.message ?? 'Unable to create a ticket right now. Please try again soon.'
          });
          return [];
        }),
        finalize(() => this.submittingSubject.next(false))
      )
      .subscribe();
  }

  dismissStatus(): void {
    this.statusSubject.next({ kind: 'idle', message: null });
  }

  selectTopic(topic: SupportTopic): void {
    this.supportForm.patchValue({ topic });
  }

  private buildPayload(): CreateTicketPayload {
    const { name, email, phone, orderId, topic, priority, message } = this.supportForm.getRawValue();
    const subject = `[${topic}] Support request from ${name.trim()}`;

    const descriptionSections = [
      message.trim(),
      '',
      orderId ? `Order ID: ${orderId.trim()}` : null,
      phone ? `Phone: ${phone.trim()}` : null,
      `Topic: ${topic}`,
      `Submitted via: support page`
    ].filter((section): section is string => Boolean(section));

    return {
      subject,
      description: descriptionSections.join('\n'),
      requesterEmail: email.trim(),
      requesterName: name.trim(),
      priority: priority ?? 'MEDIUM',
      channel: 'WEB'
    };
  }
}
