import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { User } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly http = inject(HttpClient);
  private readonly urlUsers = `${environment.apiUrl}/users`;

  listUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.urlUsers}/list`);
  }

  updateRole(id: number, role: string): Observable<User> {
    return this.http.put<User>(`${this.urlUsers}/${id}/role`, { role });
  }

  enableUser(id: number): Observable<User> {
    return this.http.put<User>(`${this.urlUsers}/${id}/enable`, {});
  }

  disableUser(id: number): Observable<User> {
    return this.http.put<User>(`${this.urlUsers}/${id}/disable`, {});
  }

  deleteUser(id: number): Observable<any> {
    return this.http.delete(`${this.urlUsers}/${id}`);
  }
}
