import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { StorefrontService } from '../../core/services/storefront.service';
import { StoreTiming } from '../../core/models/storefront.models';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [NgIf, NgFor, AsyncPipe, RouterLink],
  templateUrl: './about.page.html',
  styleUrls: ['./about.page.css']
})
export class AboutPage {
  private readonly storefront: StorefrontService = inject(StorefrontService);

  highlights$ = this.storefront.getHighlights();
  timings$ = this.storefront.getStoreTimings();
  testimonials$ = this.storefront.getTestimonials();
}
