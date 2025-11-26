import { Routes } from '@angular/router';
import { TarefaListComponent } from './components/tarefa-list/tarefa-list.component';
import { TarefaFormComponent } from './components/tarefa-form/tarefa-form.component';

export const routes: Routes = [
    { path: '', redirectTo: 'tarefas', pathMatch: 'full' },
    { path: 'tarefas', component: TarefaListComponent },
    { path: 'tarefas/nova', component: TarefaFormComponent },
    { path: 'tarefas/editar/:id', component: TarefaFormComponent }
];
