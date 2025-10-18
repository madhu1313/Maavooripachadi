import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';

type GuardResult = boolean | UrlTree;

const ACCESS_TOKEN_KEY = 'accessToken';
const SESSION_KEY = 'maavoori.session';

export const authGuard: CanActivateFn = (_route, state) => {
  const router = inject(Router);

  if (!isBrowserEnvironment()) {
    return createRedirect(router, state.url);
  }

  const tokenPresent = hasAccessToken();
  if (!tokenPresent) {
    return createRedirect(router, state.url);
  }

  const session = resolveSession();

  if (!session) {
    return createRedirect(router, state.url);
  }

  if (session.mfaRequired && !session.mfaVerified) {
    return router.createUrlTree(['/account/mfa'], { queryParams: { redirectTo: state.url } });
  }

  if (session.forcePasswordReset) {
    return router.createUrlTree(['/account/reset-password'], { queryParams: { redirectTo: state.url } });
  }

  return true;
};

function hasAccessToken(): boolean {
  if (!isBrowserEnvironment()) {
    return false;
  }

  const token = localStorage.getItem(ACCESS_TOKEN_KEY);
  return typeof token === 'string' && token.length > 0;
}

function resolveSession(): SessionState | null {
  if (!isBrowserEnvironment()) {
    return null;
  }

  const rawSession = localStorage.getItem(SESSION_KEY);
  if (!rawSession) {
    return null;
  }

  try {
    const parsed = JSON.parse(rawSession) as SessionState;
    return parsed;
  } catch {
    return null;
  }
}

function createRedirect(router: Router, returnUrl: string): GuardResult {
  return router.createUrlTree(['/account/login'], { queryParams: { redirectTo: returnUrl } });
}

function isBrowserEnvironment(): boolean {
  return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined';
}

interface SessionState {
  email?: string;
  authorities?: string[];
  mfaRequired?: boolean;
  mfaVerified?: boolean;
  forcePasswordReset?: boolean;
}
