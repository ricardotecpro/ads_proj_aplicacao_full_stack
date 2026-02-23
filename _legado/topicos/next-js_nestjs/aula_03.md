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

### 2. Criar página principal

Edite `src/app/page.tsx`:

```tsx
import { useState, useEffect } from 'react';

export default function Home() {
  const [tasks, setTasks] = useState([]);

  useEffect(() => {
    fetch('http://localhost:3000/task')
      .then((res) => res.json())
      .then((data) => setTasks(data));
  }, []);

  return (
    <div className="p-4">
      <h1 className="text-2xl font-bold">Lista de Tarefas</h1>
      <ul className="mt-4">
        {tasks.map((task) => (
          <li key={task.id} className="border-b py-2">{task.title} - {task.status}</li>
        ))}
      </ul>
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

## Testando a Aplicação Completa

1. **Rodar o backend**:
    
    ```sh
    cd backend
    npm run start:dev
    ```
    
2. **Rodar o frontend**:
    
    ```sh
    cd ../frontend
    npm run dev
    ```
    
3. **Acessar no navegador**:
    
    - Backend API: `http://localhost:3000/task`
    - Frontend: `http://localhost:3001`

---


---

### [ricardotecpro.github.io](https://ricardotecpro.github.io/)
