export interface Role {
  id?: number;
  libelle: 'ADMIN' | 'COLLABORATEUR';
}

export interface User {
  id?: number;
  nom: string;
  prenom: string;
  email: string;
  tel: string;
  password?: string;
  active?: number;
  roles?: Role[];
}

export interface Client {
  id?: number;
  nom: string;
  tel: string;
  email: string;
  adresse: string;
  photo?: string;
  activite?: string;
  createdAt?: string;
  createdBy?: string;
}

export type ProjectStatus = 'EN_ATTENTE' | 'EN_COURS' | 'TERMINE' | 'EN_RETARD';

export interface Projet {
  id?: number;
  libelle: string;
  budget: number;
  duree: string;
  description: string;
  statut?: ProjectStatus;
  documentCahierCharges?: string;
  client?: Client;
  createdAt?: string;
  createdBy?: string;
}

export type TaskStatus = 'TODO' | 'IN_PROGRESS' | 'DONE';
export type TaskPriority = 'LOW' | 'MEDIUM' | 'HIGH';

export interface Tache {
  id?: number;
  titre: string;
  description: string;
  statut: TaskStatus;
  priorite: TaskPriority;
  progression: number;
  deadline?: string;
  projet?: Projet;
  collaborateur?: User;
}

export interface ClientBudget {
  clientNom: string;
  nombreProjets: number;
  budgetTotal: number;
}

export interface DashboardStats {
  totalClients: number;
  totalProjets: number;
  totalUsers: number;
  totalTaches: number;
  budgetTotalEngage: number;
  projetsParStatut: Record<string, number>;
  tachesParStatut: Record<string, number>;
  topClientsParBudget: ClientBudget[];
}
