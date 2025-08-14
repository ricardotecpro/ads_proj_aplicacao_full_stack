# Todo List - API REST com Angular e Spring Boot

## Introdução

Este projeto é um sistema de lista de tarefas (**Todo List**) no estilo **Kanban**, onde o usuário pode adicionar tarefas, movê-las entre colunas ("A Fazer" e "Executadas") e removê-las. A aplicação é composta por um **backend** em **Spring Boot** e um **frontend** em **Angular**.

## O que é uma API REST?

Uma **API REST (Representational State Transfer)** é um conjunto de regras para comunicação entre sistemas utilizando o protocolo HTTP. No nosso projeto, o backend expõe endpoints REST que permitem ao frontend interagir com os dados de tarefas. Esses endpoints seguem os princípios:

- **Stateless**: Cada requisição é independente e contém todas as informações necessárias.
- **Utilização de métodos HTTP**:
    - `GET`: Recuperar dados (ex: listar tarefas)
    - `POST`: Criar um novo recurso (ex: adicionar uma tarefa)
    - `PUT`: Atualizar um recurso existente (ex: marcar uma tarefa como concluída)
    - `DELETE`: Remover um recurso (ex: excluir uma tarefa)

Por exemplo, para obter todas as tarefas, o frontend faz uma requisição `GET` para `http://localhost:8080/api/tasks`, e o backend responde com a lista de tarefas.

## Tecnologias Utilizadas

- **Backend:** Java 17, Spring Boot, Spring Data JPA, H2 Database
- **Frontend:** Angular, TypeScript, Bootstrap
- **Ferramentas:** Docker, Postman (para testes), Node.js, npm

---

## Estrutura de Arquivos

### Backend (Spring Boot)

```
backend/
│── src/
│   ├── main/
│   │   ├── java/com/example/todolist/
│   │   │   ├── model/Task.java
│   │   │   ├── repository/TaskRepository.java
│   │   │   ├── controller/TaskController.java
│   │   │   ├── config/DataLoader.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   ├── test/
│── pom.xml
│── README.md
```

### Configurando o `application.properties`

No diretório `src/main/resources`, configure o arquivo `application.properties` para usar apenas o H2:

```properties
# Configuração do banco de dados H2
spring.datasource.url=jdbc:h2:mem:todolist
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### Criando o CRUD no Backend

#### Modelo `Task`

```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private boolean completed;

    // Construtores, Getters e Setters
}
```

#### Repositório `TaskRepository`

```java
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
```

#### Controlador `TaskController`

```java
@RestController
@RequestMapping("/api/tasks")
@CrossOrigin("*")
public class TaskController {
    @Autowired
    private TaskRepository repository;
    
    @GetMapping
    public List<Task> getAll() {
        return repository.findAll();
    }
    
    @PostMapping
    public Task create(@RequestBody Task task) {
        return repository.save(task);
    }
    
    @PutMapping("/{id}")
    public Task update(@PathVariable Long id, @RequestBody Task task) {
        task.setId(id);
        return repository.save(task);
    }
    
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
```

### Adicionando Dados de Exemplo

Crie a classe `DataLoader.java` para popular o banco automaticamente:

```java
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private TaskRepository repository;

    @Override
    public void run(String... args) throws Exception {
        repository.save(new Task("Estudar Spring Boot", false));
        repository.save(new Task("Criar projeto Angular", true));
        repository.save(new Task("Testar integração API", false));
    }
}
```

---

## Testando o Banco de Dados

Após iniciar a aplicação com:

```sh
mvn spring-boot:run
```

Acesse o console H2:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:todolist`
- Usuário: `sa`
- Senha: (deixe em branco)

---

## Criando o CRUD no Frontend

### Criando o Serviço `task.service.ts`

```typescript
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Task {
  id?: number;
  title: string;
  completed: boolean;
}

@Injectable({ providedIn: 'root' })
export class TaskService {
  private apiUrl = 'http://localhost:8080/api/tasks';

  constructor(private http: HttpClient) {}

  getTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl);
  }

  addTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  updateTask(task: Task): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${task.id}`, task);
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
```

Agora a aplicação suporta CRUD completo com interface e interação do usuário. 🚀


---

### [ricardotecpro.github.io](https://ricardotecpro.github.io/)
