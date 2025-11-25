import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, of } from 'rxjs';
import { Tarefa } from '../../models/tarefa.model';
import { TarefaService } from '../../services/tarefa.service';
import { MatSnackBar } from '@angular/material/snack-bar';

// Imports de UI e Módulos Standalone
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-tarefa-list',
  standalone: true,
  imports: [
    CommonModule, MatCardModule, MatButtonModule, MatIconModule,
    MatCheckboxModule, MatProgressSpinnerModule, MatSnackBarModule
  ],
  templateUrl: './tarefa-list.component.html',
  styleUrls: ['./tarefa-list.component.scss']
})
export class TarefaListComponent implements OnInit {

  tarefas$!: Observable<Tarefa[]>;
  errorLoading = false;

  constructor(
    private tarefaService: TarefaService,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.carregarTarefas();
  }

  carregarTarefas(): void {
    this.errorLoading = false;
    this.tarefas$ = this.tarefaService.listarTodas().pipe(
      catchError(error => {
        console.error('Erro ao carregar tarefas:', error);
        this.snackBar.open('Falha ao carregar tarefas. Verifique a conexão com a API.', 'Fechar', { duration: 5000 });
        this.errorLoading = true;
        return of([]); // Retorna um array vazio para não quebrar o template
      })
    );
  }

  toggleConcluida(tarefa: Tarefa): void {
    tarefa.concluida = !tarefa.concluida;
    this.tarefaService.atualizar(tarefa.id, tarefa).subscribe();
  }

  novaTarefa(): void {
    this.router.navigate(['/novo']);
  }

  editar(id: number): void {
    this.router.navigate(['/editar', id]);
  }

  deletar(id: number): void {
    if (confirm('Tem certeza que deseja excluir esta tarefa?')) {
      this.tarefaService.deletar(id).subscribe({
        next: () => {
          this.snackBar.open('Tarefa excluída com sucesso!', 'Fechar', { duration: 3000 });
          this.carregarTarefas();
        },
        error: (err) => {
          console.error('Erro ao excluir tarefa:', err);
          this.snackBar.open('Erro ao excluir tarefa.', 'Fechar', { duration: 3000 });
        }
      });
    }
  }
}
