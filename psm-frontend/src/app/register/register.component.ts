import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  private readonly authService = inject(AuthenticationService);
  private readonly router = inject(Router);

  user = {
    nom: '',
    prenom: '',
    email: '',
    tel: '',
    password: '',
    role: 'COLLABORATEUR'
  };
  successMessage = signal('');
  errorMessage = signal('');

  registerUser(): void {
    const payload = {
      nom: this.user.nom,
      prenom: this.user.prenom,
      email: this.user.email,
      tel: this.user.tel,
      password: this.user.password,
      roles: [this.user.role]
    };

    this.authService.register(payload).subscribe({
      next: (res) => {
        this.errorMessage.set('');
        this.successMessage.set(res.message || 'Inscription réussie ! Notification envoyée à l\'administrateur.');
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.successMessage.set('');
        this.errorMessage.set(err?.error?.message || 'Erreur lors de l\'inscription.');
      }
    });
  }
}
