# 🎓 Módulo 2: Frontend com React

**Objetivo**: Construir uma interface moderna para interagir com nossa API.

---

## 1. O que é React?
React é uma biblioteca JavaScript para criar interfaces de usuário baseadas em **Componentes**. Um botão, um formulário, um cabeçalho... tudo é um componente reutilizável.

---

## 2. Criando o Projeto

Vamos usar o **Vite**, uma ferramenta ultra-rápida para criar projetos React.

1. No terminal, na raiz do projeto:
   ```bash
   npm create vite@latest frontend -- --template react
   cd frontend
   npm install
   ```
2. Instale as bibliotecas extras:
   ```bash
   npm install axios react-router-dom
   ```

---

## 3. Configurando a API

Crie `src/services/api.js`. Isso centraliza as chamadas ao Backend.

```javascript
import axios from 'axios';

const api = axios.create({
    baseURL: 'http://localhost:8080/api'
});

export default api;
```

---

## 4. Criando um Componente

Crie `src/components/TaskList.jsx`.

```jsx
import { useEffect, useState } from 'react';
import api from '../services/api';

function TaskList() {
    const [tasks, setTasks] = useState([]);

    useEffect(() => {
        api.get('/tarefas').then(response => {
            setTasks(response.data);
        });
    }, []);

    return (
        <div>
            <h1>Minhas Tarefas</h1>
            <ul>
                {tasks.map(task => (
                    <li key={task.id}>{task.titulo}</li>
                ))}
            </ul>
        </div>
    );
}

export default TaskList;
```

*Explicação: `useEffect` busca os dados quando a tela carrega. `useState` guarda os dados na memória do componente.*

---

## 🛑 Pare e Teste

1. Adicione `<TaskList />` no seu `App.jsx`.
2. Rode o frontend: `npm run dev`.
3. Acesse `http://localhost:5173`.
4. Se o Backend estiver rodando, você verá as tarefas cadastradas!

---

## 🏆 Desafio
Crie um formulário para adicionar novas tarefas usando `api.post('/tarefas', novaTarefa)`.
