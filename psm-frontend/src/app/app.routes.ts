import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { LogoutComponent } from './logout/logout.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { KanbanComponent } from './kanban/kanban.component';
import { ListClientComponent } from './clients/list-client/list-client.component';
import { AddClientComponent } from './clients/add-client/add-client.component';
import { UpdateClientComponent } from './clients/update-client/update-client.component';
import { ListProjetComponent } from './projets/list-projet/list-projet.component';
import { AddProjetComponent } from './projets/add-projet/add-projet.component';
import { UpdateProjetComponent } from './projets/update-projet/update-projet.component';
import { ListUserComponent } from './users/list-user/list-user.component';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'logout', component: LogoutComponent, canActivate: [authGuard] },

  // Nouvelles routes issues des recommandations : Dashboard KPI et Kanban
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] },
  { path: 'kanban', component: KanbanComponent, canActivate: [authGuard] },

  // Routes Clients
  { path: 'listClient', component: ListClientComponent, canActivate: [authGuard] },
  { path: 'addClient', component: AddClientComponent, canActivate: [authGuard] },
  {
    path: 'updateClient/:id',
    component: UpdateClientComponent,
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] }
  },

  // Routes Projets
  { path: 'listProjet', component: ListProjetComponent, canActivate: [authGuard] },
  { path: 'addProjet', component: AddProjetComponent, canActivate: [authGuard] },
  {
    path: 'updateProjet/:id',
    component: UpdateProjetComponent,
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] }
  },

  // Route Users (ADMIN uniquement)
  {
    path: 'listUser',
    component: ListUserComponent,
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] }
  },

  { path: '**', redirectTo: 'dashboard' }
];
