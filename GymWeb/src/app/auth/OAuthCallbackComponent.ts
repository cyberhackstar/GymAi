// oauth-callback.component.ts
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Token } from '../core/services/token';

@Component({
  selector: 'app-oauth-callback',
  template: `
    <div
      style="display: flex; justify-content: center; align-items: center; height: 100vh; background: #0d0d0d; color: white;"
    >
      <div style="text-align: center;">
        <h2>Completing login...</h2>
        <div style="margin-top: 1rem;">
          <div
            style="border: 3px solid #ff4c4c; border-top: 3px solid transparent; border-radius: 50%; width: 40px; height: 40px; animation: spin 1s linear infinite; margin: 0 auto;"
          ></div>
        </div>
      </div>
    </div>
    <style>
      @keyframes spin {
        0% {
          transform: rotate(0deg);
        }
        100% {
          transform: rotate(360deg);
        }
      }
    </style>
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

      if (accessToken && refreshToken) {
        this.tokenService.setToken(accessToken);
        this.tokenService.setRefreshToken(refreshToken);

        // Redirect to dashboard (or based on profile completion)
        const profileCompleted = params['profile_completed'] === 'true';
        this.router.navigate([
          profileCompleted ? '/dashboard' : '/complete-profile',
        ]);
      } else {
        this.router.navigate(['/login'], {
          queryParams: { error: 'oauth_failed' },
        });
      }
    });
  }
}

// Add this route to your app.routes.ts
// { path: 'oauth-callback', component: OAuthCallbackComponent }
