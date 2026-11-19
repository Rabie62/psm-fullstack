import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Intercepteur HTTP fonctionnel moderne (Angular HttpInterceptorFn) :
 * Injecte automatiquement l'en-tête Authorization: Bearer <token> dans chaque requête sortante.
 */
export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const rawToken = sessionStorage.getItem('token') || localStorage.getItem('token');

  if (rawToken) {
    const bearerToken = rawToken.startsWith('Bearer ') ? rawToken : `Bearer ${rawToken}`;
    req = req.clone({
      setHeaders: {
        Authorization: bearerToken
      }
    });
  }
  return next(req);
};
