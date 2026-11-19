import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { ClientService } from '../../services/client.service';
import { AuthenticationService } from '../../services/authentication.service';
import { Client } from '../../models/models';

@Component({
  selector: 'app-list-client',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './list-client.component.html'
})
export class ListClientComponent implements OnInit {
  private readonly clientService = inject(ClientService);
  readonly authService = inject(AuthenticationService);
  private readonly router = inject(Router);

  clients = signal<Client[]>([]);

  ngOnInit(): void {
    this.refreshListClients();
  }

  refreshListClients(): void {
    this.clientService.listClients().subscribe(res => {
      this.clients.set(res);
    });
  }

  updateClient(client: Client): void {
    this.router.navigate(['/updateClient', client.id]);
  }

  getLogoUrl(clientId: number): string {
    return this.clientService.getLogoUrl(clientId);
  }

  deleteClient(client: Client): void {
    if (confirm(`Voulez-vous vraiment supprimer le client ${client.nom} et ses projets associés ?`)) {
      this.clientService.deleteClient(client.id!).subscribe(() => {
        this.refreshListClients();
      });
    }
  }
}
