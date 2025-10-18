import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';

type GuardResult = boolean | UrlTree;

const ACCESS_TOKEN_KEY = 'accessToken';
const AUTHORITIES_KEYS = ['maavoori.authorities', 'authorities'];
const SESSION_KEY = 'maavoori.session';
const ADMIN_ROLES = ['ADMIN', 'SUPER_ADMIN', 'OPERATIONS_MANAGER'];

export const adminGuard: CanActivateFn = (_route, state) => {
  const router = inject(Router);

  if (!isBrowserEnvironment()) {
    return router.createUrlTree(['/account'], { queryParams: { redirectTo: state.url } });
  }

  const token = localStorage.getItem(ACCESS_TOKEN_KEY);
  if (!token) {
    return router.createUrlTree(['/account'], { queryParams: { redirectTo: state.url } });
  }

  const roles = resolveAuthorities();
  if (roles.some(role => ADMIN_ROLES.includes(role.toUpperCase()))) {
    return true;
  }

  return router.createUrlTree(['/']);
};

function resolveAuthorities(): string[] {
  for (const key of AUTHORITIES_KEYS) {
    const stored = localStorage.getItem(key);
    const parsed = parseAuthorities(stored);
    if (parsed.length) {
      return parsed;
    }
  }

  const sessionRaw = localStorage.getItem(SESSION_KEY);
  if (sessionRaw) {
    const session = safeJsonParse<Record<string, unknown>>(sessionRaw);
    const parsed = parseAuthorities(session?.['authorities'] ?? null);
    if (parsed.length) {
      return parsed;
    }
  }

  return [];
}

function parseAuthorities(value: unknown): string[] {
  if (!value) {
    return [];
  }

  if (Array.isArray(value)) {
    return value.map(String);
  }

  if (typeof value === 'string') {
    try {
      const asJson = JSON.parse(value);
      if (Array.isArray(asJson)) {
        return asJson.map(String);
      }
    } catch {
      return value.split(',').map(v => v.trim()).filter(Boolean);
    }
  }

  return [];
}

function safeJsonParse<T = unknown>(raw: string): T | null {
  try {
    return JSON.parse(raw) as T;
  } catch {
    return null;
  }
}

function isBrowserEnvironment(): boolean {
  return typeof window !== 'undefined' && typeof window.localStorage !== 'undefined';
}
