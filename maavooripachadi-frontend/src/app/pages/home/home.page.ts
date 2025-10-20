import { AsyncPipe, NgClass, NgFor, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SlickCarouselModule } from 'ngx-slick-carousel';
import { Observable } from 'rxjs';
import { Product } from '../../core/models/storefront.models';
import { CatalogService } from '../../core/services/catalog.service';
import { StorefrontService } from '../../core/services/storefront.service';
import { ProductCardComponent } from '../../shared/components/product-card/product-card.component';

interface ProductShelf {
  id: string;
  title: string;
  subtitle: string;
  description: string;
  ctaLabel: string;
  ctaLink: string;
  accent: string;
  products$: Observable<Product[]>;
}

interface TrustBadge {
  id: string;
  label: string;
  description: string;
  accent: string;
}

interface ProcessStep {
  id: string;
  title: string;
  description: string;
  highlight: string;
}

interface ExperienceStat {
  id: string;
  label: string;
  value: string;
  helper: string;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [NgFor, NgIf, NgClass, AsyncPipe, RouterLink, SlickCarouselModule, ProductCardComponent],
  templateUrl: './home.page.html',
  styleUrls: ['./home.page.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomePage {
  private readonly storefront: StorefrontService = inject(StorefrontService);
  private readonly catalog: CatalogService = inject(CatalogService);

  readonly heroSlides$ = this.storefront.getHeroSlides();
  readonly collections$ = this.storefront.getCollections();
  readonly highlights$ = this.storefront.getHighlights();
  readonly testimonials$ = this.storefront.getTestimonials();
  readonly blogPosts$ = this.storefront.getBlogPosts();
  readonly socialPosts$ = this.storefront.getSocialPosts();

  readonly productShelves: ProductShelf[] = [
    {
      id: 'best-sellers',
      title: 'Maavoori best sellers',
      subtitle: 'Loved by over 50,000 families across India',
      description: 'Signature jars that sell out every harvest season. Stock up before the next batch hits the sun.',
      ctaLabel: 'Shop best sellers',
      ctaLink: '/shop?sort=popularity',
      accent: '#f97316',
      products$: this.catalog.bestSellers(8)
    },
    {
      id: 'veggie-hero',
      title: 'Veg pickle staples',
      subtitle: 'Farm fresh, sun-kissed vegetables',
      description: 'Every jar is slow cured in cold-pressed gingelly oil for that nostalgic Andhra punch.',
      ctaLabel: 'Browse veg pickles',
      ctaLink: '/collections/veg-pickles',
      accent: '#16a34a',
      products$: this.catalog.listCollection('veg-pickles', 8)
    },
    {
      id: 'protein-pack',
      title: 'Non-veg spice heroes',
      subtitle: 'Simmered meats and seafood delicacies',
      description: 'Gongura mutton, prawn pachadi and nattukodi favourites for the true spice lover.',
      ctaLabel: 'Explore non-veg range',
      ctaLink: '/collections/non-veg-pickles',
      accent: '#ef4444',
      products$: this.catalog.listCollection('non-veg-pickles', 8)
    }
  ];

  readonly trustBadges: TrustBadge[] = [
    { id: 'small-batch', label: 'Small batch handcrafted', description: 'Cooked in brass pots in 48kg micro batches.', accent: '#f97316' },
    { id: 'cold-pressed', label: 'Cold pressed oils', description: 'Sealed with premium gingelly & groundnut oils.', accent: '#10b981' },
    { id: 'nationwide', label: 'Pan India delivery', description: 'Carefully packed with leak-proof freshness seals.', accent: '#38bdf8' },
    { id: 'family-recipe', label: 'Family recipes', description: 'Four decades of home kitchen secrets bottled.', accent: '#a855f7' }
  ];

  readonly processSteps: ProcessStep[] = [
    {
      id: 'sourcing',
      title: 'Sourcing & prep',
      description: 'We handpick seasonal produce from trusted partner farms before washing and sun-drying them naturally.',
      highlight: 'Harvest to pickle in under 24 hours'
    },
    {
      id: 'stone-ground',
      title: 'Stone-ground masalas',
      description: 'Every batch is slow ground on traditional rubbu rolu for a coarse, flavour-rich masala base.',
      highlight: 'Zero factory shortcuts'
    },
    {
      id: 'slow-cook',
      title: 'Slow cooking & curing',
      description: 'Ingredients simmer in brass pots and are cured on our rooftop deck for the perfect tang and texture.',
      highlight: '3 day sun-curing ritual'
    },
    {
      id: 'pack-ship',
      title: 'Pack & ship',
      description: 'Glass jars are vacuum sealed, nitrogen flushed and dispatched with temperature controlled logistics.',
      highlight: 'Delivered fresh across India'
    }
  ];

  readonly experienceStats: ExperienceStat[] = [
    { id: 'families', label: 'Families served', value: '50K+', helper: 'across India & overseas' },
    { id: 'recipes', label: 'Heritage recipes', value: '32', helper: 'curated for modern kitchens' },
    { id: 'hours', label: 'Sun curing hours', value: '120+', helper: 'per artisanal batch' }
  ];

  readonly heroSellingPoints: string[] = [
    'Preservative free handcrafted pickles',
    'Made in micro batches every fortnight',
    'Pan India delivery within 3-5 days'
  ];

  readonly heroCarouselConfig = {
    dots: true,
    infinite: true,
    arrows: false,
    autoplay: true,
    autoplaySpeed: 5200,
    speed: 650,
    fade: true,
    cssEase: 'ease-in-out'
  };

  readonly testimonialCarouselConfig = {
    dots: true,
    infinite: true,
    slidesToShow: 2,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 6500,
    responsive: [
      { breakpoint: 992, settings: { slidesToShow: 1 } }
    ]
  };

  readonly socialCarouselConfig = {
    dots: false,
    infinite: true,
    slidesToShow: 4,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 4200,
    responsive: [
      { breakpoint: 1200, settings: { slidesToShow: 3 } },
      { breakpoint: 900, settings: { slidesToShow: 2 } },
      { breakpoint: 520, settings: { slidesToShow: 1 } }
    ]
  };
}

