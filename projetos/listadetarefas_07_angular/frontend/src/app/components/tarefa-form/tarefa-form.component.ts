import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TarefaService } from '../../services/tarefa.service';
import { Tarefa } from '../../models/tarefa.model';

@Component({
    selector: 'app-tarefa-form',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './tarefa-form.component.html',
    styleUrls: ['./tarefa-form.component.css']
})
export class TarefaFormComponent implements OnInit {
    tarefa: Tarefa = {
        titulo: '',
        concluida: false
    };
    isEdicao = false;

    constructor(
        private tarefaService: TarefaService,
        private router: Router,
        private route: ActivatedRoute
    ) { }

    ngOnInit(): void {
        const id = this.route.snapshot.paramMap.get('id');
        if (id) {
            this.isEdicao = true;
            this.tarefaService.listar().subscribe(tarefas => {
                const encontrada = tarefas.find(t => t.id === +id);
                if (encontrada) {
                    this.tarefa = { ...encontrada };
                }
            });
            // Idealmente, usaríamos um método buscarPorId(id) no serviço, mas listar() funciona para este exemplo simples.
            // Se implementamos buscarPorId no serviço, melhor usar ele:
            // this.tarefaService.buscarPorId(+id).subscribe(...)
        }
    }

    salvar(): void {
        if (this.isEdicao && this.tarefa.id) {
            this.tarefaService.atualizar(this.tarefa.id, this.tarefa).subscribe({
                next: () => this.router.navigate(['/tarefas']),
                error: (erro) => console.error('Erro ao atualizar tarefa', erro)
            });
        } else {
            this.tarefaService.criar(this.tarefa).subscribe({
                next: () => this.router.navigate(['/tarefas']),
                error: (erro) => console.error('Erro ao criar tarefa', erro)
            });
        }
    }
}
