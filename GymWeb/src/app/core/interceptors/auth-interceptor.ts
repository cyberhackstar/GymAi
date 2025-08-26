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

  const token = tokenService.getToken(); // automatically null if expired

  // 🚫 Don't attach token for login or register endpoints
  const isAuthRequest =
    req.url.includes('/auth/login') || req.url.includes('/auth/register');

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
      // If token is missing or request fails with 401, redirect to login
      if (error.status === 401 || (!token && !isAuthRequest)) {
        tokenService.clearToken(); // clear any invalid/expired tokens
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
