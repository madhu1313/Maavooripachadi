import { AsyncPipe, CommonModule, DatePipe, NgFor, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { StorefrontService } from '../../../core/services/storefront.service';

interface FooterNavColumn {
  title: string;
  links: Array<{ label: string; path: string }>;
}

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, AsyncPipe, RouterLink, DatePipe],
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent {
  private readonly storefront: StorefrontService = inject(StorefrontService);

  readonly channels$ = this.storefront.getContactChannels();
  readonly timings$ = this.storefront.getStoreTimings();

  readonly socials = [
    { icon: 'instagram', label: 'Instagram', href: 'https://www.instagram.com/maavooripachadi' },
    { icon: 'facebook', label: 'Facebook', href: 'https://www.facebook.com/maavooripachadi' },
    { icon: 'youtube', label: 'YouTube', href: 'https://www.youtube.com/@maavooripachadi' }
  ];

  readonly navColumns: FooterNavColumn[] = [
    {
      title: 'Shop',
      links: [
        { label: 'All Products', path: '/shop' },
        { label: 'Veg Pickles', path: '/collections/veg-pickles' },
        { label: 'Non-Veg Pickles', path: '/collections/non-veg-pickles' },
        { label: 'Snacks & Sweets', path: '/collections/snacks-sweets' }
      ]
    },
    {
      title: 'Support',
      links: [
        { label: 'Track Order', path: '/track' },
        { label: 'Support Desk', path: '/support' },
        { label: 'Contact Us', path: '/contact' },
        { label: 'FAQs', path: '/support#faq' }
      ]
    },
    {
      title: 'About Maavoori',
      links: [
        { label: 'Our Story', path: '/about' },
        { label: 'Recipes', path: '/recipes' },
        { label: 'Blog', path: '/blog' },
        { label: 'Bulk & Corporate', path: '/contact' }
      ]
    }
  ];

  readonly legalLinks = [
    { label: 'Shipping Policy', path: '/policies/shipping' },
    { label: 'Refund Policy', path: '/policies/refund' },
    { label: 'Privacy Policy', path: '/policies/privacy' },
    { label: 'Terms of Service', path: '/policies/terms' }
  ];

  readonly today = new Date();
}
