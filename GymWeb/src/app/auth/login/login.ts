// login.component.ts - Fixed version
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Token } from '../../core/services/token';
import { Router, RouterModule } from '@angular/router';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { MatIconModule } from '@angular/material/icon';
import { AuthService } from '../../core/services/auth';
import { LoginRequest, LoginResponse } from '../../models/auth.model';
import { UserProfileService } from '../../core/services/user';
import { environment } from '../../../environments/environment';
import { LoadingSpinner } from '../../shared/loading-spinner/loading-spinner';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    MatIconModule,
    RouterModule,
    NgOptimizedImage,
    // LoadingSpinner,
  ],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {
  loginForm: FormGroup;
  errorMessage = '';
  showPassword = false;
  isLoading: boolean = false;
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private tokenService: Token,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
    });
  }

  togglePassword(): void {
    this.showPassword = !this.showPassword;
  }

  onSubmit(): void {
    this.isLoading = true;
    if (this.loginForm.invalid) {
      this.errorMessage = 'Please enter a valid email and password.';
      return;
    }

    const payload: LoginRequest = this.loginForm.value;

    this.authService.login(payload).subscribe({
      next: (res: LoginResponse) => {
        if (res && res.accessToken && res.refreshToken) {
          this.tokenService.setToken(res.accessToken);
          this.tokenService.setRefreshToken(res.refreshToken);

          this.router.navigate(['/plan-dashboard']);
        } else {
          this.errorMessage = 'Login failed: Tokens not received.';
        }
      },
      error: (err) => {
        console.error('Login error:', err);
        this.errorMessage = 'Invalid credentials. Please try again.';
      },
    });
  }

  loginWithGoogle(): void {
    this.performOAuth2Login('google');
  }

  loginWithGitHub(): void {
    this.performOAuth2Login('github');
  }

  private performOAuth2Login(provider: 'google' | 'github'): void {
    // Store redirect URL in sessionStorage
    sessionStorage.setItem('oauth_redirect', '/plan-dashboard');

    // Get current origin
    const currentOrigin = window.location.origin;
    console.log('Current origin for OAuth:', currentOrigin);

    // Set multiple fallback mechanisms
    this.setFrontendOriginCookie(currentOrigin);
    this.setFrontendOriginInSession(currentOrigin);

    // Create OAuth URL with frontend origin as query parameter
    const oauthUrl = `${
      environment.authUrl
    }/oauth2/authorization/${provider}?frontend_origin=${encodeURIComponent(
      currentOrigin
    )}`;

    console.log('Redirecting to OAuth URL:', oauthUrl);

    // Add a small delay to ensure cookie is set
    setTimeout(() => {
      window.location.href = oauthUrl;
    }, 100);
  }

  private setFrontendOriginInSession(origin: string): void {
    // Use fetch to set session attribute on backend
    fetch(`${environment.authUrl}/api/auth/set-frontend-origin`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ frontendOrigin: origin }),
      credentials: 'include', // Important for session cookies
    }).catch((error) => {
      console.warn('Could not set frontend origin in session:', error);
    });
  }

  private setFrontendOriginCookie(origin: string): void {
    const isProduction = !origin.includes('localhost');

    if (isProduction) {
      // Production: Set cookie with proper domain
      const domain = this.extractDomain(origin);

      // Set both with and without domain for maximum compatibility
      document.cookie = `frontend_origin=${origin}; path=/; domain=${domain}; max-age=600; SameSite=None; Secure`;
      document.cookie = `frontend_origin=${origin}; path=/; max-age=600; SameSite=Lax; Secure`;

      console.log(`Set production cookies for domain: ${domain}`);
    } else {
      // Development: Multiple cookie variations
      document.cookie = `frontend_origin=${origin}; path=/; max-age=600; SameSite=Lax`;
      document.cookie = `frontend_origin=${origin}; path=/; max-age=600; SameSite=None`;

      console.log('Set development cookies');
    }
  }

  private extractDomain(url: string): string {
    try {
      const urlObj = new URL(url);
      const hostname = urlObj.hostname;

      // ✅ Extract root domain (e.g., "neelahouse.cloud" from "gymai.neelahouse.cloud")
      const parts = hostname.split('.');
      if (parts.length >= 2) {
        return '.' + parts.slice(-2).join('.');
      }
      return hostname;
    } catch (error) {
      console.error('Error extracting domain:', error);
      return '';
    }
  }
}
