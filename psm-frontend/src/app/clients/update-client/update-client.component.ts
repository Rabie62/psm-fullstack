import { Component, OnInit, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/models';

@Component({
  selector: 'app-update-client',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './update-client.component.html'
})
export class UpdateClientComponent implements OnInit {
  private readonly clientService = inject(ClientService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  id!: number;
  client: Client = { nom: '', tel: '', email: '', adresse: '', activite: '' };
  selectedLogoFile: File | null = null;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedLogoFile = input.files[0];
    }
  }

  getLogoUrl(): string {
    return this.clientService.getLogoUrl(this.id);
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.id = Number(params.get('id'));
      this.clientService.getClient(this.id).subscribe(res => {
        this.client = { ...res };
        this.cdr.detectChanges();
      });
    });
  }

  updateClient(): void {
    this.clientService.updateClient(this.id, this.client).subscribe(() => {
      if (this.selectedLogoFile) {
        this.clientService.uploadLogo(this.id, this.selectedLogoFile).subscribe({
          next: () => this.router.navigate(['/listClient']),
          error: (err) => {
            console.error('Erreur upload logo', err);
            this.router.navigate(['/listClient']);
          }
        });
      } else {
        this.router.navigate(['/listClient']);
      }
    });
  }
}
