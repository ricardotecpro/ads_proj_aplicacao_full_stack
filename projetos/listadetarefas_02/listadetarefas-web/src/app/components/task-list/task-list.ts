// src/app/components/task-list/task-list.component.ts
import { Component, OnInit, OnDestroy } from '@angular/core';
import { TarefaService } from '../../services/tarefa';
import { Tarefa } from '../../models/tarefa';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [ CommonModule, FormsModule ],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css'
})
export class TaskListComponent implements OnInit, OnDestroy {
  tarefas: Tarefa[] = [];
  novaTarefa: Tarefa = { descricao: '', concluida: false };

  // Subject to manage subscriptions and prevent memory leaks.
  private readonly destroy$ = new Subject<void>();
  // Map to store original descriptions during editing, preventing bugs with multiple simultaneous edits.
  private originalDescriptions = new Map<number, string>();

  constructor(private tarefaService: TarefaService) { }

  ngOnInit(): void {
    this.carregarTarefas();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private carregarTarefas(): void {
    this.tarefaService.getTarefas()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => { this.tarefas = data; },
        error: (err) => console.error('Erro ao carregar tarefas:', err)
      });
  }

  adicionarTarefa(): void {
    if (!this.novaTarefa.descricao.trim()) {
      return; // Do not add empty tasks.
    }

    this.tarefaService.addTarefa(this.novaTarefa)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (tarefaAdicionada) => {
          this.tarefas.push(tarefaAdicionada);
          this.novaTarefa = { descricao: '', concluida: false }; // Reset form.
        },
        error: (err) => console.error('Erro ao adicionar tarefa:', err)
      });
  }

  deletarTarefa(id: number | undefined): void {
    if (id === undefined) {
      console.error('Tentativa de deletar tarefa sem ID.');
      return;
    }

    this.tarefaService.deleteTarefa(id)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.tarefas = this.tarefas.filter(t => t.id !== id);
        },
        error: (err) => console.error(`Erro ao deletar tarefa com id ${id}:`, err)
      });
  }

  toggleConcluida(tarefa: Tarefa): void {
    // The 'concluida' property is already updated by ngModel.
    // We just persist this change to the backend.
    this.tarefaService.updateTarefa(tarefa)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        // On success, no UI change is needed.
        error: (err) => {
          console.error('Erro ao atualizar status da tarefa:', err);
          // Revert the checkbox state on failure to keep UI consistent with backend.
          tarefa.concluida = !tarefa.concluida;
        }
      });
  }

  // --- UI Control Methods for In-place Editing ---

  iniciarEdicao(tarefa: Tarefa): void {
    if (tarefa.id === undefined) return;
    // Store the original description before editing begins.
    this.originalDescriptions.set(tarefa.id, tarefa.descricao);
    tarefa.editando = true;
  }

  salvarEdicao(tarefa: Tarefa): void {
    if (tarefa.id === undefined) return;

    const descricaoOriginal = this.originalDescriptions.get(tarefa.id);
    if (tarefa.descricao.trim() === '' || tarefa.descricao.trim() === descricaoOriginal) {
      // If the new description is empty or unchanged, just cancel the edit.
      this.cancelarEdicao(tarefa);
      return;
    }

    tarefa.editando = false;
    this.tarefaService.updateTarefa(tarefa)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          // On success, clean up the state.
          this.originalDescriptions.delete(tarefa.id!);
        },
        error: (err) => {
          console.error('Erro ao salvar edição da tarefa:', err);
          // On error, revert the description in the UI.
          if (descricaoOriginal !== undefined) {
            tarefa.descricao = descricaoOriginal;
          }
        }
      });
  }

  cancelarEdicao(tarefa: Tarefa): void {
    if (tarefa.id === undefined) return;

    const descricaoOriginal = this.originalDescriptions.get(tarefa.id);
    if (descricaoOriginal !== undefined) {
      tarefa.descricao = descricaoOriginal;
    }
    tarefa.editando = false;
    // Clean up the state.
    this.originalDescriptions.delete(tarefa.id);
  }
}
