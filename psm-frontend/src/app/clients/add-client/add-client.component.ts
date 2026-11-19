import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ClientService } from '../../services/client.service';
import { Client } from '../../models/models';

@Component({
  selector: 'app-add-client',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './add-client.component.html'
})
export class AddClientComponent {
  private readonly clientService = inject(ClientService);
  private readonly router = inject(Router);

  client: Client = {
    nom: '',
    tel: '',
    email: '',
    adresse: '',
    activite: ''
  };

  selectedLogoFile: File | null = null;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedLogoFile = input.files[0];
    }
  }

  createClient(): void {
    this.clientService.createClient(this.client).subscribe((createdClient: Client) => {
      if (this.selectedLogoFile && createdClient.id) {
        this.clientService.uploadLogo(createdClient.id, this.selectedLogoFile).subscribe({
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
