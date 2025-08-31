// jwt.service.ts - JWT Token Management Service
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface TokenPayload {
  userId: number;
  email: string;
  name: string;
  exp: number;
  iat: number;
}

@Injectable({
  providedIn: 'root',
})
export class JwtService {
  private tokenKey = 'access_token';
  private _token$ = new BehaviorSubject<string | null>(this.getToken());
  private _userInfo$ = new BehaviorSubject<TokenPayload | null>(
    this.getUserInfo()
  );

  constructor() {
    // Listen for storage changes across tabs
    window.addEventListener('storage', (event) => {
      if (event.key === this.tokenKey) {
        this.syncFromStorage();
      }
    });
  }

  getToken(): string | null {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) return null;

    if (this.isTokenExpired(token)) {
      this.clearToken();
      return null;
    }

    return token;
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
    this.syncFromStorage();
  }

  clearToken(): void {
    localStorage.removeItem(this.tokenKey);
    this.syncFromStorage();
  }

  getUserInfo(): TokenPayload | null {
    const token = this.getToken();
    if (!token) return null;

    try {
      const payload = token.split('.')[1];
      const decodedPayload = atob(payload);
      return JSON.parse(decodedPayload) as TokenPayload;
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  getUserEmail(): string | null {
    const userInfo = this.getUserInfo();
    return userInfo?.email || null;
  }

  getUserName(): string | null {
    const userInfo = this.getUserInfo();
    return userInfo?.name || null;
  }

  getUserId(): number | null {
    const userInfo = this.getUserInfo();
    return userInfo?.userId || null;
  }

  isAuthenticated(): boolean {
    return this.getToken() !== null;
  }

  private isTokenExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const now = Math.floor(Date.now() / 1000);
      return payload.exp < now;
    } catch {
      return true;
    }
  }

  private syncFromStorage(): void {
    const token = this.getToken();
    const userInfo = this.getUserInfo();
    this._token$.next(token);
    this._userInfo$.next(userInfo);
  }

  // Observables for components to subscribe to
  get token$() {
    return this._token$.asObservable();
  }

  get userInfo$() {
    return this._userInfo$.asObservable();
  }
}
