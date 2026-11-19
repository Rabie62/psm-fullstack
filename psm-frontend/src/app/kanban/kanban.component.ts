import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TacheService } from '../services/tache.service';
import { ProjetService } from '../services/projet.service';
import { AuthenticationService } from '../services/authentication.service';
import { Projet, Tache, TaskStatus } from '../models/models';

@Component({
  selector: 'app-kanban',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './kanban.component.html'
})
export class KanbanComponent implements OnInit {
  private readonly tacheService = inject(TacheService);
  private readonly projetService = inject(ProjetService);
  readonly authService = inject(AuthenticationService);

  taches = signal<Tache[]>([]);
  projets = signal<Projet[]>([]);

  todoTasks = computed(() => this.taches().filter(t => t.statut === 'TODO'));
  inProgressTasks = computed(() => this.taches().filter(t => t.statut === 'IN_PROGRESS'));
  doneTasks = computed(() => this.taches().filter(t => t.statut === 'DONE'));

  selectedProjetId!: number;
  newTask: Tache = {
    titre: '',
    description: '',
    statut: 'TODO',
    priorite: 'MEDIUM',
    progression: 0
  };

  ngOnInit(): void {
    this.loadKanban();
    this.projetService.listProjets().subscribe(res => {
      this.projets.set(res);
      if (res.length > 0) {
        this.selectedProjetId = res[0].id!;
      }
    });
  }

  loadKanban(): void {
    this.tacheService.listTaches().subscribe(res => this.taches.set(res));
  }

  addTask(): void {
    if (!this.selectedProjetId || !this.newTask.titre) return;
    this.tacheService.createTache(this.selectedProjetId, this.newTask).subscribe(() => {
      this.newTask = { titre: '', description: '', statut: 'TODO', priorite: 'MEDIUM', progression: 0 };
      this.loadKanban();
    });
  }

  moveTask(tache: Tache, nextStatus: TaskStatus): void {
    const nextProgress = nextStatus === 'DONE' ? 100 : (nextStatus === 'IN_PROGRESS' ? 50 : 0);
    this.tacheService.updateStatus(tache.id!, nextStatus, nextProgress).subscribe(() => {
      this.loadKanban();
    });
  }

  removeTask(tache: Tache): void {
    this.tacheService.deleteTache(tache.id!).subscribe(() => this.loadKanban());
  }
}
