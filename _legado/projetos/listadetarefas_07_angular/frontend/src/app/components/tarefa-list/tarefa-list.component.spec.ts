import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TarefaListComponent } from './tarefa-list.component';
import { TarefaService } from '../../services/tarefa.service';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

describe('TarefaListComponent', () => {
    let component: TarefaListComponent;
    let fixture: ComponentFixture<TarefaListComponent>;
    let tarefaService: TarefaService;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [TarefaListComponent],
            providers: [
                provideHttpClient(),
                provideHttpClientTesting(),
                provideRouter([]),
                TarefaService
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(TarefaListComponent);
        component = fixture.componentInstance;
        tarefaService = TestBed.inject(TarefaService);

        // Mock do listar para evitar chamada real no ngOnInit
        spyOn(tarefaService, 'listar').and.returnValue(of([]));

        fixture.detectChanges();
    });

    it('deve criar', () => {
        expect(component).toBeTruthy();
    });
});
