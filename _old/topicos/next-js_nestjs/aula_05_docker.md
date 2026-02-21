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
import { Task } from './task/task.entity';
import { TaskModule } from './task/task.module';

@Module({
  imports: [
    TypeOrmModule.forRoot({
      type: process.env.NODE_ENV === 'production' ? 'mysql' : 'sqlite',
      database: process.env.NODE_ENV === 'production' ? 'todo_db' : 'database.sqlite',
      entities: [Task],
      synchronize: true,
    }),
    TaskModule,
  ],
})
export class AppModule {}
```

### 3. Criar a entidade Task

Crie o arquivo `src/task/task.entity.ts`:

```typescript
import { Entity, Column, PrimaryGeneratedColumn } from 'typeorm';

@Entity()
export class Task {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  title: string;

  @Column()
  status: string;
}
```

### 4. Criar o controlador, serviço e repositório

```sh
npx nest generate resource task
```

Edite `src/task/task.controller.ts` para adicionar endpoints CRUD:

```typescript
import { Controller, Get, Post, Body, Patch, Param, Delete } from '@nestjs/common';
import { TaskService } from './task.service';
import { CreateTaskDto } from './dto/create-task.dto';
import { UpdateTaskDto } from './dto/update-task.dto';

@Controller('task')
export class TaskController {
  constructor(private readonly taskService: TaskService) {}

  @Post()
  create(@Body() createTaskDto: CreateTaskDto) {
    return this.taskService.create(createTaskDto);
  }

  @Get()
  findAll() {
    return this.taskService.findAll();
  }

  @Get(':id')
  findOne(@Param('id') id: string) {
    return this.taskService.findOne(+id);
  }

  @Patch(':id')
  update(@Param('id') id: string, @Body() updateTaskDto: UpdateTaskDto) {
    return this.taskService.update(+id, updateTaskDto);
  }

  @Delete(':id')
  remove(@Param('id') id: string) {
    return this.taskService.remove(+id);
  }
}
```

### 5. Criar banco SQLite para testes

```sh
touch database.sqlite
```

### 6. Rodar o backend

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

### 2. Criar componente para Drag and Drop

Crie um novo arquivo **`src/components/TaskBoard.tsx`**:

```tsx
"use client";

import { useState } from "react";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";

export default function TaskBoard() {
  const [tasks, setTasks] = useState([]);

  const onDragEnd = (result) => {
    if (!result.destination) return;
    const updatedTasks = [...tasks];
    const [movedTask] = updatedTasks.splice(result.source.index, 1);
    movedTask.status = result.destination.droppableId;
    updatedTasks.splice(result.destination.index, 0, movedTask);
    setTasks(updatedTasks);
  };

  return (
    <DragDropContext onDragEnd={onDragEnd}>
      <Droppable droppableId="todo">
        {(provided) => (
          <div ref={provided.innerRef} {...provided.droppableProps}>
            <h2>A Fazer</h2>
            {tasks.filter(task => task.status === "A Fazer").map((task, index) => (
              <Draggable key={task.id} draggableId={task.id} index={index}>
                {(provided) => (
                  <div ref={provided.innerRef} {...provided.draggableProps} {...provided.dragHandleProps}>
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
  );
}
```

Agora, edite `src/app/page.tsx` para importar esse componente:

```tsx
import TaskBoard from "@/components/TaskBoard";

export default function Home() {
  return (
    <div>
      <h1>Lista de Tarefas</h1>
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


---

### [ricardotecpro.github.io](https://ricardotecpro.github.io/)
