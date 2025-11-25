import { Routes } from '@angular/router';

export const routes: Routes = [
  // Rota principal: carrega o TarefaListComponent
  {
    path: '',
    loadComponent: () =>
      import('./components/tarefa-list/tarefa-list.component')
      .then(m => m.TarefaListComponent)
  },

  // Rota para criar nova tarefa: carrega o TarefaFormComponent
  {
    path: 'novo',
    loadComponent: () =>
      import('./components/tarefa-form/tarefa-form.component')
      .then(m => m.TarefaFormComponent)
  },

  // Rota para editar uma tarefa (passando o ID): carrega o mesmo TarefaFormComponent
  {
    path: 'editar/:id',
    loadComponent: () =>
      import('./components/tarefa-form/tarefa-form.component')
      .then(m => m.TarefaFormComponent)
  },

  // Redireciona qualquer rota não encontrada para a principal
  { path: '**', redirectTo: '' }
];
