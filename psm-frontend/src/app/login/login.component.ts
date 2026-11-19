import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {
  private readonly router = inject(Router);
  private readonly authService = inject(AuthenticationService);

  email = '';
  password = '';
  invalidLogin = signal(false);
  errorMessage = signal('Email ou mot de passe invalide.');

  checkLogin(): void {
    this.authService.authenticate(this.email, this.password).subscribe({
      next: () => {
        this.invalidLogin.set(false);
        this.router.navigate(['/listClient']);
      },
      error: (err) => {
        this.invalidLogin.set(true);
        this.errorMessage.set(err?.error?.message || 'Email ou mot de passe invalide.');
      }
    });
  }
}
