export const environment = {
  production: true,
  apiBase: '/api/v1',
  assetsBaseUrl: 'https://maavooripachadi.com/assets',
  site: {
    name: 'Maavoori Pachadi',
    baseUrl: 'https://maavooripachadi.com',
    tagline: 'Heritage Andhra pickles, sun-cured for modern kitchens',
    supportEmail: 'support@maavooripachadi.com',
    supportPhone: '+91 85558 59667',
    whatsappLink: 'https://wa.me/918555859667'
  },
  payments: {
    razorpayKey: 'rzp_test_RUbsNadTP2WNOd'
  },
  seo: {
    defaultTitle: 'Maavoori Pachadi | Heritage Andhra Pickles & Pantry Staples',
    defaultDescription:
      'Discover handcrafted Andhra and Telangana pickles, podis and snacks slow-cured in micro batches and delivered fresh across India.',
    twitterHandle: '@maavooripachadi'
  },
  featureFlags: {
    enableMarquee: true,
    enableWhatsappWidget: true,
    enableWishlist: true,
    enableBlog: true
  },
  storefront: {
    trendingSearches: ['Mango Avakai', 'Gongura Mutton', 'Sweet Avakai', 'Millet Murukulu'],
    testimonialLimit: 8,
    instagramFeedLimit: 8
  }
};
