import { Component, OnInit } from '@angular/core';
// Importa o ReactiveFormsModule aqui!
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar'; // Importar
import { ActivatedRoute, Router } from '@angular/router';

// CORREÇÃO: Caminho relativo para sair de 'components/tarefa-form' e entrar em 'services'
import { TarefaService } from '../../services/tarefa.service';

// Imports de UI e Módulos Standalone
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { CommonModule } from '@angular/common'; // Para *ngIf

@Component({
  selector: 'app-tarefa-form',
  standalone: true, // Standalone
  imports: [ // Importa tudo que o template usa
    CommonModule,
    ReactiveFormsModule, // Essencial para [formGroup]
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatCheckboxModule,
    MatSnackBarModule
  ],
  templateUrl: './tarefa-form.component.html',
  styleUrls: ['./tarefa-form.component.scss']
})
export class TarefaFormComponent implements OnInit {

  tarefaForm: FormGroup;
  isEditMode = false;
  tarefaId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private tarefaService: TarefaService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {
    this.tarefaForm = this.fb.group({
      titulo: ['', [Validators.required, Validators.minLength(3)]],
      descricao: [''],
      concluida: [false]
    });
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      if (idParam) {
        this.isEditMode = true;
        this.tarefaId = Number(idParam);

        // CORREÇÃO: Adicionado tipo 'any' para evitar erro TS7006 (implicit any)
        this.tarefaService.buscarPorId(this.tarefaId).subscribe((tarefa: any) => {
          this.tarefaForm.patchValue(tarefa);
        });
      }
    });
  }

  onSubmit(): void {
    if (this.tarefaForm.invalid) {
      return;
    }

    const tarefaData = this.tarefaForm.value;

    if (this.isEditMode && this.tarefaId) {
      // Modo Edição (PUT)
      this.tarefaService.atualizar(this.tarefaId, tarefaData).subscribe(
        () => {
          this.snackBar.open('Tarefa atualizada com sucesso!', 'Fechar', { duration: 3000 });
          this.router.navigate(['/']);
        },
         // CORREÇÃO: Adicionado tipo 'any'
        (error: any) => this.snackBar.open('Erro ao atualizar tarefa.', 'Fechar', { duration: 3000 })
      );
    } else {
      // Modo Criação (POST)
      this.tarefaService.criar(tarefaData).subscribe(
        () => {
          this.snackBar.open('Tarefa criada com sucesso!', 'Fechar', { duration: 3000 });
          this.router.navigate(['/']);
        },
         // CORREÇÃO: Adicionado tipo 'any'
        (error: any) => this.snackBar.open('Erro ao criar tarefa.', 'Fechar', { duration: 3000 })
      );
    }
  }

  getErrorMessage(fieldName: string): string {
    const field = this.tarefaForm.get(fieldName);
    if (field?.hasError('required')) {
      return 'Campo obrigatório';
    }
    if (field?.hasError('minlength')) {
      return 'Deve ter no mínimo 3 caracteres';
    }
    return '';
  }

  cancelar(): void {
    this.router.navigate(['/']);
  }
}
