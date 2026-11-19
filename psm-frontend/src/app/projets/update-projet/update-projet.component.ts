import { Component, OnInit, inject, signal, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ProjetService } from '../../services/projet.service';
import { ClientService } from '../../services/client.service';
import { Client, Projet } from '../../models/models';

@Component({
  selector: 'app-update-projet',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './update-projet.component.html'
})
export class UpdateProjetComponent implements OnInit {
  private readonly projetService = inject(ProjetService);
  private readonly clientService = inject(ClientService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  id!: number;
  clients = signal<Client[]>([]);
  selectedClientId!: number;
  projet: Projet = { libelle: '', budget: 0, duree: '', description: '', statut: 'EN_COURS' };
  selectedDocumentFile: File | null = null;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedDocumentFile = input.files[0];
    }
  }

  getDocumentDownloadUrl(): string {
    return this.projetService.getDocumentDownloadUrl(this.id);
  }

  ngOnInit(): void {
    this.clientService.listClients().subscribe(res => {
      this.clients.set(res);
      this.cdr.detectChanges();
    });

    this.route.paramMap.subscribe(params => {
      this.id = Number(params.get('id'));
      this.projetService.getProjet(this.id).subscribe(res => {
        this.projet = { ...res };
        if (res.client?.id) {
          this.selectedClientId = res.client.id;
        }
        this.cdr.detectChanges();
      });
    });
  }

  updateProjet(): void {
    this.projetService.updateProjet(this.selectedClientId, this.id, this.projet).subscribe(() => {
      if (this.selectedDocumentFile) {
        this.projetService.uploadDocument(this.id, this.selectedDocumentFile).subscribe({
          next: () => this.router.navigate(['/listProjet']),
          error: (err) => {
            console.error('Erreur upload document', err);
            this.router.navigate(['/listProjet']);
          }
        });
      } else {
        this.router.navigate(['/listProjet']);
      }
    });
  }
}
