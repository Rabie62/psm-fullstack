import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ProjetService } from '../../services/projet.service';
import { ClientService } from '../../services/client.service';
import { Client, Projet } from '../../models/models';

@Component({
  selector: 'app-add-projet',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './add-projet.component.html'
})
export class AddProjetComponent implements OnInit {
  private readonly projetService = inject(ProjetService);
  private readonly clientService = inject(ClientService);
  private readonly router = inject(Router);

  clients = signal<Client[]>([]);
  selectedClientId: number | null = null;
  projet: Projet = {
    libelle: '',
    budget: 0,
    duree: '',
    description: ''
  };
  selectedDocumentFile: File | null = null;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedDocumentFile = input.files[0];
    }
  }

  ngOnInit(): void {
    this.selectedClientId = null;
    this.clientService.listClients().subscribe(res => {
      this.clients.set(res);
    });
  }

  createProjet(): void {
    if (!this.selectedClientId) {
      return;
    }
    this.projetService.createProjet(this.selectedClientId, this.projet).subscribe((createdProjet: Projet) => {
      if (this.selectedDocumentFile && createdProjet.id) {
        this.projetService.uploadDocument(createdProjet.id, this.selectedDocumentFile).subscribe({
          next: () => {
            this.selectedClientId = null;
            this.projet = { libelle: '', budget: 0, duree: '', description: '' };
            this.router.navigate(['/listProjet']);
          },
          error: (err) => {
            console.error('Erreur upload document', err);
            this.selectedClientId = null;
            this.projet = { libelle: '', budget: 0, duree: '', description: '' };
            this.router.navigate(['/listProjet']);
          }
        });
      } else {
        this.selectedClientId = null;
        this.projet = { libelle: '', budget: 0, duree: '', description: '' };
        this.router.navigate(['/listProjet']);
      }
    });
  }
}
