# Criando uma API REST Todo-List com Next.js e NestJS

Este projeto é uma aplicação de lista de tarefas no estilo "quadro" com tarefas "A Fazer" e "Executadas". Utilizaremos **Next.js** para o frontend e **NestJS** para o backend. O banco de dados **SQLite** será usado para testes e homologação, enquanto o **MySQL** será usado para produção.

## Estrutura do Projeto

A estrutura do projeto será organizada da seguinte forma:

```
/todo-list-app
  ├── backend/  # Código do backend NestJS
  ├── frontend/ # Código do frontend Next.js
  ├── docker/   # Configuração Docker para o projeto
  ├── README.md # Documentação do projeto
```

## Pré-requisitos

Antes de iniciar, certifique-se de ter instalado:

- **Node.js** (versão 20.19.0 ou superior)
- **Docker** (para facilitar a execução do banco de dados e da aplicação)
- **Git** (para versionamento de código)

Caso esteja com uma versão anterior do Node.js, recomenda-se atualizar para 20.19.0:

```sh
nvm install 20.19.0
nvm use 20.19.0
```

Caso queira instalar os pacotes ignorando a verificação de engine:

```sh
npm install @nestjs/typeorm typeorm sqlite3 --legacy-peer-deps
```

ou

```sh
npm install @nestjs/typeorm typeorm sqlite3 --force
```

## Criando a estrutura de arquivos correta

Execute os seguintes comandos no terminal para criar a estrutura do projeto:

```sh
mkdir todo-list-app && cd todo-list-app
npx @nestjs/cli new backend
cd backend
npm install
cd ..
npx create-next-app@latest frontend --typescript --eslint --tailwind --src-dir --app --import-alias "@/*"
```

## Configurando o Backend (NestJS)

### 1. Instalar dependências

```sh
cd backend
npm install @nestjs/common @nestjs/core @nestjs/typeorm typeorm mysql2 sqlite3 reflect-metadata
```

### 2. Configurar banco de dados

Edite o arquivo `src/app.module.ts`:

```typescript
import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { TaskModule } from './task/task.module';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: process.env.NODE_ENV === 'production' ? 'mysql' : 'sqlite',
      database: process.env.NODE_ENV === 'production' ? 'todo_db' : 'database.sqlite',
      entities: [__dirname + '/**/*.entity{.ts,.js}'],
      synchronize: true,
    }),
    TaskModule,
  ],
})
export class AppModule {}
```

### 3. Criar o controlador e serviço

```sh
npx nest generate resource task
```

Edite `src/task/task.controller.ts` para adicionar endpoints:

```typescript
import { Controller, Get, Post, Body } from '@nestjs/common';
import { TaskService } from './task.service';

@Controller('task')
export class TaskController {
  constructor(private readonly taskService: TaskService) {}

  @Get()
  async getAllTasks() {
    return this.taskService.findAll();
  }

  @Post()
  async createTask(@Body() taskData) {
    return this.taskService.create(taskData);
  }
}
```

### 4. Criar banco SQLite para testes

Crie um arquivo `backend/database.sqlite` para armazenar os dados localmente:

```sh
touch backend/database.sqlite
```

### 5. Rodar o backend

```sh
npm run start:dev
```

A API estará disponível em `http://localhost:3000/task`.

---

## Configurando o Frontend (Next.js)

### 1. Instalar dependências para Drag and Drop

```sh
cd ../frontend
npm install @hello-pangea/dnd
```

### 2. Criar componente para Drag and Drop com entrada de tarefas

Crie um novo arquivo **`src/components/TaskBoard.tsx`**:

```tsx
"use client";

import { useState, useEffect } from "react";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";

interface Task {
  id: string;
  title: string;
  status: string;
}

export default function TaskBoard() {
  const [tasks, setTasks] = useState<Task[]>([]);
  const [newTaskTitle, setNewTaskTitle] = useState("");

  useEffect(() => {
    fetch("http://localhost:3000/task")
      .then((res) => res.json())
      .then(setTasks);
  }, []);

  const addTask = async () => {
    if (!newTaskTitle.trim()) return;

    const response = await fetch("http://localhost:3000/task", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ title: newTaskTitle, status: "A Fazer" }),
    });

    if (response.ok) {
      const newTask = await response.json();
      setTasks((prevTasks) => [...prevTasks, newTask]);
      setNewTaskTitle("");
    }
  };

  return (
    <div>
      <div className="flex gap-2 mb-4">
        <input
          type="text"
          value={newTaskTitle}
          onChange={(e) => setNewTaskTitle(e.target.value)}
          placeholder="Digite uma nova tarefa"
          className="border p-2 flex-1 rounded"
        />
        <button onClick={addTask} className="bg-blue-500 text-white p-2 rounded">
          Adicionar
        </button>
      </div>
      <DragDropContext onDragEnd={() => {}}>
        <Droppable droppableId="todo">
          {(provided) => (
            <div ref={provided.innerRef} {...provided.droppableProps} className="bg-gray-100 p-4 rounded-md shadow-md">
              <h2 className="text-lg font-semibold mb-2">📌 A Fazer</h2>
              {tasks.map((task, index) => (
                <Draggable key={task.id} draggableId={task.id} index={index}>
                  {(provided) => (
                    <div ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps} className="bg-white p-2 mb-2 rounded shadow cursor-pointer">
                      {task.title}
                    </div>
                  )}
                </Draggable>
              ))}
              {provided.placeholder}
            </div>
          )}
        </Droppable>
      </DragDropContext>
    </div>
  );
}
```

Agora, edite `src/app/page.tsx` para importar esse componente:

```tsx
import TaskBoard from "@/components/TaskBoard";

export default function Home() {
  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold">Lista de Tarefas</h1>
      <TaskBoard />
    </div>
  );
}
```

### 3. Rodar o frontend

```sh
npm run dev
```

O frontend estará disponível em `http://localhost:3001`.


---

### [ricardotecpro.github.io](https://ricardotecpro.github.io/)
