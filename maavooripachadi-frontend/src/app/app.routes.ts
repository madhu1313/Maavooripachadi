import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./pages/home/home.page').then((m) => m.HomePage) },
  { path: 'shop', loadComponent: () => import('./pages/shop/shop.page').then((m) => m.ShopPage) },
  {
    path: 'collections/:handle',
    loadComponent: () => import('./pages/shop/shop.page').then((m) => m.ShopPage)
  },
  {
    path: 'product/:id',
    loadComponent: () => import('./pages/product/product.page').then((m) => m.ProductPage)
  },
  {
    path: 'collections',
    redirectTo: 'shop',
    pathMatch: 'full'
  },
  {
    path: 'gifting',
    redirectTo: 'collections/gifts',
    pathMatch: 'full'
  },
  { path: 'cart', loadComponent: () => import('./pages/cart/cart.page').then((m) => m.CartPage) },
  {
    path: 'checkout',
    loadComponent: () => import('./pages/checkout/checkout.page').then((m) => m.CheckoutPage)
  },
  {
    path: 'order/success',
    redirectTo: 'success',
    pathMatch: 'full'
  },
  {
    path: 'success',
    loadComponent: () => import('./pages/success/success.page').then((m) => m.SuccessPage)
  },
  {
    path: 'recipes',
    loadComponent: () => import('./pages/recipes/recipes.page').then((m) => m.RecipesPage)
  },
  { path: 'blog', loadComponent: () => import('./pages/blog/blog.page').then((m) => m.BlogPage) },
  {
    path: 'stories',
    redirectTo: 'blog',
    pathMatch: 'full'
  },
  { path: 'about', loadComponent: () => import('./pages/about/about.page').then((m) => m.AboutPage) },
  {
    path: 'contact',
    loadComponent: () => import('./pages/contact/contact.page').then((m) => m.ContactPage)
  },
  { path: 'track', loadComponent: () => import('./pages/track/track.page').then((m) => m.TrackPage) },
  { path: 'support', loadComponent: () => import('./pages/support/support.page').then((m) => m.SupportPage) },
  { path: 'account', loadComponent: () => import('./pages/account/account.page').then((m) => m.AccountPage) },
  { path: 'admin', loadComponent: () => import('./pages/admin/admin.page').then((m) => m.AdminPage) },
  {
    path: 'wishlist',
    loadComponent: () => import('./pages/account/account.page').then((m) => m.AccountPage)
  },
  { path: '**', redirectTo: '' }
];
