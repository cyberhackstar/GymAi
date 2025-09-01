// oauth-callback.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Token } from '../core/services/token';

@Component({
  selector: 'app-oauth-callback',
  template: `
    <div class="flex justify-center items-center h-screen">
      <div class="text-center">
        <div
          class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto"
        ></div>
        <p class="mt-4 text-gray-600">Processing login...</p>
      </div>
    </div>
  `,
  standalone: true,
})
export class OAuthCallbackComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tokenService: Token
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const accessToken = params['access_token'];
      const refreshToken = params['refresh_token'];
      const profileCompleted = params['profile_completed'] === 'true';

      if (accessToken && refreshToken) {
        // ✅ Store tokens
        this.tokenService.setToken(accessToken);
        this.tokenService.setRefreshToken(refreshToken);

        // ✅ Get the intended redirect URL from sessionStorage
        const intendedRedirect = sessionStorage.getItem('oauth_redirect');
        sessionStorage.removeItem('oauth_redirect'); // Clean up

        // ✅ Navigate to intended page or default
        const redirectTo = intendedRedirect || '/plan-dashboard';

        console.log('OAuth login successful, redirecting to:', redirectTo);
        this.router.navigate([redirectTo]);
      } else {
        // ✅ Handle error case
        console.error('OAuth callback missing tokens');
        this.router.navigate(['/login'], {
          queryParams: { error: 'oauth_failed' },
        });
      }
    });
  }
}
