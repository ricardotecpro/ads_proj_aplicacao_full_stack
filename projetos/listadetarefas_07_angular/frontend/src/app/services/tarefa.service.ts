import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tarefa } from '../models/tarefa.model';

@Injectable({
    providedIn: 'root'
})
export class TarefaService {
    private apiUrl = 'http://localhost:8088/tarefas';

    constructor(private http: HttpClient) { }

    listar(): Observable<Tarefa[]> {
        return this.http.get<Tarefa[]>(this.apiUrl);
    }

    buscarPorId(id: number): Observable<Tarefa> {
        return this.http.get<Tarefa>(`${this.apiUrl}/${id}`);
    }

    criar(tarefa: Tarefa): Observable<Tarefa> {
        return this.http.post<Tarefa>(this.apiUrl, tarefa);
    }

    atualizar(id: number, tarefa: Tarefa): Observable<Tarefa> {
        return this.http.put<Tarefa>(`${this.apiUrl}/${id}`, tarefa);
    }

    atualizarStatus(id: number, concluida: boolean): Observable<Tarefa> {
        return this.http.patch<Tarefa>(`${this.apiUrl}/${id}/status`, { concluida });
    }

    atualizarTitulo(id: number, titulo: string): Observable<Tarefa> {
        return this.http.patch<Tarefa>(`${this.apiUrl}/${id}/titulo`, { titulo });
    }

    deletar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
