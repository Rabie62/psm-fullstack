import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Tache, TaskStatus } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class TacheService {
  private readonly http = inject(HttpClient);
  private readonly urlTaches = `${environment.apiUrl}/taches`;

  listTaches(): Observable<Tache[]> {
    return this.http.get<Tache[]>(`${this.urlTaches}/list`);
  }

  createTache(projetId: number, tache: Tache, collaborateurId?: number): Observable<Tache> {
    const query = collaborateurId ? `?collaborateurId=${collaborateurId}` : '';
    return this.http.post<Tache>(`${this.urlTaches}/add/${projetId}${query}`, tache);
  }

  updateStatus(tacheId: number, statut: TaskStatus, progression: number): Observable<Tache> {
    return this.http.patch<Tache>(`${this.urlTaches}/${tacheId}/status`, { statut, progression });
  }

  deleteTache(tacheId: number): Observable<any> {
    return this.http.delete(`${this.urlTaches}/${tacheId}`);
  }
}
