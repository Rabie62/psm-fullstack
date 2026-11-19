import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ProjetService } from '../../services/projet.service';
import { AuthenticationService } from '../../services/authentication.service';
import { Projet } from '../../models/models';

@Component({
  selector: 'app-list-projet',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './list-projet.component.html'
})
export class ListProjetComponent implements OnInit {
  private readonly projetService = inject(ProjetService);
  readonly authService = inject(AuthenticationService);
  private readonly router = inject(Router);

  projets = signal<Projet[]>([]);
  searchKeyword = signal('');

  filteredProjets = computed(() => {
    const kw = this.searchKeyword().toLowerCase().trim();
    if (!kw) return this.projets();
    return this.projets().filter(p =>
      p.libelle.toLowerCase().includes(kw) ||
      (p.client?.nom || '').toLowerCase().includes(kw)
    );
  });

  ngOnInit(): void {
    this.refreshListProjets();
  }

  refreshListProjets(): void {
    this.projetService.listProjets().subscribe(res => {
      this.projets.set(res);
    });
  }

  exportToCsv(): void {
    this.projetService.exportCsv().subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'projets_psm.csv';
      a.click();
      window.URL.revokeObjectURL(url);
    });
  }

  updateProjet(projet: Projet): void {
    this.router.navigate(['/updateProjet', projet.id]);
  }

  getDocumentDownloadUrl(projetId: number): string {
    return this.projetService.getDocumentDownloadUrl(projetId);
  }

  deleteProjet(projet: Projet): void {
    if (confirm(`Confirmez-vous la suppression du projet "${projet.libelle}" ?`)) {
      this.projetService.deleteProjet(projet.id!).subscribe(() => {
        this.refreshListProjets();
      });
    }
  }
}
