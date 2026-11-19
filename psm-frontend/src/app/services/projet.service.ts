import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Projet } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ProjetService {
  private readonly http = inject(HttpClient);
  private readonly urlProjets = `${environment.apiUrl}/projets`;

  listProjets(): Observable<Projet[]> {
    return this.http.get<Projet[]>(`${this.urlProjets}/list`);
  }

  searchPaginated(keyword: string, page: number, size: number): Observable<any> {
    return this.http.get<any>(`${this.urlProjets}/page?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`);
  }

  exportCsv(): Observable<Blob> {
    return this.http.get(`${this.urlProjets}/export/csv`, { responseType: 'blob' });
  }

  getProjet(id: number): Observable<Projet> {
    return this.http.get<Projet>(`${this.urlProjets}/${id}`);
  }

  createProjet(clientId: number, projet: Projet): Observable<Projet> {
    return this.http.post<Projet>(`${this.urlProjets}/add/${clientId}`, projet);
  }

  updateProjet(clientId: number, projetId: number, projet: Projet): Observable<Projet> {
    return this.http.put<Projet>(`${this.urlProjets}/update/${clientId}/${projetId}`, projet);
  }

  deleteProjet(id: number): Observable<any> {
    return this.http.delete(`${this.urlProjets}/delete/${id}`);
  }

  uploadDocument(projetId: number, file: File): Observable<Projet> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Projet>(`${this.urlProjets}/${projetId}/document`, formData);
  }

  getDocumentDownloadUrl(projetId: number): string {
    return `${this.urlProjets}/${projetId}/document`;
  }
}
