import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthenticationService } from './authentication.service';

/**
 * Intercepteur HTTP fonctionnel moderne (Angular HttpInterceptorFn) :
 * 1. Injecte automatiquement l'en-tête Authorization: Bearer <token> dans chaque requête sortante.
 * 2. Intercepte les erreurs 401 (jeton expiré/invalide) pour réinitialiser la session et rediriger vers /login.
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  const rawToken = sessionStorage.getItem('token') || localStorage.getItem('token');

  if (rawToken) {
    const bearerToken = rawToken.startsWith('Bearer ') ? rawToken : `Bearer ${rawToken}`;
    req = req.clone({
      setHeaders: {
        Authorization: bearerToken
      }
    });
  }

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // Si la session est expirée ou non autorisée (sauf échec explicite de saisie sur /login)
      if (error.status === 401 && !req.url.includes('/api/auth/login')) {
        authService.logOut();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
