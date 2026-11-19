import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private readonly httpClient = inject(HttpClient);
  private readonly authUrl = `${environment.apiUrl}/api/auth`;

  readonly currentUserRoles = signal<string[]>(this.loadRolesFromStorage());
  readonly isAdminSignal = computed(() => this.currentUserRoles().includes('ADMIN'));

  private loadRolesFromStorage(): string[] {
    const rolesStr = sessionStorage.getItem('roles') || localStorage.getItem('roles');
    return rolesStr ? JSON.parse(rolesStr) : [];
  }

  authenticate(email: string, password: string): Observable<any> {
    return this.httpClient.post<any>(`${this.authUrl}/login`, { email, password }).pipe(
      map(userData => {
        const roles: string[] = userData.roles || [];
        const bearerToken = `Bearer ${userData.token}`;

        sessionStorage.setItem('email', userData.email);
        sessionStorage.setItem('nom', userData.nom);
        sessionStorage.setItem('prenom', userData.prenom);
        sessionStorage.setItem('roles', JSON.stringify(roles));
        sessionStorage.setItem('token', bearerToken);

        localStorage.setItem('email', userData.email);
        localStorage.setItem('nom', userData.nom);
        localStorage.setItem('prenom', userData.prenom);
        localStorage.setItem('roles', JSON.stringify(roles));
        localStorage.setItem('token', bearerToken);

        this.currentUserRoles.set(roles);
        return userData;
      })
    );
  }

  register(userPayload: any): Observable<any> {
    return this.httpClient.post<any>(`${this.authUrl}/register`, userPayload);
  }

  isUserLoggedIn(): boolean {
    const token = sessionStorage.getItem('token') || localStorage.getItem('token');
    if (!token) {
      return false;
    }

    try {
      const cleanToken = token.replace('Bearer ', '').trim();
      const parts = cleanToken.split('.');
      if (parts.length === 3) {
        const payload = JSON.parse(atob(parts[1]));
        if (payload.exp && (payload.exp * 1000) < Date.now()) {
          this.logOut();
          return false;
        }
      }
    } catch {
      this.logOut();
      return false;
    }

    return true;
  }

  getRoles(): string[] {
    return this.currentUserRoles();
  }

  isAdmin(): boolean {
    return this.isAdminSignal();
  }

  isCollaborateur(): boolean {
    return this.currentUserRoles().includes('COLLABORATEUR');
  }

  getFullName(): string {
    const prenom = sessionStorage.getItem('prenom') || localStorage.getItem('prenom') || '';
    const nom = sessionStorage.getItem('nom') || localStorage.getItem('nom') || '';
    return `${prenom} ${nom}`.trim() || sessionStorage.getItem('email') || localStorage.getItem('email') || 'Utilisateur';
  }

  logOut(): void {
    sessionStorage.clear();
    localStorage.clear();
    this.currentUserRoles.set([]);
  }
}
