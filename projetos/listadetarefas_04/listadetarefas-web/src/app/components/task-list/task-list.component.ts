// src/app/components/task-list/task-list.component.ts
import { Component, OnInit } from '@angular/core';
import { TarefaService } from '../../services/tarefa.service';
import { Tarefa } from '../../models/tarefa';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [ CommonModule, FormsModule ], // Módulos para diretivas (*ngFor) e formulários (ngModel).
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css'
})
export class TaskListComponent implements OnInit {
  tarefas: Tarefa[] = [];
  novaTarefa: Tarefa = { descricao: '', concluida: false };
  private descricaoOriginal: string = '';

  constructor(private tarefaService: TarefaService) { }

  ngOnInit(): void { this.carregarTarefas(); }

  carregarTarefas(): void {
    this.tarefaService.getTarefas().subscribe(data => { this.tarefas = data; });
  }

  adicionarTarefa(): void {
    if (this.novaTarefa.descricao.trim() === '') return;
    this.tarefaService.addTarefa(this.novaTarefa).subscribe(data => {
      this.tarefas.push(data);
      this.novaTarefa = { descricao: '', concluida: false }; // Limpa o campo.
    });
  }

  atualizarTarefa(tarefa: Tarefa): void {
    this.tarefaService.updateTarefa(tarefa).subscribe();
  }

  deletarTarefa(id: number | undefined): void {
    if (id === undefined) return;
    this.tarefaService.deleteTarefa(id).subscribe(() => {
      this.tarefas = this.tarefas.filter(t => t.id !== id);
    });
  }

  // Métodos para controle da UI de edição
  iniciarEdicao(tarefa: Tarefa): void {
    this.descricaoOriginal = tarefa.descricao;
    tarefa.editando = true;
  }

  salvarEdicao(tarefa: Tarefa): void {
    tarefa.editando = false;
    if (tarefa.descricao.trim() === '') {
      tarefa.descricao = this.descricaoOriginal; // Restaura se o campo ficar vazio.
    } else {
      this.atualizarTarefa(tarefa);
    }
  }

  cancelarEdicao(tarefa: Tarefa): void {
    tarefa.descricao = this.descricaoOriginal;
    tarefa.editando = false;
  }
}
