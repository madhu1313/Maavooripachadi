import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ApiService, QueryParams } from './api.service';

export type ReviewStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'HIDDEN';

export interface Review {
  id?: number;
  productId: number;
  variantId?: number | null;
  rating: number;
  title?: string | null;
  body?: string | null;
  subjectId?: string | null;
  verifiedPurchase?: boolean;
  status?: ReviewStatus;
  helpfulCount?: number;
  notHelpfulCount?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface ReviewFlag {
  id?: number;
  review?: Review;
  subjectId?: string;
  reason?: string;
  details?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface SubmitReviewPayload {
  productId: number;
  variantId?: number | null;
  rating: number;
  title?: string | null;
  body?: string | null;
  subjectId: string;
  imageUrls?: string[];
}

export interface VoteReviewPayload {
  reviewId: number;
  subjectId: string;
  helpful: boolean;
}

export interface FlagReviewPayload {
  reviewId: number;
  subjectId: string;
  reason?: string;
  details?: string;
}

export interface ReviewListOptions {
  page?: number;
  size?: number;
}

interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

@Injectable({ providedIn: 'root' })
export class ReviewsService {
  constructor(private readonly api: ApiService) {}

  list(productId: number, options: ReviewListOptions = {}): Observable<PageResponse<Review>> {
    const query: QueryParams = {
      productId,
      page: options.page ?? 0,
      size: options.size ?? 10
    };

    return this.api.get<PageResponse<Review>>({ key: 'reviews', query }).pipe(
      map((page) => ({
        ...page,
        content: page.content.map((review) => this.normalizeReview(review))
      }))
    );
  }

  submit(payload: SubmitReviewPayload): Observable<Review> {
    return this.api.post<Review>({ key: 'reviews', body: payload }).pipe(map((review) => this.normalizeReview(review)));
  }

  vote(payload: VoteReviewPayload): Observable<Review> {
    return this.api.post<Review>({ key: 'reviewsVote', body: payload }).pipe(map((review) => this.normalizeReview(review)));
  }

  flag(payload: FlagReviewPayload): Observable<ReviewFlag> {
    return this.api.post<ReviewFlag>({ key: 'reviewsFlag', body: payload });
  }

  private normalizeReview(review: Review): Review {
    if (!review) {
      return {
        productId: 0,
        rating: 0
      };
    }

    return {
      ...review,
      verifiedPurchase: Boolean(review.verifiedPurchase),
      helpfulCount: review.helpfulCount ?? 0,
      notHelpfulCount: review.notHelpfulCount ?? 0
    };
  }
}
