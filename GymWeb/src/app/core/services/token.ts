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
  private roleKey = 'user_role';
  private profileCompletedKey = 'profile_completed';

  // Initialize BehaviorSubjects with null first, then sync after
  token$ = new BehaviorSubject<string | null>(null);
  refreshToken$ = new BehaviorSubject<string | null>(null);
  userId$ = new BehaviorSubject<string | null>(null);
  name$ = new BehaviorSubject<string | undefined>(undefined);
  email$ = new BehaviorSubject<string | undefined>(undefined);
  role$ = new BehaviorSubject<string | undefined>(undefined);
  profileCompleted$ = new BehaviorSubject<boolean>(false);

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
          event.key === this.emailKey ||
          event.key === this.roleKey ||
          event.key === this.profileCompletedKey
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
        // Token expired → clear it but keep refresh token for potential refresh
        localStorage.removeItem(this.tokenKey);
        this.token$.next(null);
        console.log('Access token expired, removed from storage');
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
      if (decoded.role) localStorage.setItem(this.roleKey, decoded.role);
      if (typeof decoded.profileCompleted === 'boolean') {
        localStorage.setItem(
          this.profileCompletedKey,
          decoded.profileCompleted.toString()
        );
      }
    }

    // Sync observables
    this.syncFromStorage();
  }

  // ===== Refresh Token =====
  getRefreshToken(): string | null {
    if (typeof localStorage === 'undefined') return null;

    const refreshToken = localStorage.getItem(this.refreshTokenKey);
    if (!refreshToken) return null;

    // Check if refresh token is expired
    const decoded = this.decodeToken(refreshToken);
    if (decoded?.exp) {
      const now = Math.floor(Date.now() / 1000);
      if (decoded.exp < now) {
        console.log('Refresh token expired, clearing all tokens');
        this.clearToken();
        return null;
      }
    }

    return refreshToken;
  }

  setRefreshToken(refreshToken: string): void {
    if (typeof localStorage === 'undefined') return;

    localStorage.setItem(this.refreshTokenKey, refreshToken);
    this.refreshToken$.next(refreshToken);
  }

  // ===== Token Expiry Checks =====
  isTokenExpired(): boolean {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) return true;

    const decoded = this.decodeToken(token);
    if (!decoded?.exp) return true;

    const now = Math.floor(Date.now() / 1000);
    return decoded.exp < now;
  }

  isTokenExpiringSoon(minutesBeforeExpiry: number = 5): boolean {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) return true;

    const decoded = this.decodeToken(token);
    if (!decoded?.exp) return true;

    const now = Math.floor(Date.now() / 1000);
    const expiryThreshold = now + minutesBeforeExpiry * 60;

    return decoded.exp < expiryThreshold;
  }

  getTokenExpirationTime(): number | null {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) return null;

    const decoded = this.decodeToken(token);
    if (!decoded?.exp) return null;

    return decoded.exp * 1000; // Convert to milliseconds
  }

  // ===== Clear All =====
  clearToken(): void {
    if (typeof localStorage === 'undefined') return;

    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshTokenKey);
    localStorage.removeItem(this.userIdKey);
    localStorage.removeItem(this.nameKey);
    localStorage.removeItem(this.emailKey);
    localStorage.removeItem(this.roleKey);
    localStorage.removeItem(this.profileCompletedKey);

    this.syncFromStorage();
  }

  // Clear only access token (keep refresh token)
  clearAccessToken(): void {
    if (typeof localStorage === 'undefined') return;

    localStorage.removeItem(this.tokenKey);
    // Don't clear user details as they might be needed for refresh
    this.token$.next(null);
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

  getRole(): string | undefined {
    if (typeof localStorage === 'undefined') return undefined;
    return localStorage.getItem(this.roleKey) ?? undefined;
  }

  isProfileCompleted(): boolean {
    if (typeof localStorage === 'undefined') return false;
    const completed = localStorage.getItem(this.profileCompletedKey);
    return completed === 'true';
  }

  // ===== Convenience Methods =====
  isAuthenticated(): boolean {
    return this.getToken() !== null || this.getRefreshToken() !== null;
  }

  hasValidAccessToken(): boolean {
    return this.getToken() !== null;
  }

  canRefreshToken(): boolean {
    return this.getRefreshToken() !== null;
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  hasRole(role: string): boolean {
    return this.getRole() === role;
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
      this.email$ &&
      this.role$ &&
      this.profileCompleted$
    ) {
      this.token$.next(this.getToken());
      this.refreshToken$.next(this.getRefreshToken());
      this.userId$.next(this.getUserId());
      this.name$.next(this.getName());
      this.email$.next(this.getEmail());
      this.role$.next(this.getRole());
      this.profileCompleted$.next(this.isProfileCompleted());
    }
  }

  // ===== Debug Methods =====
  getTokenInfo(): any {
    const token = this.getToken();
    const refreshToken = this.getRefreshToken();

    return {
      hasAccessToken: !!token,
      hasRefreshToken: !!refreshToken,
      isTokenExpired: this.isTokenExpired(),
      isTokenExpiringSoon: this.isTokenExpiringSoon(),
      tokenExpirationTime: this.getTokenExpirationTime(),
      userInfo: {
        id: this.getUserId(),
        email: this.getEmail(),
        name: this.getName(),
        role: this.getRole(),
        profileCompleted: this.isProfileCompleted(),
      },
    };
  }
}
