import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService } from '../../services/user.service';
import { User } from '../../models/models';

@Component({
  selector: 'app-list-user',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './list-user.component.html'
})
export class ListUserComponent implements OnInit {
  private readonly userService = inject(UserService);

  users = signal<User[]>([]);
  feedbackMessage = signal('');
  errorMessage = signal('');

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userService.listUsers().subscribe({
      next: (res) => this.users.set(res),
      error: (err) => this.errorMessage.set(err?.error?.message || 'Erreur lors du chargement des utilisateurs.')
    });
  }

  changeRole(user: User, newRole: string): void {
    this.feedbackMessage.set('');
    this.errorMessage.set('');
    this.userService.updateRole(user.id!, newRole).subscribe({
      next: () => {
        this.feedbackMessage.set(`Rôle de ${user.prenom} ${user.nom} mis à jour vers ${newRole}.`);
        this.loadUsers();
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.message || 'Action non autorisée (vérifiez que votre compte possède le rôle ADMIN).');
      }
    });
  }

  toggleAccount(user: User): void {
    this.feedbackMessage.set('');
    this.errorMessage.set('');
    const req$ = user.active === 1
      ? this.userService.disableUser(user.id!)
      : this.userService.enableUser(user.id!);

    req$.subscribe({
      next: () => {
        this.feedbackMessage.set(`Statut du compte de ${user.prenom} ${user.nom} mis à jour.`);
        this.loadUsers();
      },
      error: (err) => {
        this.errorMessage.set(err?.error?.message || 'Impossible de modifier le statut de ce compte.');
      }
    });
  }
}
