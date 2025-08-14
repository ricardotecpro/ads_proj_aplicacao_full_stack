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

- **Node.js** (versão 20.11 ou superior)
- **Docker** (para facilitar a execução do banco de dados e da aplicação)
- **Git** (para versionamento de código)

## Configurando o Backend (NestJS)

### 1. Criar o backend com NestJS

No terminal, execute:

```sh
mkdir todo-list-app && cd todo-list-app
npx @nestjs/cli new backend
cd backend
npm install
```

### 2. Instalar dependências

```sh
npm install @nestjs/typeorm typeorm mysql2 sqlite3 reflect-metadata
```

### 3. Configurar banco de dados

Edite o arquivo `src/app.module.ts`:

```ts
import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Task } from './task.entity';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: process.env.NODE_ENV === 'production' ? 'mysql' : 'sqlite',
      database: process.env.NODE_ENV === 'production' ? 'todo_db' : 'database.sqlite',
      entities: [Task],
      synchronize: true,
    }),
  ],
})
export class AppModule {}
```

### 4. Criar a entidade Task

Crie o arquivo `src/task.entity.ts`:

```ts
import { Entity, Column, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
export class Task {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  title: string;

  @Column()
  status: 'A Fazer' | 'Executada';
}
```

### 5. Criar o controlador e serviço

```sh
npx nest generate resource task
```

Edite `src/task/task.controller.ts` para adicionar endpoints:

```ts
@Get()
async getAllTasks() {
  return this.taskService.findAll();
}

@Post()
async createTask(@Body() taskData: CreateTaskDto) {
  return this.taskService.create(taskData);
}
```

### 6. Criar banco SQLite para testes

```sh
touch database.sqlite
```

### 7. Rodar o backend

```sh
npm run start:dev
```

A API estará disponível em `http://localhost:3000/task`.

### 8. Testar API no navegador

Abra no navegador ou use **Postman**:

- `GET http://localhost:3000/task` → Lista todas as tarefas
- `POST http://localhost:3000/task` → Cria uma nova tarefa

---

## Configurando o Frontend (Next.js)

### 1. Criar o frontend com Next.js

```sh
cd ..
npx create-next-app frontend --typescript --eslint --tailwind --src-dir --app --import-alias "@/*"
cd frontend
npm install
```

### 2. Criar componente para Drag and Drop

Crie um novo arquivo **`src/components/TaskBoard.tsx`**:

```tsx
"use client";

import { useState } from "react";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";

const initialTasks = [
  { id: "1", title: "Estudar Next.js", status: "A Fazer" },
  { id: "2", title: "Criar API NestJS", status: "A Fazer" },
  { id: "3", title: "Testar aplicação", status: "Executada" },
];

export default function TaskBoard() {
  const [tasks, setTasks] = useState(initialTasks);

  const onDragEnd = (result) => {
    if (!result.destination) return;

    const updatedTasks = Array.from(tasks);
    const [movedTask] = updatedTasks.splice(result.source.index, 1);
    movedTask.status = result.destination.droppableId === "todo" ? "A Fazer" : "Executada";
    updatedTasks.splice(result.destination.index, 0, movedTask);

    setTasks(updatedTasks);
  };

  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <div className="grid grid-cols-2 gap-4 p-4">
        <Droppable droppableId="todo">
          {(provided) => (
            <div ref={provided.innerRef} {...provided.droppableProps} className="bg-gray-100 p-4 rounded-md shadow-md">
              <h2 className="text-lg font-semibold mb-2">📌 A Fazer</h2>
              {tasks.filter(task => task.status === "A Fazer").map((task, index) => (
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
      </div>
    </DragDropContext>
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

## Próximos Passos

- Criar **autenticação de usuários**
- Configurar **Docker** para facilitar a implantação

---

---

### 🚀 [ricardotecpro.github.io](https://ricardotecpro.github.io/)
