import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

const ACCESS_TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (!isBrowserEnvironment()) {
    return next(req);
  }

  const token = localStorage.getItem(ACCESS_TOKEN_KEY);
  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
        'X-Maavoori-Client': 'maavooripachadi.com'
      }
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => handleAuthError(error, req.url))
  );
};

function handleAuthError(error: HttpErrorResponse, url: string) {
  if (!isBrowserEnvironment()) {
    return throwError(() => error);
  }

  if (error.status === 401 || error.status === 403) {
    clearSession();
    const router = inject(Router);
    const currentRoute = router.url;
    const redirectTo = shouldPreserveUrl(currentRoute) ? currentRoute : undefined;
    const queryParams = redirectTo ? { redirectTo } : undefined;
    router.navigate(['/account'], { queryParams }).catch(() => undefined);
  }

  return throwError(() => error);
}

function clearSession(): void {
  try {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem('maavoori.session');
  } catch {
    // ignore storage failures
  }
}

function shouldPreserveUrl(url: string): boolean {
  if (!url) {
    return false;
  }
  return url.includes('/admin') || url.includes('/account');
}

function isBrowserEnvironment(): boolean {
  return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined';
}
