import {
  HttpRequest,
  HttpHandlerFn,
  HttpInterceptorFn,
  HttpErrorResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Token } from '../services/token';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const tokenService = inject(Token);
  const router = inject(Router);

  // Get token (will be null if expired or doesn't exist)
  let token: string | null = null;
  try {
    token = tokenService.getToken();
  } catch (error) {
    console.error('Error getting token:', error);
    token = null;
  }

  // Don't attach token for auth endpoints
  const isAuthRequest =
    req.url.includes('/auth/login') ||
    req.url.includes('/auth/register') ||
    req.url.includes('/oauth2/') ||
    req.url.includes('/login/oauth2/') ||
    req.url.includes('/cors/test');

  let authReq = req;
  if (token && !isAuthRequest) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`,
      },
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // Handle 401 Unauthorized responses
      if (error.status === 401) {
        console.log(
          '401 Unauthorized - clearing token and redirecting to login'
        );
        try {
          tokenService.clearToken();
        } catch (clearError) {
          console.error('Error clearing token:', clearError);
        }

        // Only redirect if not already on login page
        if (
          !window.location.pathname.includes('/login') &&
          !window.location.pathname.includes('/oauth-callback')
        ) {
          router.navigate(['/login']);
        }
      }

      return throwError(() => error);
    })
  );
};
