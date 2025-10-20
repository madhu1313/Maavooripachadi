import { AsyncPipe, CommonModule, NgFor, NgIf } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface TopBarLink {
  label: string;
  icon?: string;
  path?: string;
}

interface TopBarLocaleOption {
  code: string;
  label: string;
}

@Component({
  selector: 'app-top-bar',
  standalone: true,
  imports: [CommonModule, NgFor, NgIf, AsyncPipe, RouterLink],
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css']
})
export class TopBarComponent {
  readonly messages = [
    'No preservatives • HACCP certified • Crafted in 48 kg micro batches',
    'Pan-India delivery in 3-5 days • Free Hyderabad delivery above ₹999',
    'WhatsApp us for gifting • Corporate hampers ready in 48 hours'
  ];

  readonly links: TopBarLink[] = [
    { label: 'Festival Hampers', path: '/collections/gifts', icon: 'gift' },
    { label: 'Track Order', path: '/track', icon: 'local_shipping' },
    { label: 'Support', path: '/support', icon: 'support_agent' },
    { label: 'Recipes', path: '/recipes', icon: 'restaurant_menu' }
  ];

  readonly currencies: TopBarLocaleOption[] = [
    { code: 'INR', label: 'INR ₹' },
    { code: 'USD', label: 'USD $' },
    { code: 'SGD', label: 'SGD $' }
  ];

  readonly languages: TopBarLocaleOption[] = [
    { code: 'en', label: 'English' },
    { code: 'te', label: 'తెలుగు' },
    { code: 'hi', label: 'हिन्दी' }
  ];

  readonly concierge = {
    whatsapp: 'https://wa.me/918555859667',
    display: '+91 85558 59667'
  };
}
