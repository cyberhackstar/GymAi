import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class Token {
  private tokenKey = 'access_token';
  private refreshTokenKey = 'refresh_token';
  private userIdKey = 'user_id';
  private nameKey = 'user_name';
  private emailKey = 'user_email';

  // Initialize BehaviorSubjects with null first, then sync after
  token$ = new BehaviorSubject<string | null>(null);
  refreshToken$ = new BehaviorSubject<string | null>(null);
  userId$ = new BehaviorSubject<string | null>(null);
  name$ = new BehaviorSubject<string | undefined>(undefined);
  email$ = new BehaviorSubject<string | undefined>(undefined);

  constructor() {
    // Sync from storage after BehaviorSubjects are initialized
    this.syncFromStorage();

    // Listen for localStorage changes across tabs/windows
    if (typeof window !== 'undefined') {
      window.addEventListener('storage', (event) => {
        if (
          event.key === this.tokenKey ||
          event.key === this.refreshTokenKey ||
          event.key === this.userIdKey ||
          event.key === this.nameKey ||
          event.key === this.emailKey
        ) {
          this.syncFromStorage();
        }
      });
    }
  }

  // ===== Access Token =====
  getToken(): string | null {
    if (typeof localStorage === 'undefined') return null;

    const token = localStorage.getItem(this.tokenKey);
    if (!token) return null;

    const decoded = this.decodeToken(token);
    if (decoded?.exp) {
      const now = Math.floor(Date.now() / 1000);
      if (decoded.exp < now) {
        // Token expired → clear it
        this.clearToken();
        return null;
      }
    }

    return token;
  }

  setToken(token: string): void {
    if (typeof localStorage === 'undefined') return;

    localStorage.setItem(this.tokenKey, token);

    // Decode token and store user details
    const decoded = this.decodeToken(token);
    if (decoded) {
      if (decoded.userId)
        localStorage.setItem(this.userIdKey, decoded.userId.toString());
      if (decoded.name) localStorage.setItem(this.nameKey, decoded.name);
      if (decoded.email) localStorage.setItem(this.emailKey, decoded.email);
    }

    // Sync observables
    this.syncFromStorage();
  }

  // ===== Refresh Token =====
  getRefreshToken(): string | null {
    if (typeof localStorage === 'undefined') return null;
    return localStorage.getItem(this.refreshTokenKey);
  }

  setRefreshToken(refreshToken: string): void {
    if (typeof localStorage === 'undefined') return;

    localStorage.setItem(this.refreshTokenKey, refreshToken);
    this.refreshToken$.next(refreshToken);
  }

  // ===== Clear All =====
  clearToken(): void {
    if (typeof localStorage === 'undefined') return;

    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    localStorage.removeItem(this.userIdKey);
    localStorage.removeItem(this.nameKey);
    localStorage.removeItem(this.emailKey);

    this.syncFromStorage();
  }

  // ===== User Info =====
  getUserId(): string | null {
    if (typeof localStorage === 'undefined') return null;
    return localStorage.getItem(this.userIdKey);
  }

  getName(): string | undefined {
    if (typeof localStorage === 'undefined') return undefined;
    return localStorage.getItem(this.nameKey) ?? undefined;
  }

  getEmail(): string | undefined {
    if (typeof localStorage === 'undefined') return undefined;
    return localStorage.getItem(this.emailKey) ?? undefined;
  }

  // ===== Decode JWT =====
  private decodeToken(token: string): any | null {
    try {
      const payload = token.split('.')[1];
      const decodedPayload = atob(payload);
      return JSON.parse(decodedPayload);
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  // ===== Sync Observables with Storage =====
  private syncFromStorage(): void {
    // Only sync if BehaviorSubjects are initialized
    if (
      this.token$ &&
      this.refreshToken$ &&
      this.userId$ &&
      this.name$ &&
      this.email$
    ) {
      this.token$.next(this.getToken());
      this.refreshToken$.next(this.getRefreshToken());
      this.userId$.next(this.getUserId());
      this.name$.next(this.getName());
      this.email$.next(this.getEmail());
    }
  }
}
