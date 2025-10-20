import { AsyncPipe, NgFor, NgIf } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { StorefrontService } from '../../core/services/storefront.service';
import { BlogPost, SocialPost } from '../../core/models/storefront.models';
import { ApiService } from '../../core/services/api.service';

@Component({
  selector: 'app-blog',
  standalone: true,
  imports: [NgIf, NgFor, AsyncPipe, RouterLink, ReactiveFormsModule],
  templateUrl: './blog.page.html',
  styleUrls: ['./blog.page.css']
})
export class BlogPage {
  private readonly storefront: StorefrontService = inject(StorefrontService);
  private readonly fb: FormBuilder = inject(FormBuilder);
  private readonly api: ApiService = inject(ApiService);

  blogPosts$: Observable<BlogPost[]> = this.storefront.getBlogPosts().pipe(
    map((posts) => posts ?? [])
  );

  featuredPost$: Observable<BlogPost | null> = this.blogPosts$.pipe(
    map((posts) => posts.length ? posts[0] : null)
  );

  otherPosts$: Observable<BlogPost[]> = this.blogPosts$.pipe(
    map((posts) => posts.length > 1 ? posts.slice(1) : [])
  );

  socialPosts$: Observable<SocialPost[]> = this.storefront.getSocialPosts().pipe(
    map((posts) => posts ?? [])
  );

  newsletterForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  submitting = false;
  statusMessage = '';

  subscribe(): void {
    if (this.newsletterForm.invalid || this.submitting) {
      return;
    }

    this.submitting = true;
    const email = this.newsletterForm.value.email ?? '';

    this.api.post(this.api.url('newsletter'), { email }).pipe(
      tap(() => {
        this.statusMessage = 'Thanks for joining the Maavoori mailing list!';
        this.newsletterForm.reset();
      }),
      catchError(() => {
        this.statusMessage = 'We could not add you right now. Please try again later.';
        return of(undefined);
      })
    ).subscribe({
      complete: () => (this.submitting = false)
    });
  }
}
