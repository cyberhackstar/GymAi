// oauth-callback.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Token } from '../core/services/token';
import { LoadingSpinner } from '../shared/loading-spinner/loading-spinner';

@Component({
  selector: 'app-oauth-callback',
  template: `<app-loading-spinner message="Getting Ready...">
  </app-loading-spinner>`,
  standalone: true,
  imports: [CommonModule, LoadingSpinner],
})
export class OAuthCallbackComponent implements OnInit {
  debugInfo = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tokenService: Token
  ) {}

  ngOnInit(): void {
    console.log('OAuth Callback component initialized');
    console.log('Current URL:', window.location.href);

    // Check for error parameters first
    this.route.queryParams.subscribe((params) => {
      console.log('OAuth callback received params:', params);

      // Handle OAuth errors
      if (params['error']) {
        console.error('OAuth error received:', params['error']);
        this.debugInfo = `OAuth error: ${params['error']}`;
        setTimeout(() => {
          this.router.navigate(['/login'], {
            queryParams: {
              error: params['error'],
              reason: params['error_description'] || 'oauth_error',
            },
          });
        }, 2000);
        return;
      }

      const accessToken = params['access_token'];
      const refreshToken = params['refresh_token'];
      const profileCompleted = params['profile_completed'] === 'true';

      this.debugInfo = `Tokens received: ${!!accessToken && !!refreshToken}`;

      if (accessToken && refreshToken) {
        console.log('✅ Tokens received successfully');
        console.log('Access token length:', accessToken.length);
        console.log('Refresh token length:', refreshToken.length);
        console.log('Profile completed:', profileCompleted);

        // ✅ Store tokens
        this.tokenService.setToken(accessToken);
        this.tokenService.setRefreshToken(refreshToken);

        // ✅ Get the intended redirect URL from sessionStorage
        const intendedRedirect = sessionStorage.getItem('oauth_redirect');
        sessionStorage.removeItem('oauth_redirect'); // Clean up

        // ✅ Navigate to intended page or default
        const redirectTo = intendedRedirect || '/plan-dashboard';

        console.log('🔄 OAuth login successful, redirecting to:', redirectTo);
        this.debugInfo = `Redirecting to ${redirectTo}...`;

        // ✅ Small delay to show the success message
        setTimeout(() => {
          this.router.navigate([redirectTo]).then((success) => {
            if (success) {
              console.log('✅ Navigation successful');
            } else {
              console.error('❌ Navigation failed');
              this.router.navigate(['/dashboard']); // Fallback
            }
          });
        }, 1000);
      } else {
        // ✅ Handle error case
        console.error('❌ OAuth callback missing tokens');
        console.error('Available params:', Object.keys(params));
        this.debugInfo = 'OAuth failed - missing tokens';

        setTimeout(() => {
          this.router.navigate(['/login'], {
            queryParams: { error: 'oauth_failed', reason: 'missing_tokens' },
          });
        }, 2000);
      }
    });
  }
}
