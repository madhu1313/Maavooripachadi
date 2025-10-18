import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';
import { ApiService } from './api.service';

type Role = 'ADMIN' | 'SUPER_ADMIN' | 'OPERATIONS_MANAGER' | 'CUSTOMER' | 'GUEST';

export interface LoginResponse {
  accessToken: string;
  refreshToken?: string;
  expiresIn?: number;
  roles: Role[];
  mfaRequired?: boolean;
}

export interface LoginRequest {
  identifier: string;
  password: string;
  otp?: string;
  deviceId?: string;
}

export interface RegisterRequest {
  identifier: string;
  password: string;
  fullName?: string;
}

export interface ProfileResponse {
  identifier: string;
  email?: string;
  phone?: string;
  name?: string;
  roles: Role[];
  authorities?: string[];
  mfaEnabled?: boolean;
  mfaRequired?: boolean;
  forcePasswordReset?: boolean;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private readonly http: HttpClient, private readonly api: ApiService) {}

  login(payload: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.api.url('authLogin'), payload).pipe(
      tap(response => this.persistSession(response))
    );
  }

  register(payload: RegisterRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.api.url('authRegister'), payload).pipe(
      tap(response => this.persistSession(response))
    );
  }

  refresh(): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.api.url('authRefresh'), { refreshToken: this.getRefreshToken() }).pipe(
      tap(response => this.persistSession(response))
    );
  }

  me(): Observable<ProfileResponse> {
    return this.http.get<ProfileResponse>(this.api.url('authMe'));
  }

  logout(): void {
    this.clearSession();
  }

  isAuthenticated(): boolean {
    return Boolean(this.getAccessToken());
  }

  storeSession(session: SessionState): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, session.accessToken);
    if (session.refreshToken) {
      localStorage.setItem(REFRESH_TOKEN_KEY, session.refreshToken);
    }
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
  }

  getAccessToken(): string | null {
    return typeof localStorage !== 'undefined' ? localStorage.getItem(ACCESS_TOKEN_KEY) : null;
  }

  getRefreshToken(): string | null {
    return typeof localStorage !== 'undefined' ? localStorage.getItem(REFRESH_TOKEN_KEY) : null;
  }

  private persistSession(response: LoginResponse): void {
    const session: SessionState = {
      accessToken: response.accessToken,
      refreshToken: response.refreshToken,
      authorities: response.roles,
      mfaRequired: response.mfaRequired ?? false
    };
    this.storeSession(session);
  }

  private clearSession(): void {
    if (typeof localStorage === 'undefined') {
      return;
    }
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    localStorage.removeItem(SESSION_KEY);
  }
}

const ACCESS_TOKEN_KEY = 'accessToken';
const REFRESH_TOKEN_KEY = 'refreshToken';
const SESSION_KEY = 'maavoori.session';

type SessionState = {
  accessToken: string;
  refreshToken?: string;
  authorities: Role[];
  mfaRequired: boolean;
};
