import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  Observable,
  BehaviorSubject,
  tap,
  catchError,
  throwError,
  of,
  timer,
} from 'rxjs';
import { Token } from './token';
import { environment } from '../../../environments/environment';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  name: string;
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface UserInfo {
  id: number;
  email: string;
  name: string;
  role: string;
  provider: string;
  profileCompleted: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = environment.authUrl + '/api/auth'; // Adjust this to your API base URL
  private currentUserSubject = new BehaviorSubject<UserInfo | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  // Cache user info to avoid frequent API calls
  private userInfoCache: UserInfo | null = null;
  private userInfoCacheTime: number = 0;
  private readonly CACHE_DURATION = 5 * 60 * 1000; // 5 minutes cache

  // Track validation state to avoid duplicate calls
  private isValidating = false;
  private lastValidationTime = 0;
  private readonly VALIDATION_COOLDOWN = 30 * 1000; // 30 seconds cooldown

  constructor(private http: HttpClient, private tokenService: Token) {
    // Initialize current user from token if available
    this.initializeFromToken();

    // Set up periodic token refresh check
    this.setupTokenRefreshCheck();
  }

  private initializeFromToken(): void {
    const token = this.tokenService.getToken();
    if (token && !this.tokenService.isTokenExpired()) {
      // Extract user info from token instead of making API call
      this.extractUserFromToken(token);
    }
  }

  private extractUserFromToken(token: string): void {
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (payload) {
        const userInfo: UserInfo = {
          id: payload.userId,
          email: payload.email,
          name: payload.name,
          role: payload.role,
          provider: payload.provider,
          profileCompleted: payload.profileCompleted,
        };
        this.currentUserSubject.next(userInfo);
        this.userInfoCache = userInfo;
        this.userInfoCacheTime = Date.now();
      }
    } catch (error) {
      console.error('Error extracting user from token:', error);
    }
  }

  private setupTokenRefreshCheck(): void {
    // Check token expiration every 5 minutes
    timer(0, 5 * 60 * 1000).subscribe(() => {
      const token = this.tokenService.getToken();
      if (token && this.tokenService.isTokenExpiringSoon(10)) {
        console.log('Token expiring soon, attempting refresh...');
        this.refreshTokenSilently();
      }
    });
  }

  private refreshTokenSilently(): void {
    const refreshToken = this.tokenService.getRefreshToken();
    if (refreshToken) {
      this.refreshToken(refreshToken).subscribe({
        next: () => console.log('Token refreshed silently'),
        error: (error) => {
          console.log('Silent token refresh failed:', error);
          this.logout();
        },
      });
    }
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/login`, credentials)
      .pipe(
        tap((response) => {
          this.handleAuthResponse(response);
        }),
        catchError((error) => {
          console.error('Login error:', error);
          return throwError(() => error);
        })
      );
  }

  register(userData: RegisterRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/register`, userData)
      .pipe(
        tap((response) => {
          this.handleAuthResponse(response);
        }),
        catchError((error) => {
          console.error('Registration error:', error);
          return throwError(() => error);
        })
      );
  }

  refreshToken(refreshToken: string): Observable<AuthResponse> {
    const request: RefreshTokenRequest = { refreshToken };
    return this.http
      .post<AuthResponse>(`${this.baseUrl}/refresh`, request)
      .pipe(
        tap((response) => {
          this.handleAuthResponse(response);
        }),
        catchError((error) => {
          console.error('Token refresh error:', error);
          this.logout();
          return throwError(() => error);
        })
      );
  }

  // Optimized getCurrentUser - uses cache when possible
  getCurrentUser(): Observable<UserInfo> {
    // Return cached data if available and fresh
    if (this.userInfoCache && this.isCacheValid()) {
      console.log('Returning cached user info');
      return of(this.userInfoCache);
    }

    // Check if we can extract from token
    const token = this.tokenService.getToken();
    if (token && !this.tokenService.isTokenExpired()) {
      this.extractUserFromToken(token);
      if (this.userInfoCache) {
        return of(this.userInfoCache);
      }
    }

    // Only call API if cache is invalid and we have a valid token
    console.log('Fetching user info from API');
    return this.http.get<UserInfo>(`${this.baseUrl}/me`).pipe(
      tap((user) => {
        this.currentUserSubject.next(user);
        this.userInfoCache = user;
        this.userInfoCacheTime = Date.now();
      }),
      catchError((error) => {
        console.error('Get current user error:', error);
        // Don't clear cache on error - might be temporary network issue
        return throwError(() => error);
      })
    );
  }

  // Optimized token validation - reduces API calls
  validateToken(): Observable<any> {
    // Skip validation if done recently
    const now = Date.now();
    if (
      this.isValidating ||
      now - this.lastValidationTime < this.VALIDATION_COOLDOWN
    ) {
      console.log('Skipping token validation - done recently');
      return of({ valid: true });
    }

    // Check token locally first
    const token = this.tokenService.getToken();
    if (!token) {
      return throwError(() => new Error('No token available'));
    }

    if (this.tokenService.isTokenExpired()) {
      console.log('Token expired locally');
      return throwError(() => new Error('Token expired'));
    }

    // Only validate with server if necessary
    this.isValidating = true;
    this.lastValidationTime = now;

    return this.http.get(`${this.baseUrl}/validate`).pipe(
      tap(() => {
        console.log('Server token validation successful');
      }),
      catchError((error) => {
        console.error('Token validation error:', error);
        return throwError(() => error);
      }),
      tap(() => {
        this.isValidating = false;
      })
    );
  }

  logout(): Observable<any> {
    const logoutRequest = this.http.post(`${this.baseUrl}/logout`, {}).pipe(
      catchError((error) => {
        console.error('Logout error:', error);
        return of(null); // Don't fail logout on server error
      })
    );

    // Clear all local data
    this.tokenService.clearToken();
    this.currentUserSubject.next(null);
    this.userInfoCache = null;
    this.userInfoCacheTime = 0;

    return logoutRequest;
  }

  private handleAuthResponse(response: AuthResponse): void {
    if (response.accessToken) {
      this.tokenService.setToken(response.accessToken);
      // Extract user info from new token
      this.extractUserFromToken(response.accessToken);
    }
    if (response.refreshToken) {
      this.tokenService.setRefreshToken(response.refreshToken);
    }
  }

  private isCacheValid(): any {
    return (
      this.userInfoCache &&
      Date.now() - this.userInfoCacheTime < this.CACHE_DURATION
    );
  }

  // Quick authentication check without API calls
  isAuthenticated(): boolean {
    return (
      this.tokenService.hasValidAccessToken() ||
      this.tokenService.canRefreshToken()
    );
  }

  // Quick admin check from local data
  isAdmin(): boolean {
    // Check from cache first
    if (this.userInfoCache && this.isCacheValid()) {
      return this.userInfoCache.role === 'ADMIN';
    }

    // Fallback to token
    return this.tokenService.isAdmin();
  }

  // Quick role check from local data
  hasRole(role: string): boolean {
    // Check from cache first
    if (this.userInfoCache && this.isCacheValid()) {
      return this.userInfoCache.role === role;
    }

    // Fallback to token
    return this.tokenService.hasRole(role);
  }

  // Get current user without API call
  getCurrentUserValue(): UserInfo | null {
    if (this.userInfoCache && this.isCacheValid()) {
      return this.userInfoCache;
    }
    return this.currentUserSubject.value;
  }

  // OAuth callback handling
  handleOAuthCallback(accessToken: string, refreshToken: string): void {
    this.tokenService.setToken(accessToken);
    this.tokenService.setRefreshToken(refreshToken);

    // Extract user info from token instead of API call
    this.extractUserFromToken(accessToken);
  }

  // Force refresh user info (bypasses cache)
  refreshUserInfo(): Observable<UserInfo> {
    this.userInfoCache = null;
    this.userInfoCacheTime = 0;
    return this.getCurrentUser();
  }

  // Clear all caches
  clearCache(): void {
    this.userInfoCache = null;
    this.userInfoCacheTime = 0;
    this.lastValidationTime = 0;
    this.isValidating = false;
  }
}
