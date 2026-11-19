import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Client } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private readonly http = inject(HttpClient);
  private readonly urlClients = `${environment.apiUrl}/clients`;

  listClients(): Observable<Client[]> {
    return this.http.get<Client[]>(`${this.urlClients}/list`);
  }

  getClient(id: number): Observable<Client> {
    return this.http.get<Client>(`${this.urlClients}/${id}`);
  }

  createClient(client: Client): Observable<Client> {
    return this.http.post<Client>(`${this.urlClients}/add`, client);
  }

  updateClient(id: number, client: Client): Observable<Client> {
    return this.http.put<Client>(`${this.urlClients}/${id}`, client);
  }

  deleteClient(id: number): Observable<any> {
    return this.http.delete(`${this.urlClients}/${id}`);
  }

  uploadLogo(clientId: number, file: File): Observable<Client> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Client>(`${this.urlClients}/${clientId}/logo`, formData);
  }

  getLogoUrl(clientId: number): string {
    return `${this.urlClients}/${clientId}/logo`;
  }
}
