import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TarefaFormComponent } from './tarefa-form.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';

describe('TarefaFormComponent', () => {
    let component: TarefaFormComponent;
    let fixture: ComponentFixture<TarefaFormComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [TarefaFormComponent],
            providers: [
                provideHttpClient(),
                provideHttpClientTesting(),
                provideRouter([])
            ]
        })
            .compileComponents();

        fixture = TestBed.createComponent(TarefaFormComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('deve criar', () => {
        expect(component).toBeTruthy();
    });
});
