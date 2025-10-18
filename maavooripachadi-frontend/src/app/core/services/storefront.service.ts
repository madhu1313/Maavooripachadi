import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import {
  BLOG_POSTS,
  COLLECTIONS,
  CONTACT_CHANNELS,
  HERO_SLIDES,
  NAVIGATION_LINKS,
  SERVICE_HIGHLIGHTS,
  SOCIAL_POSTS,
  STORE_TIMINGS,
  TESTIMONIALS
} from '../data/storefront.data';
import {
  BlogPost,
  Collection,
  ContactChannel,
  HeroSlide,
  NavigationLink,
  ServiceHighlight,
  SocialPost,
  StoreTiming,
  Testimonial
} from '../models/storefront.models';

@Injectable({ providedIn: 'root' })
export class StorefrontService {
  getNavigation(): Observable<NavigationLink[]> {
    return of(NAVIGATION_LINKS);
  }

  getHeroSlides(): Observable<HeroSlide[]> {
    return of(HERO_SLIDES);
  }

  getCollections(): Observable<Collection[]> {
    return of(COLLECTIONS);
  }

  getHighlights(): Observable<ServiceHighlight[]> {
    return of(SERVICE_HIGHLIGHTS);
  }

  getTestimonials(): Observable<Testimonial[]> {
    return of(TESTIMONIALS);
  }

  getBlogPosts(): Observable<BlogPost[]> {
    return of(BLOG_POSTS);
  }

  getSocialPosts(): Observable<SocialPost[]> {
    return of(SOCIAL_POSTS);
  }

  getContactChannels(): Observable<ContactChannel[]> {
    return of(CONTACT_CHANNELS);
  }

  getStoreTimings(): Observable<StoreTiming[]> {
    return of(STORE_TIMINGS);
  }
}
