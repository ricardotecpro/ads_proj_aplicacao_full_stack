// src/app/app.component.ts
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
// 1. IMPORTE o seu componente aqui
import { TaskListComponent } from './components/task-list/task-list';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    TaskListComponent // 2. ADICIONE o componente ao array de imports
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  title = 'listadetarefas-web';
}
