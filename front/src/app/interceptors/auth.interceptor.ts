import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const AuthInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const isAuthRequest = req.url.includes('/auth/') || req.url.includes('/register');
  const isUserRequest = req.url.includes('/users/username/');

  let authReq = req;
  const token = authService.getToken();

  if (token && !isAuthRequest) {
    authReq = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      // No cerrar sesión para requests de usuario que puedan fallar
      if (error.status === 401 && !isAuthRequest && !isUserRequest) {
        console.error('Token inválido o expirado');
        authService.logout();
        router.navigate(['/login']);
      }

      return throwError(() => error);
    })
  );
};
