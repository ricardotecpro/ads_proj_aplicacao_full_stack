import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TarefaService } from '../../services/tarefa.service';
import { Tarefa } from '../../models/tarefa.model';

@Component({
    selector: 'app-tarefa-list',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './tarefa-list.component.html',
    styleUrls: ['./tarefa-list.component.css']
})
export class TarefaListComponent implements OnInit {
    tarefas: Tarefa[] = [];

    constructor(private tarefaService: TarefaService) { }

    ngOnInit(): void {
        this.carregarTarefas();
    }

    carregarTarefas(): void {
        this.tarefaService.listar().subscribe({
            next: (dados) => this.tarefas = dados,
            error: (erro) => console.error('Erro ao carregar tarefas', erro)
        });
    }

    deletar(id: number): void {
        if (confirm('Tem certeza que deseja deletar esta tarefa?')) {
            this.tarefaService.deletar(id).subscribe({
                next: () => this.carregarTarefas(),
                error: (erro) => console.error('Erro ao deletar tarefa', erro)
            });
        }
    }

    alterarStatus(tarefa: Tarefa): void {
        const novoStatus = !tarefa.concluida;
        // O id é opcional na interface, mas sempre vem do backend. Usamos ! para garantir ou verificamos.
        if (tarefa.id) {
            this.tarefaService.atualizarStatus(tarefa.id, novoStatus).subscribe({
                next: (tarefaAtualizada) => tarefa.concluida = tarefaAtualizada.concluida,
                error: (erro) => console.error('Erro ao atualizar status', erro)
            });
        }
    }
}
