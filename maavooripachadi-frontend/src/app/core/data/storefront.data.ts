import {
  BlogPost,
  Collection,
  ContactChannel,
  HeroSlide,
  NavigationLink,
  Product,
  ServiceHighlight,
  SocialPost,
  StoreTiming,
  Testimonial
} from '../models/storefront.models';

const PLACEHOLDER = '/assets/images/Maavooripachadi.jpg';

export const NAVIGATION_LINKS: NavigationLink[] = [
  { label: 'Home', path: '/' },
  {
    label: 'Veg Pickles',
    path: '/collections/veg-pickles',
    children: [
      { label: 'Mango Avakai', path: '/product/mango-avakai' },
      { label: 'Gongura Pandumirchi', path: '/product/gongura-pandumirchi', badge: 'Hot' },
      { label: 'Budida Gummadikaya', path: '/product/budida-gummadikaya' },
      { label: 'Kothimeera Pickle', path: '/product/kothimeera-pickle' }
    ]
  },
  {
    label: 'Non-Veg Pickles',
    path: '/collections/non-veg-pickles',
    children: [
      { label: 'Gongura Mutton', path: '/product/gongura-mutton' },
      { label: 'Natu Kodi Pickle', path: '/product/natu-kodi' },
      { label: 'Prawns Pickle', path: '/product/prawns' },
      { label: 'Crab Pickle', path: '/product/crab-pickle' }
    ]
  },
  {
    label: 'Podi & Mixes',
    path: '/collections/podi',
    children: [
      { label: 'Karivepaku Podi', path: '/product/karivepaku-podi' },
      { label: 'Sambar Mix', path: '/product/sambar-mix' },
      { label: 'Flax Seed Podi', path: '/product/flax-seed-podi' }
    ]
  },
  {
    label: 'Snacks & Sweets',
    path: '/collections/snacks-sweets',
    children: [
      { label: 'Bellam Gavvalu', path: '/product/bellam-gavvalu', badge: 'New' },
      { label: 'Ribbon Pakodi', path: '/product/ribbon-pakodi' },
      { label: 'Badam Halwa', path: '/product/badam-halwa' }
    ]
  },
  {
    label: 'Gift Hampers',
    path: '/collections/gifts',
    children: [
      { label: 'Festive Family Pack', path: '/collections/gifts/festive-pack' },
      { label: 'Corporate Combo', path: '/collections/gifts/corporate' }
    ]
  },
  { label: 'Contact', path: '/pages/contact' }
];

export const HERO_SLIDES: HeroSlide[] = [
  {
    id: 'slide-heritage',
    heading: 'Maavoori Pachadi Favourites',
    subheading: 'Heritage Recipes - Modern Kitchens',
    description:
      'Celebrate the flavours of Telangana and Andhra with small-batch pickles crafted from sun-ripened produce and cold pressed oils.',
    ctaLabel: 'Browse Veg Pickles',
    ctaLink: '/collections/veg-pickles',
    image: '/assets/images/Maavooripachadi.jpg',
    alignment: 'right',
    accent: '#f97316'
  },
  {
    id: 'slide-meat',
    heading: 'Authentic Non-Veg Delicacies',
    subheading: 'Slow Cooked - Spice Balanced',
    description:
      'From gongura mutton to nattukodi pickle, every jar is simmered to perfection and sealed with traditional aromatics.',
    ctaLabel: 'Shop Non-Veg Range',
    ctaLink: '/collections/non-veg-pickles',
    image: '/assets/images/non-veg.jpg',
    alignment: 'center',
    accent: '#ea580c'
  },
  {
    id: 'slide-snacks',
    heading: 'Tea-Time Snacks & Sweets',
    subheading: 'Crunchy - Nutritious - Ready to Serve',
    description:
      'Millet murukulu, jaggery treats and weekend favourites curated for gifting and family get-togethers.',
    ctaLabel: 'Explore Snacks',
    ctaLink: '/collections/snacks-sweets',
    image: '/assets/images/sweets-n-snacks.jpg',
    alignment: 'right',
    accent: '#0ea5e9'
  }
];

export const COLLECTIONS: Collection[] = [
  {
    id: 'veg-pickles',
    title: 'Veg Pickles',
    description: 'Seasonal vegetables pickled with sesame oil and stone-ground masalas.',
    image: '/assets/images/veg.jpg',
    handle: '/collections/veg-pickles',
    accent: '#f97316'
  },
  {
    id: 'non-veg-pickles',
    title: 'Non-Veg Pickles',
    description: 'Signature mutton, chicken and seafood pickles made fresh in copper pots.',
    image: '/assets/images/non-veg.jpg',
    handle: '/collections/non-veg-pickles',
    accent: '#ea580c'
  },
  {
    id: 'podi',
    title: 'Spice Podis',
    description: 'Protein-rich podis and quick mix powders for everyday meals.',
    image: '/assets/images/podi.jpg',
    handle: '/collections/podi',
    accent: '#22c55e'
  },
  {
    id: 'snacks-sweets',
    title: 'Snacks & Sweets',
    description: 'Crispy munchies and ghee-laden sweets for festivals and chai time.',
    image: '/assets/images/sweets-n-snacks.jpg',
    handle: '/collections/snacks-sweets',
    accent: '#0ea5e9'
  }
];

export const PRODUCTS: Product[] = [
  {
    id: 'mango-avakai',
    title: 'Mango Avakai',
    description: 'Signature kothapalli mango pickle tempered with mustard and garlic.',
    image: '/assets/images/mango-maagai_670x.jpg',
    price: 360,
    collectionIds: ['veg-pickles'],
    rating: 5,
    reviewsCount: 182,
    badge: 'Iconic',
    isBestSeller: true
  },
  {
    id: 'gongura-pandumirchi',
    title: 'Gongura Pandumirchi',
    description: 'Sun-dried chillies and tangy sorrel leaves for spice lovers.',
    image: '/assets/images/gongura_pandumirchi_670x.jpg',
    price: 320,
    collectionIds: ['veg-pickles'],
    rating: 4.8,
    reviewsCount: 96,
    isBestSeller: true
  },
  {
    id: 'budida-gummadikaya',
    title: 'Budida Gummadikaya',
    description: 'Ash gourd pickle balanced with jaggery and red chilli.',
    image: PLACEHOLDER,
    price: 280,
    collectionIds: ['veg-pickles'],
    rating: 4.6,
    reviewsCount: 74
  },
  {
    id: 'gongura-mutton',
    title: 'Gongura Mutton Pickle',
    description: 'Slow cooked lamb with sorrel leaves, ginger and garlic.',
    image: PLACEHOLDER,
    price: 720,
    collectionIds: ['non-veg-pickles'],
    rating: 4.9,
    reviewsCount: 133,
    badge: 'Hot Pick',
    isBestSeller: true
  },
  {
    id: 'natu-kodi',
    title: 'Natu Kodi Pickle',
    description: 'Country chicken chunks simmered with home-ground garam masala.',
    image: PLACEHOLDER,
    price: 640,
    collectionIds: ['non-veg-pickles'],
    rating: 4.7,
    reviewsCount: 88
  },
  {
    id: 'prawns',
    title: 'Prawns Pickle',
    description: 'Coastal style prawns in gingelly oil with curry leaves and garlic.',
    image: PLACEHOLDER,
    price: 760,
    collectionIds: ['non-veg-pickles'],
    rating: 4.8,
    reviewsCount: 101,
    badge: 'Chef Choice'
  },
  {
    id: 'karivepaku-podi',
    title: 'Karivepaku Podi',
    description: 'Curry leaf powder roasted with urad dal and sesame seeds.',
    image: PLACEHOLDER,
    price: 160,
    collectionIds: ['podi'],
    rating: 4.7,
    reviewsCount: 92
  },
  {
    id: 'flax-seed-podi',
    title: 'Flax Seed Podi',
    description: 'Nutty flax seeds paired with peanuts and tamarind for breakfast bowls.',
    image: PLACEHOLDER,
    price: 140,
    collectionIds: ['podi'],
    rating: 4.6,
    reviewsCount: 58
  },
  {
    id: 'sambar-mix',
    title: 'Instant Sambar Mix',
    description: 'Ready-to-cook powder with roasted spices and lentils.',
    image: PLACEHOLDER,
    price: 180,
    collectionIds: ['podi'],
    rating: 4.5,
    reviewsCount: 41,
    isNew: true
  },
  {
    id: 'bellam-gavvalu',
    title: 'Bellam Gavvalu',
    description: 'Jaggery coated shell snacks fried in cold pressed ghee.',
    image: PLACEHOLDER,
    price: 260,
    collectionIds: ['snacks-sweets'],
    rating: 4.9,
    reviewsCount: 67,
    badge: 'New'
  },
  {
    id: 'ribbon-pakodi',
    title: 'Ribbon Pakodi',
    description: 'Crispy besan strips seasoned with garlic and pepper.',
    image: PLACEHOLDER,
    price: 220,
    collectionIds: ['snacks-sweets'],
    rating: 4.5,
    reviewsCount: 54
  },
  {
    id: 'badam-halwa',
    title: 'Badam Halwa',
    description: 'Almond halwa slow cooked with A2 ghee and saffron.',
    image: PLACEHOLDER,
    price: 540,
    collectionIds: ['snacks-sweets'],
    rating: 4.9,
    reviewsCount: 112,
    isBestSeller: true
  }
];

export const SERVICE_HIGHLIGHTS: ServiceHighlight[] = [
  {
    id: 'shipping',
    title: 'Nationwide Delivery',
    description: 'Fresh jars dispatched within 48 hours across India.',
    icon: 'local_shipping'
  },
  {
    id: 'craft',
    title: 'Farm to Jar Ingredients',
    description: 'Stone-ground spices and seasonal produce sourced from partner farms.',
    icon: 'restaurant'
  },
  {
    id: 'payments',
    title: 'Flexible Payments',
    description: 'UPI, cards, netbanking and cash-on-delivery options available.',
    icon: 'payments'
  },
  {
    id: 'support',
    title: 'WhatsApp Assistance',
    description: 'Need pairing ideas? Our Maavoori experts are a ping away.',
    icon: 'chat'
  }
];

export const TESTIMONIALS: Testimonial[] = [
  {
    id: 't1',
    name: 'Meghana S.',
    location: 'Hyderabad',
    rating: 5,
    quote:
      'The mango avakai tastes exactly like my ammamma used to make. Perfect balance of spice and tang.'
  },
  {
    id: 't2',
    name: 'Arun K.',
    location: 'Bengaluru',
    rating: 4.7,
    quote:
      'Loved the non-veg sampler box. The gongura mutton was a hit at our weekend lunch.'
  },
  {
    id: 't3',
    name: 'Suma R.',
    location: 'Pune',
    rating: 4.9,
    quote:
      'Quick delivery and neat packaging. The podis have become a breakfast staple at our home.'
  }
];

export const BLOG_POSTS: BlogPost[] = [
  {
    id: 'blog-1',
    title: 'Inside the Maavoori Kitchen',
    excerpt: 'Take a peek at how our home chefs pickle, sun-dry and seal every batch.',
    image: '/assets/images/non-veg.jpg',
    readMoreUrl: '/blogs/stories/inside-the-kitchen',
    publishedOn: 'July 18, 2025'
  },
  {
    id: 'blog-2',
    title: 'Pairing Pickles with Everyday Meals',
    excerpt: 'Simple tips to elevate dosa, idli and millet bowls with our podis and pickles.',
    image: '/assets/images/karivepaku_670x.jpg',
    readMoreUrl: '/blogs/stories/pickle-pairings',
    publishedOn: 'June 30, 2025'
  },
  {
    id: 'blog-3',
    title: 'Festive Gift Ideas from Maavoori',
    excerpt: 'Curated hampers and limited-edition jars to surprise your loved ones.',
    image: '/assets/images/Diwali-Gift-Tray.jpg',
    readMoreUrl: '/blogs/stories/festive-gifts',
    publishedOn: 'June 05, 2025'
  }
];

export const SOCIAL_POSTS: SocialPost[] = [
  { id: 'ig-1', image: '/assets/images/Maavooripachadi.jpg', link: 'https://www.instagram.com/maavooripachadi', platform: 'instagram' },
  /*{ id: 'ig-2', image: '/assets/images/category-powders.jpg', link: 'https://www.instagram.com/maavooripachadi', platform: 'instagram' },
  { id: 'ig-3', image: '/assets/images/category-gifts.jpg', link: 'https://www.instagram.com/maavooripachadi', platform: 'instagram' },
  { id: 'ig-4', image: '/assets/images/hero.jpg', link: 'https://www.instagram.com/maavooripachadi', platform: 'instagram' },
  { id: 'ig-5', image: '/assets/images/category-pickles.jpg', link: 'https://www.instagram.com/maavooripachadi', platform: 'instagram' },
  { id: 'ig-6', image: '/assets/images/category-gifts.jpg', link: 'https://www.instagram.com/maavooripachadi', platform: 'instagram' }*/
];

export const CONTACT_CHANNELS: ContactChannel[] = [
  {
    id: 'phone',
    label: 'Call',
    value: '+91 85558 59667',
    icon: 'call',
    href: 'tel:+918555859667'
  },
  {
    id: 'whatsapp',
    label: 'WhatsApp',
    value: '+91 85558 59667',
    icon: 'chat',
    href: 'https://wa.me/918555859667'
  },
  {
    id: 'email',
    label: 'Email',
    value: 'hello@maavooripachadi.com',
    icon: 'mail',
    href: 'mailto:hello@maavooripachadi.com'
  },
  {
    id: 'address',
    label: 'Kitchen Studio',
    value: 'Plot 21, Road 7, Banjara Hills, Hyderabad, Telangana 500034',
    icon: 'location_on'
  }
];

export const STORE_TIMINGS: StoreTiming[] = [
  { day: 'Monday', open: '10:00 AM', close: '07:00 PM' },
  { day: 'Tuesday', open: '10:00 AM', close: '07:00 PM' },
  { day: 'Wednesday', open: '10:00 AM', close: '07:00 PM' },
  { day: 'Thursday', open: '10:00 AM', close: '07:00 PM' },
  { day: 'Friday', open: '10:00 AM', close: '07:00 PM' },
  { day: 'Saturday', open: '10:00 AM', close: '06:00 PM' },
  { day: 'Sunday', open: '11:00 AM', close: '05:00 PM' }
];





