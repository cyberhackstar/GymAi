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

  // Live observables for components to subscribe to
  token$ = new BehaviorSubject<string | null>(this.getToken());
  refreshToken$ = new BehaviorSubject<string | null>(this.getRefreshToken());
  userId$ = new BehaviorSubject<string | null>(this.getUserId());
  name$ = new BehaviorSubject<string | null>(this.getName());
  email$ = new BehaviorSubject<string | null>(this.getEmail());

  constructor() {
    // Listen for localStorage changes across tabs/windows
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

  // ===== Access Token =====
  getToken(): string | null {
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
    return localStorage.getItem(this.refreshTokenKey);
  }

  setRefreshToken(refreshToken: string): void {
    localStorage.setItem(this.refreshTokenKey, refreshToken);
    this.refreshToken$.next(refreshToken);
  }

  // ===== Clear All =====
  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    localStorage.removeItem(this.userIdKey);
    localStorage.removeItem(this.nameKey);
    localStorage.removeItem(this.emailKey);

    this.syncFromStorage();
  }

  // ===== User Info =====
  getUserId(): string | null {
    return localStorage.getItem(this.userIdKey);
  }

  getName(): string | null {
    return localStorage.getItem(this.nameKey);
  }

  getEmail(): string | null {
    return localStorage.getItem(this.emailKey);
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
    this.token$.next(this.getToken());
    this.refreshToken$.next(this.getRefreshToken());
    this.userId$.next(this.getUserId());
    this.name$.next(this.getName());
    this.email$.next(this.getEmail());
  }
}
