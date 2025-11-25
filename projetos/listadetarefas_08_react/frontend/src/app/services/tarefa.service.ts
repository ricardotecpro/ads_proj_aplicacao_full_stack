import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tarefa } from '../models/tarefa.model';

@Injectable({
  providedIn: 'root'
})
export class TarefaService {

  private readonly API_URL = 'http://localhost:8080/api/tarefas/';

  constructor(private http: HttpClient) { }

  // GET /api/tarefas
  listarTodas(): Observable<Tarefa[]> {
    return this.http.get<Tarefa[]>(this.API_URL);
  }

  // GET /api/tarefas/{id}
  buscarPorId(id: number): Observable<Tarefa> {
    return this.http.get<Tarefa>(`${this.API_URL}${id}`);
  }

  // POST /api/tarefas
  criar(tarefa: Partial<Tarefa>): Observable<Tarefa> {
    return this.http.post<Tarefa>(this.API_URL, tarefa);
  }

  // PUT /api/tarefas/{id}
  atualizar(id: number, tarefa: Partial<Tarefa>): Observable<Tarefa> {
    return this.http.put<Tarefa>(`${this.API_URL}${id}`, tarefa);
  }

  // DELETE /api/tarefas/{id}
  deletar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}${id}`);
  }
}
