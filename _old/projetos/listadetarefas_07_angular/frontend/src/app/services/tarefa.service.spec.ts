import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TarefaService } from './tarefa.service';
import { Tarefa } from '../models/tarefa.model';

describe('TarefaService', () => {
    let service: TarefaService;
    let httpMock: HttpTestingController;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                TarefaService,
                provideHttpClient(),
                provideHttpClientTesting()
            ]
        });
        service = TestBed.inject(TarefaService);
        httpMock = TestBed.inject(HttpTestingController);
    });

    afterEach(() => {
        httpMock.verify();
    });

    it('deve ser criado', () => {
        expect(service).toBeTruthy();
    });

    it('deve listar tarefas', () => {
        const tarefasMock: Tarefa[] = [
            { id: 1, titulo: 'Teste 1', concluida: false },
            { id: 2, titulo: 'Teste 2', concluida: true }
        ];

        service.listar().subscribe(tarefas => {
            expect(tarefas.length).toBe(2);
            expect(tarefas).toEqual(tarefasMock);
        });

        const req = httpMock.expectOne('http://localhost:8088/tarefas');
        expect(req.request.method).toBe('GET');
        req.flush(tarefasMock);
    });

    it('deve criar tarefa', () => {
        const novaTarefa: Tarefa = { titulo: 'Nova', concluida: false };
        const tarefaCriada: Tarefa = { id: 1, ...novaTarefa };

        service.criar(novaTarefa).subscribe(tarefa => {
            expect(tarefa).toEqual(tarefaCriada);
        });

        const req = httpMock.expectOne('http://localhost:8088/tarefas');
        expect(req.request.method).toBe('POST');
        req.flush(tarefaCriada);
    });
});
