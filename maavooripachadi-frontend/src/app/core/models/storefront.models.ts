export interface NavigationLink {
  label: string;
  path?: string;
  externalUrl?: string;
  children?: NavigationLink[];
  badge?: string;
  icon?: string;
}

export interface HeroSlide {
  id: string;
  heading: string;
  subheading: string;
  description: string;
  ctaLabel: string;
  ctaLink: string;
  image: string;
  alignment?: 'left' | 'center' | 'right';
  accent?: string;
}

export interface Collection {
  id: string;
  title: string;
  description: string;
  image: string;
  handle: string;
  accent?: string;
}

export interface Product {
  id: string;
  title: string;
  description: string;
  image: string;
  price: number;
  compareAtPrice?: number;
  pricePaise?: number;
  badge?: string;
  rating?: number;
  reviewsCount?: number;
  tags?: string[];
  collectionIds?: string[];
  isNew?: boolean;
  isBestSeller?: boolean;
}

export interface ServiceHighlight {
  id: string;
  title: string;
  description: string;
  icon: string;
}

export interface Testimonial {
  id: string;
  name: string;
  location: string;
  rating: number;
  quote: string;
  avatar?: string;
}

export interface BlogPost {
  id: string;
  title: string;
  excerpt: string;
  image: string;
  readMoreUrl: string;
  publishedOn: string;
}

export interface SocialPost {
  id: string;
  image: string;
  link: string;
  platform: 'instagram' | 'facebook';
}

export interface ContactChannel {
  id: string;
  label: string;
  value: string;
  icon: string;
  href?: string;
}

export interface StoreTiming {
  day: string;
  open: string;
  close: string;
}
