# Construindo o Frontend com Angular

Após a criação da nossa API, o próximo passo é desenvolver a interface web que irá interagir com ela. Utilizaremos **Angular** para criar uma Single Page Application (SPA) moderna e reativa.

## Etapa 1: Preparando o Ambiente e Criando o Projeto

O primeiro passo é garantir que todas as ferramentas necessárias estejam instaladas e criar a estrutura inicial do nosso projeto Angular.

### 1\. Pré-requisitos

Abra seu terminal e verifique se o Node.js e o Angular CLI estão instalados.

```bash
node -v
ng version
```

> **Nota:** Se você não tiver o Node.js, baixe-o no [site oficial](https://nodejs.org/). Para instalar o Angular CLI, execute o comando: `npm install -g @angular/cli`.

### 2\. Criando o Projeto Angular

Navegue até o diretório onde você armazena seus projetos (fora da pasta da API) e execute o comando para criar uma nova aplicação:

```bash
ng new todolist-web
```

O Angular CLI fará algumas perguntas durante a configuração:

  - `Would you like to add Angular routing?` (Deseja adicionar roteamento?): **n**
    *(Para este projeto simples, não precisaremos de rotas complexas.)*
  - `Which stylesheet format would you like to use?` (Qual formato de folha de estilos?): **CSS**
    *(Manteremos a opção padrão e mais simples.)*

Aguarde o Angular CLI criar a pasta `todolist-web` e instalar todas as dependências. Depois, abra este novo projeto em seu editor de código (VS Code é altamente recomendado).

### 3\. Gerando os Blocos de Construção

Com o projeto criado, vamos usar o Angular CLI para gerar os arquivos essenciais da nossa aplicação. Navegue até a pasta do projeto no terminal:

```bash
cd todolist-web
```

Agora, execute os seguintes comandos:

```bash
# 1. Crie uma interface para o modelo de dados 'Tarefa'
ng generate interface models/tarefa

# 2. Crie o serviço que irá se comunicar com a API
ng generate service services/tarefa

# 3. Crie o componente visual que exibirá a lista de tarefas
ng generate component components/task-list
```

## Etapa 2: Conectando o Frontend com o Backend

Agora vamos escrever o código que faz a ponte entre o Angular e a API Spring Boot. A implementação varia um pouco dependendo se o seu projeto foi criado com **Módulos (padrão antigo)** ou **Componentes Standalone (padrão moderno)**.

### Definindo a Interface `Tarefa` (Comum a Ambos)

Independente da abordagem, o modelo de dados é o mesmo. Abra o arquivo `src/app/models/tarefa.ts` e defina sua estrutura:

```typescript
// src/app/models/tarefa.ts
export interface Tarefa {
  id?: number; // O ID é opcional, pois o backend o gera na criação.
  descricao: string;
  concluida: boolean;
}
```

-----

### Opção A: Configuração com Módulos (app.module.ts)

Se seu projeto utiliza um `app.module.ts`, siga estes passos.

#### 1\. Configure o `app.module.ts`

Para que nosso serviço possa fazer requisições HTTP e para usarmos formulários, precisamos importar os módulos `HttpClientModule` e `FormsModule`. Abra `src/app/app.module.ts` e modifique-o:

```typescript
// src/app/app.module.ts
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'; // 1. Importe
import { FormsModule } from '@angular/forms';           // 2. Importe

import { AppComponent } from './app.component';
import { TaskListComponent } from './components/task-list/task-list.component';

@NgModule({
  declarations: [
    AppComponent,
    TaskListComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule, // 3. Adicione aos imports
    FormsModule       // 4. Adicione também
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
```

#### 2\. Implemente o `TarefaService`

Abra `src/app/services/tarefa.service.ts` e adicione os métodos para realizar as operações CRUD (Create, Read, Update, Delete) na API.

```typescript
// src/app/services/tarefa.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tarefa } from '../models/tarefa';

@Injectable({
  providedIn: 'root'
})
export class TarefaService {

  // URL base da nossa API
  private apiUrl = 'http://localhost:8080/api/tarefas';

  // Injetamos o HttpClient para fazer requisições HTTP
  constructor(private http: HttpClient) { }

  // READ: Retorna a lista de tarefas
  getTarefas(): Observable<Tarefa[]> {
    return this.http.get<Tarefa[]>(this.apiUrl);
  }

  // CREATE: Envia uma nova tarefa para a API
  addTarefa(tarefa: Tarefa): Observable<Tarefa> {
    return this.http.post<Tarefa>(this.apiUrl, tarefa);
  }

  // UPDATE: Atualiza uma tarefa existente
  updateTarefa(tarefa: Tarefa): Observable<Tarefa> {
    const url = `${this.apiUrl}/${tarefa.id}`;
    return this.http.put<Tarefa>(url, tarefa);
  }

  // DELETE: Deleta uma tarefa pelo seu ID
  deleteTarefa(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }
}
```

-----

### Opção B: Configuração com Componentes Standalone

Se seu projeto não possui um `app.module.ts` e usa componentes `standalone: true`, a configuração é um pouco diferente.

#### 1\. Configure o Acesso HTTP (A Grande Mudança)

A configuração de provedores globais é feita no arquivo `src/app/app.config.ts`. É aqui que substituímos o `HttpClientModule`.

```typescript
// src/app/app.config.ts
import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http'; // 1. IMPORTE

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient() // 2. ADICIONE AQUI
  ]
};
```

> O `TarefaService` permanece **exatamente o mesmo** da Opção A, pois ele já é "providedIn: 'root'".

#### 2\. Integre o Componente Filho

Em uma aplicação standalone, o `AppComponent` precisa importar explicitamente os componentes que utiliza. Abra `src/app/app.component.ts`:

```typescript
// src/app/app.component.ts
import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TaskListComponent } from './components/task-list/task-list.component'; // 1. IMPORTE

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    TaskListComponent // 2. ADICIONE AOS IMPORTS
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'todolist-web';
}
```

-----

## Etapa 3: Implementando o Componente `TaskListComponent`

Com a estrutura e os serviços configurados, a próxima etapa é dar vida ao nosso componente principal.

### 1\. Integrando o Componente no `AppComponent`

Independente da abordagem (Módulos ou Standalone), o passo final da configuração é garantir que o `app.component.html` renderize nosso `TaskListComponent`. Abra este arquivo, **apague todo o conteúdo padrão** e adicione apenas a seguinte linha:

```html
<app-task-list></app-task-list>
```

### 2\. A Lógica do Componente (`task-list.component.ts`)

Abra `src/app/components/task-list/task-list.component.ts`. Aqui, vamos injetar o `TarefaService`, buscar os dados e definir os métodos que o HTML irá chamar.

> **Importante:** Em uma aplicação **Standalone**, você deve adicionar `CommonModule` e `FormsModule` ao array `imports` do decorador `@Component`, pois eles fornecem as diretivas `*ngFor` e `[(ngModel)]`.

```typescript
// src/app/components/task-list/task-list.component.ts
import { Component, OnInit } from '@angular/core';
import { TarefaService } from '../../services/tarefa.service';
import { Tarefa } from '../../models/tarefa';
import { CommonModule } from '@angular/common'; // Importe para *ngFor, etc.
import { FormsModule } from '@angular/forms';   // Importe para [(ngModel)]

@Component({
  selector: 'app-task-list',
  standalone: true,
  imports: [
    CommonModule, // Adicione aqui
    FormsModule   // E aqui
  ],
  templateUrl: './task-list.component.html',
  styleUrl: './task-list.component.css'
})
export class TaskListComponent implements OnInit {

  tarefas: Tarefa[] = [];
  novaTarefa: Tarefa = { descricao: '', concluida: false };

  constructor(private tarefaService: TarefaService) { }

  ngOnInit(): void {
    this.carregarTarefas();
  }

  carregarTarefas(): void {
    this.tarefaService.getTarefas().subscribe(data => {
      this.tarefas = data;
    });
  }

  adicionarTarefa(): void {
    if (this.novaTarefa.descricao.trim() === '') return;

    this.tarefaService.addTarefa(this.novaTarefa).subscribe(tarefaAdicionada => {
      this.tarefas.push(tarefaAdicionada);
      this.novaTarefa = { descricao: '', concluida: false };
    });
  }

  atualizarStatus(tarefa: Tarefa): void {
    this.tarefaService.updateTarefa(tarefa).subscribe();
  }

  deletarTarefa(id: number | undefined): void {
    if (id === undefined) return;

    this.tarefaService.deleteTarefa(id).subscribe(() => {
      this.tarefas = this.tarefas.filter(t => t.id !== id);
    });
  }
}
```

### 3\. A Aparência do Componente (`task-list.component.html`)

Abra `src/app/components/task-list/task-list.component.html` e substitua seu conteúdo pelo HTML que irá renderizar nosso formulário e a lista de tarefas.

```html
<div class="container">
  <h1>Minha Lista de Tarefas</h1>

  <form class="form-add" (ngSubmit)="adicionarTarefa()">
    <input
      type="text"
      placeholder="O que precisa ser feito?"
      [(ngModel)]="novaTarefa.descricao"
      name="descricao"
      required
    >
    <button type="submit">Adicionar</button>
  </form>

  <ul class="task-list">
    <li *ngFor="let tarefa of tarefas">
      <input
        type="checkbox"
        [(ngModel)]="tarefa.concluida"
        (change)="atualizarStatus(tarefa)"
      >
      <span [ngClass]="{'completed': tarefa.concluida}">
        {{ tarefa.descricao }}
      </span>
      <button class="delete-btn" (click)="deletarTarefa(tarefa.id)">×</button>
    </li>
  </ul>
</div>
```

### 4\. O Estilo do Componente (`task-list.component.css`)

Por fim, adicione um pouco de CSS em `src/app/components/task-list/task-list.component.css` para deixar a aplicação mais agradável.

```css
.container {
  max-width: 600px;
  margin: 2rem auto;
  font-family: sans-serif;
  padding: 1rem;
}

.form-add {
  display: flex;
  margin-bottom: 1.5rem;
}

.form-add input {
  flex-grow: 1;
  padding: 0.8rem;
  border: 1px solid #ccc;
  border-radius: 4px 0 0 4px;
}

.form-add button {
  padding: 0.8rem 1.2rem;
  border: none;
  background-color: #007bff;
  color: white;
  cursor: pointer;
  border-radius: 0 4px 4px 0;
}

.task-list {
  list-style: none;
  padding: 0;
}

.task-list li {
  display: flex;
  align-items: center;
  padding: 0.8rem;
  border-bottom: 1px solid #eee;
}

.task-list li:last-child {
  border-bottom: none;
}

.task-list li span {
  flex-grow: 1;
  margin-left: 1rem;
}

.task-list li span.completed {
  text-decoration: line-through;
  color: #888;
}

.delete-btn {
  border: none;
  background: transparent;
  color: #ff4d4d;
  cursor: pointer;
  font-size: 1.2rem;
  font-weight: bold;
}
```

## Etapa 4: Rodando a Aplicação Completa\!

Chegou a hora de ver tudo funcionando junto.

1.  **Garanta que sua API Spring Boot esteja rodando.**
2.  Abra um terminal na pasta do projeto Angular (`todolist-web`) e execute:

<!-- end list -->

```bash
ng serve --open
```

Seu navegador abrirá em `http://localhost:4200` e você poderá interagir com sua aplicação de lista de tarefas\!

-----

## Troubleshooting: Erros de Compilação

> **Erro Comum:** `Property 'addTarefa' does not exist on type 'TarefaService'.`
>
> **Causa:** Este erro geralmente ocorre quando o componente (`TaskListComponent`) tenta chamar um método que não foi definido ou foi escrito incorretamente no serviço (`TarefaService`).
>
> **Solução:** Verifique o arquivo `src/app/services/tarefa.service.ts` e garanta que ele contenha o código completo e correto para todos os métodos CRUD, conforme mostrado na **Etapa 2 (Opção A ou B)** deste guia. Qualquer erro de digitação ou método ausente causará essa falha na compilação.


---

### [ricardotecpro.github.io](https://ricardotecpro.github.io/)
