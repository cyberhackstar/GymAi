import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { CanActivateFn } from '@angular/router';
import { map, catchError, of } from 'rxjs';
import { AuthService } from '../services/auth';

// Auth Guard - Protects routes that require authentication
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Check if user is authenticated
  if (!authService.isAuthenticated()) {
    console.log('AuthGuard: No token found, redirecting to login');
    router.navigate(['/login'], {
      queryParams: { returnUrl: state.url },
    });
    return false;
  }

  // Validate the token with the server
  return authService.validateToken().pipe(
    map((response) => {
      console.log('AuthGuard: Token validation successful');
      return true;
    }),
    catchError((error) => {
      console.log('AuthGuard: Token validation failed, redirecting to login');
      router.navigate(['/login'], {
        queryParams: { returnUrl: state.url },
      });
      return of(false);
    })
  );
};

// Admin Guard - Protects routes that require ADMIN role
export const adminGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // First check if user is authenticated
  if (!authService.isAuthenticated()) {
    console.log('AdminGuard: No token found, redirecting to login');
    router.navigate(['/login'], {
      queryParams: { returnUrl: state.url },
    });
    return false;
  }

  // Check if user has admin role
  return authService.getCurrentUser().pipe(
    map((user) => {
      if (user && user.role === 'ADMIN') {
        console.log('AdminGuard: Admin access granted');
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

// Role Guard Factory - Creates a guard for specific roles
export const roleGuard = (requiredRole: string): CanActivateFn => {
  return (route, state) => {
    const authService = inject(AuthService);
    const router = inject(Router);

    // First check if user is authenticated
    if (!authService.isAuthenticated()) {
      console.log(`RoleGuard: No token found, redirecting to login`);
      router.navigate(['/login'], {
        queryParams: { returnUrl: state.url },
      });
      return false;
    }

    // Check if user has the required role
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

// Guest Guard - Redirects authenticated users away from login/register pages
export const guestGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    console.log(
      'GuestGuard: User already authenticated, redirecting to dashboard'
    );
    router.navigate(['/dashboard']); // or wherever authenticated users should go
    return false;
  }

  return true;
};

// Profile Completed Guard - Ensures user has completed their profile
export const profileCompletedGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (!authService.isAuthenticated()) {
    console.log('ProfileCompletedGuard: No token found, redirecting to login');
    router.navigate(['/login']);
    return false;
  }

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

// Example usage in routing:
/*
const routes: Routes = [
  { 
    path: 'login', 
    component: LoginComponent,
    canActivate: [guestGuard] 
  },
  { 
    path: 'register', 
    component: RegisterComponent,
    canActivate: [guestGuard] 
  },
  { 
    path: 'dashboard', 
    component: DashboardComponent,
    canActivate: [authGuard] 
  },
  { 
    path: 'admin', 
    component: AdminComponent,
    canActivate: [adminGuard] 
  },
  { 
    path: 'user-management', 
    component: UserManagementComponent,
    canActivate: [roleGuard('ADMIN')] 
  },
  { 
    path: 'profile', 
    component: ProfileComponent,
    canActivate: [authGuard, profileCompletedGuard] 
  },
  { 
    path: 'unauthorized', 
    component: UnauthorizedComponent 
  }
];
*/
