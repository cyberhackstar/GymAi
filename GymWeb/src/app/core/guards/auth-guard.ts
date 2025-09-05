import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { CanActivateFn } from '@angular/router';
import { map, catchError, of } from 'rxjs';
import { AuthService } from '../services/auth';
import { Token } from '../services/token';

// Ultra-fast Auth Guard - Only checks localStorage, no API calls
export const fastAuthGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(Token);
  const router = inject(Router);

  // Quick local token check - no API calls
  if (tokenService.hasValidAccessToken()) {
    return true;
  }

  console.log('FastAuthGuard: No valid token, redirecting to login');
  router.navigate(['/login'], {
    queryParams: { returnUrl: state.url },
  });
  return false;
};

// Balanced Auth Guard - Uses localStorage first, minimal API calls
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const tokenService = inject(Token);
  const router = inject(Router);

  // First check local token
  const token = tokenService.getToken();
  if (!token) {
    console.log('AuthGuard: No token found, redirecting to login');
    router.navigate(['/login'], {
      queryParams: { returnUrl: state.url },
    });
    return false;
  }

  // If token exists and isn't expired, allow access immediately
  if (!tokenService.isTokenExpired()) {
    console.log('AuthGuard: Valid token found, access granted');
    return true;
  }

  // Only validate with server if token is expired but we have a refresh token
  if (tokenService.canRefreshToken()) {
    console.log('AuthGuard: Token expired, attempting validation/refresh');
    return authService.validateToken().pipe(
      map(() => {
        console.log('AuthGuard: Server validation successful');
        return true;
      }),
      catchError((error) => {
        console.log(
          'AuthGuard: Server validation failed, redirecting to login'
        );
        router.navigate(['/login'], {
          queryParams: { returnUrl: state.url },
        });
        return of(false);
      })
    );
  }

  // No valid token and no refresh token
  console.log('AuthGuard: No valid tokens, redirecting to login');
  router.navigate(['/login'], {
    queryParams: { returnUrl: state.url },
  });
  return false;
};

// Ultra-fast Admin Guard - Checks localStorage role first
export const fastAdminGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(Token);
  const router = inject(Router);

  // Quick authentication check
  if (!tokenService.hasValidAccessToken()) {
    console.log('FastAdminGuard: No valid token, redirecting to login');
    router.navigate(['/login'], {
      queryParams: { returnUrl: state.url },
    });
    return false;
  }

  // Quick role check from localStorage
  if (tokenService.isAdmin()) {
    console.log('FastAdminGuard: Admin role confirmed from localStorage');
    return true;
  }

  console.log('FastAdminGuard: User is not admin');
  router.navigate(['/unauthorized']);
  return false;
};

// Balanced Admin Guard - Uses localStorage first, fallback to cache/API
export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const tokenService = inject(Token);
  const router = inject(Router);

  // First check if user is authenticated locally
  const token = tokenService.getToken();
  if (!token || tokenService.isTokenExpired()) {
    console.log('AdminGuard: No valid token, redirecting to login');
    router.navigate(['/login'], {
      queryParams: { returnUrl: state.url },
    });
    return false;
  }

  // Check role from localStorage first (fastest)
  const localRole = tokenService.getRole();
  if (localRole === 'ADMIN') {
    console.log('AdminGuard: Admin role confirmed from localStorage');
    return true;
  }

  // Check from AuthService cache/memory
  if (authService.isAdmin()) {
    console.log('AdminGuard: Admin role confirmed from cache');
    return true;
  }

  // If we have conflicting info, verify with server (rare case)
  const currentUser = authService.getCurrentUserValue();
  if (currentUser) {
    if (currentUser.role === 'ADMIN') {
      console.log('AdminGuard: Admin role confirmed from current user');
      return true;
    } else {
      console.log('AdminGuard: User is not admin');
      router.navigate(['/unauthorized']);
      return false;
    }
  }

  // Last resort - server check
  return authService.getCurrentUser().pipe(
    map((user) => {
      if (user && user.role === 'ADMIN') {
        console.log('AdminGuard: Admin access granted after server check');
        return true;
      } else {
        console.log('AdminGuard: Access denied - insufficient privileges');
        router.navigate(['/unauthorized']);
        return false;
      }
    }),
    catchError((error) => {
      console.log('AdminGuard: Error checking user role:', error);
      router.navigate(['/login'], {
        queryParams: { returnUrl: state.url },
      });
      return of(false);
    })
  );
};

// Fast Role Guard Factory - Uses localStorage role data
export const fastRoleGuard = (requiredRole: string): CanActivateFn => {
  return (route, state) => {
    const tokenService = inject(Token);
    const router = inject(Router);

    // Quick authentication check
    if (!tokenService.hasValidAccessToken()) {
      console.log(`FastRoleGuard: No valid token, redirecting to login`);
      router.navigate(['/login'], {
        queryParams: { returnUrl: state.url },
      });
      return false;
    }

    // Quick role check from localStorage
    if (tokenService.hasRole(requiredRole)) {
      console.log(
        `FastRoleGuard: Required role ${requiredRole} confirmed from localStorage`
      );
      return true;
    }

    console.log(
      `FastRoleGuard: User does not have required role ${requiredRole}`
    );
    router.navigate(['/unauthorized']);
    return false;
  };
};

// Balanced Role Guard Factory
export const roleGuard = (requiredRole: string): CanActivateFn => {
  return (route, state) => {
    const authService = inject(AuthService);
    const tokenService = inject(Token);
    const router = inject(Router);

    // First check if user is authenticated locally
    const token = tokenService.getToken();
    if (!token || tokenService.isTokenExpired()) {
      console.log(`RoleGuard: No valid token, redirecting to login`);
      router.navigate(['/login'], {
        queryParams: { returnUrl: state.url },
      });
      return false;
    }

    // Check role from localStorage first (fastest)
    const localRole = tokenService.getRole();
    if (localRole === requiredRole) {
      console.log(
        `RoleGuard: Required role ${requiredRole} confirmed from localStorage`
      );
      return true;
    }

    // Check from AuthService cache/memory
    if (authService.hasRole(requiredRole)) {
      console.log(
        `RoleGuard: Required role ${requiredRole} confirmed from cache`
      );
      return true;
    }

    // Check from current user in memory
    const currentUser = authService.getCurrentUserValue();
    if (currentUser) {
      if (currentUser.role === requiredRole) {
        console.log(
          `RoleGuard: Required role ${requiredRole} confirmed from current user`
        );
        return true;
      } else {
        console.log(
          `RoleGuard: User does not have required role ${requiredRole}`
        );
        router.navigate(['/unauthorized']);
        return false;
      }
    }

    // Only fallback to server if we have no local user info (rare)
    return authService.getCurrentUser().pipe(
      map((user) => {
        if (user && user.role === requiredRole) {
          console.log(`RoleGuard: Access granted for role: ${requiredRole}`);
          return true;
        } else {
          console.log(
            `RoleGuard: Access denied - required role: ${requiredRole}, user role: ${user?.role}`
          );
          router.navigate(['/unauthorized']);
          return false;
        }
      }),
      catchError((error) => {
        console.log(`RoleGuard: Error checking user role:`, error);
        router.navigate(['/login'], {
          queryParams: { returnUrl: state.url },
        });
        return of(false);
      })
    );
  };
};

// Guest Guard - Fast localStorage check
export const guestGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(Token);
  const router = inject(Router);

  if (tokenService.hasValidAccessToken()) {
    console.log(
      'GuestGuard: User already authenticated, redirecting to dashboard'
    );
    router.navigate(['/dashboard']);
    return false;
  }

  return true;
};

// Fast Profile Completed Guard - Uses localStorage data
export const fastProfileCompletedGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(Token);
  const router = inject(Router);

  // Check authentication first
  if (!tokenService.hasValidAccessToken()) {
    console.log(
      'FastProfileCompletedGuard: No valid token, redirecting to login'
    );
    router.navigate(['/login']);
    return false;
  }

  // Check from localStorage first (fastest)
  if (tokenService.isProfileCompleted()) {
    console.log(
      'FastProfileCompletedGuard: Profile completed confirmed from localStorage'
    );
    return true;
  }

  console.log('FastProfileCompletedGuard: Profile not completed');
  router.navigate(['/profile-setup']);
  return false;
};

// Balanced Profile Completed Guard
export const profileCompletedGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const tokenService = inject(Token);
  const router = inject(Router);

  // Check authentication first
  if (!tokenService.hasValidAccessToken()) {
    console.log('ProfileCompletedGuard: No valid token, redirecting to login');
    router.navigate(['/login']);
    return false;
  }

  // Check from localStorage first (fastest)
  if (tokenService.isProfileCompleted()) {
    console.log(
      'ProfileCompletedGuard: Profile completed confirmed from localStorage'
    );
    return true;
  }

  // Check from current user in memory
  const currentUser = authService.getCurrentUserValue();
  if (currentUser) {
    if (currentUser.profileCompleted) {
      console.log(
        'ProfileCompletedGuard: Profile completed confirmed from current user'
      );
      return true;
    } else {
      console.log('ProfileCompletedGuard: Profile not completed');
      router.navigate(['/profile-setup']);
      return false;
    }
  }

  // Only call server if we have no local info (rare)
  return authService.getCurrentUser().pipe(
    map((user) => {
      if (user && user.profileCompleted) {
        console.log('ProfileCompletedGuard: Profile completed, access granted');
        return true;
      } else {
        console.log(
          'ProfileCompletedGuard: Profile not completed, redirecting to profile setup'
        );
        router.navigate(['/profile-setup']);
        return false;
      }
    }),
    catchError((error) => {
      console.log(
        'ProfileCompletedGuard: Error checking profile status:',
        error
      );
      router.navigate(['/login']);
      return of(false);
    })
  );
};

// Usage recommendations:
// - Use 'fast' guards for most routes that need quick performance
// - Use 'balanced' guards for critical routes that need server verification
// - Example routing:
/*
const routes: Routes = [
  // Fast guards for regular user routes
  { path: 'dashboard', component: DashboardComponent, canActivate: [fastAuthGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [fastAuthGuard] },
  { path: 'workouts', component: WorkoutsComponent, canActivate: [fastAuthGuard, fastProfileCompletedGuard] },
  
  // Balanced guards for sensitive routes
  { path: 'admin', component: AdminComponent, canActivate: [authGuard, adminGuard] },
  { path: 'settings', component: SettingsComponent, canActivate: [authGuard, roleGuard('ADMIN')] },
  
  // Ultra-fast guards for frequently accessed routes
  { path: 'quick-access', component: QuickComponent, canActivate: [fastRoleGuard('USER')] }
];
*/
