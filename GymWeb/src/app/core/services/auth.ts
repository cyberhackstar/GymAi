import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap, catchError, throwError } from 'rxjs';
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

  constructor(private http: HttpClient, private tokenService: Token) {
    // Initialize current user if token exists
    this.initializeCurrentUser();
  }

  private initializeCurrentUser(): void {
    const token = this.tokenService.getToken();
    if (token) {
      this.getCurrentUser().subscribe({
        next: (user) => this.currentUserSubject.next(user),
        error: (error) => {
          console.error('Error initializing current user:', error);
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

  getCurrentUser(): Observable<UserInfo> {
    return this.http.get<UserInfo>(`${this.baseUrl}/me`).pipe(
      tap((user) => {
        this.currentUserSubject.next(user);
      }),
      catchError((error) => {
        console.error('Get current user error:', error);
        return throwError(() => error);
      })
    );
  }

  validateToken(): Observable<any> {
    return this.http.get(`${this.baseUrl}/validate`).pipe(
      catchError((error) => {
        console.error('Token validation error:', error);
        return throwError(() => error);
      })
    );
  }

  logout(): Observable<any> {
    const logoutRequest = this.http.post(`${this.baseUrl}/logout`, {}).pipe(
      catchError((error) => {
        console.error('Logout error:', error);
        // Even if logout fails on server, clear local storage
        return throwError(() => error);
      })
    );

    // Clear tokens and user info
    this.tokenService.clearToken();
    this.currentUserSubject.next(null);

    return logoutRequest;
  }

  private handleAuthResponse(response: AuthResponse): void {
    if (response.accessToken) {
      this.tokenService.setToken(response.accessToken);
    }
    if (response.refreshToken) {
      this.tokenService.setRefreshToken(response.refreshToken);
    }

    // Get updated user info after successful auth
    this.getCurrentUser().subscribe({
      next: (user) => this.currentUserSubject.next(user),
      error: (error) =>
        console.error('Error getting user info after auth:', error),
    });
  }

  isAuthenticated(): boolean {
    const token = this.tokenService.getToken();
    return token !== null;
  }

  isAdmin(): boolean {
    const currentUser = this.currentUserSubject.value;
    return currentUser?.role === 'ADMIN';
  }

  hasRole(role: string): boolean {
    const currentUser = this.currentUserSubject.value;
    return currentUser?.role === role;
  }

  getCurrentUserValue(): UserInfo | null {
    return this.currentUserSubject.value;
  }

  // OAuth callback handling
  handleOAuthCallback(accessToken: string, refreshToken: string): void {
    this.tokenService.setToken(accessToken);
    this.tokenService.setRefreshToken(refreshToken);

    // Get user info after OAuth login
    this.getCurrentUser().subscribe({
      next: (user) => this.currentUserSubject.next(user),
      error: (error) => {
        console.error('Error getting user info after OAuth:', error);
        this.logout();
      },
    });
  }
}
