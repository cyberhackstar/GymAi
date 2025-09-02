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
  ],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
})
export class Login {
  loginForm: FormGroup;
  errorMessage = '';
  showPassword = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private tokenService: Token,
    private userService: UserProfileService,
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

          this.userService.isProfileCompleted().subscribe({
            next: (completed) => {
              if (completed) {
                this.router.navigate(['/dashboard']);
              } else {
                this.router.navigate(['/complete-profile']);
              }
            },
            error: () => {
              this.router.navigate(['/complete-profile']);
            },
          });
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

    // ✅ Set cookie with proper domain configuration
    this.setFrontendOriginCookie(currentOrigin);

    // ✅ Add frontend origin as query parameter as fallback
    const oauthUrl = `${
      environment.authUrl
    }/oauth2/authorization/${provider}?frontend_origin=${encodeURIComponent(
      currentOrigin
    )}`;

    console.log('Redirecting to OAuth URL:', oauthUrl);
    window.location.href = oauthUrl;
  }

  private setFrontendOriginCookie(origin: string): void {
    const isProduction = !origin.includes('localhost');

    if (isProduction) {
      const domain = this.extractDomain(origin);

      // ✅ Production cookie: shared across subdomains, HTTPS only
      document.cookie = `frontend_origin=${origin}; path=/; domain=${domain}; max-age=600; SameSite=None; Secure`;
      console.log(`Set production cookie for domain: ${domain}`);
    } else {
      // ✅ Development: no domain, not secure
      document.cookie = `frontend_origin=${origin}; path=/; max-age=600; SameSite=Lax`;
      console.log('Set development cookie');
    }
  }

  private extractDomain(url: string): string {
    try {
      const urlObj = new URL(url);
      const hostname = urlObj.hostname;
      const parts = hostname.split('.');

      // ✅ Always take the last two parts (e.g. "neelahouse.cloud")
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
