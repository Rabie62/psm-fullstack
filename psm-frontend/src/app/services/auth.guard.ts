import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthenticationService } from './authentication.service';

/**
 * Guard fonctionnel moderne (Angular 17+ CanActivateFn) avec inject() :
 * Vérifie l'authentification JWT et les rôles autorisés sur la route (RBAC).
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  if (!authService.isUserLoggedIn()) {
    authService.logOut();
    router.navigate(['/login']);
    return false;
  }

  const expectedRoles: string[] | undefined = route.data?.['roles'];
  if (expectedRoles && expectedRoles.length > 0) {
    const userRoles = authService.getRoles();
    const hasRole = expectedRoles.some(role => userRoles.includes(role));
    if (!hasRole) {
      router.navigate(['/listClient']);
      return false;
    }
  }

  return true;
};
