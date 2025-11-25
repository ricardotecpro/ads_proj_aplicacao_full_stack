import { Component, OnInit } from '@angular/core';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // Importar
import { Router, RouterLink } from '@angular/router'; // Importar RouterLink
import { Observable } from 'rxjs';

// CORREÇÃO: Caminhos relativos para sair de 'components/tarefa-list' e entrar em 'models' e 'services'
import { Tarefa } from '../../models/tarefa.model';
import { TarefaService } from '../../services/tarefa.service';

// Imports de UI e Módulos Standalone
import { MatCardModule } from '@angular/material/card';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { CommonModule } from '@angular/common'; // Para | date e | async

@Component({
  selector: 'app-tarefa-list',
  standalone: true, // Standalone
  imports: [ // Importa tudo que o template usa
    CommonModule,
    RouterLink,
    MatCardModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatCheckboxModule,
    MatSnackBarModule
  ],
  templateUrl: './tarefa-list.component.html',
  styleUrls: ['./tarefa-list.component.scss']
})
export class TarefaListComponent implements OnInit {

  tarefas$!: Observable<Tarefa[]>;
  displayedColumns: string[] = ['id', 'titulo', 'concluida', 'dataCriacao', 'acoes'];

  constructor(
    private tarefaService: TarefaService,
    private router: Router,
    private snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.carregarTarefas();
  }

  carregarTarefas(): void {
    this.tarefas$ = this.tarefaService.listarTodas();
  }

  editar(id: number): void {
    this.router.navigate(['/editar', id]);
  }

  deletar(id: number): void {
    if (confirm('Tem certeza que deseja excluir esta tarefa?')) {
      this.tarefaService.deletar(id).subscribe(
        () => {
          this.snackBar.open('Tarefa excluída com sucesso!', 'Fechar', { duration: 3000 });
          this.carregarTarefas();
        },
        // CORREÇÃO: Adicionado tipo 'any' para evitar erro TS7006 (implicit any)
        (error: any) => {
          this.snackBar.open('Erro ao excluir tarefa.', 'Fechar', { duration: 3000 });
        }
      );
    }
  }
}
