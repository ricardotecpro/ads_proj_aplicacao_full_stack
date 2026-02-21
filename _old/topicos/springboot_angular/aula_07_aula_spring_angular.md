# Todo List - API REST com Angular e Spring Boot

## Introdução

Este projeto é um sistema de lista de tarefas (**Todo List**) no estilo **Kanban**, onde o usuário pode adicionar tarefas, movê-las entre colunas ("A Fazer" e "Executadas") e removê-las. A aplicação é composta por um **backend** em **Spring Boot** e um **frontend** em **Angular**.

## Tecnologias Utilizadas

- **Backend:** Java 21, Spring Boot, Spring Data JPA, MySQL (ou H2 para testes)
- **Frontend:** Angular, TypeScript, Bootstrap
- **Ferramentas:** Docker, Postman (para testes), Node.js, npm

---

## Estrutura de Arquivos

### Backend (Spring Boot)

```
backend/
│── src/
│   ├── main/
│   │   ├── java/com/example/listatarefas/
│   │   │   ├── model/Task.java
│   │   │   ├── repository/TaskRepository.java
│   │   │   ├── controller/TaskController.java
│   │   ├── resources/
│   │   │   ├── application.properties
│   ├── test/
│── pom.xml
│── README.md
```

### Frontend (Angular)

```
frontend/
│── src/
│   ├── app/
│   │   ├── task-board/
│   │   │   ├── task-board.component.ts
│   │   │   ├── task-board.component.html
│   │   ├── services/task.service.ts
│   ├── assets/
│   ├── index.html
│── angular.json
│── package.json
│── README.md
```

---

## 1. Configurando o Backend (Spring Boot)

### Criando o projeto Spring Boot

1. Acesse [Spring Initializr](https://start.spring.io/)
2. Configure as dependências:
    - Spring Web
    - Spring Data JPA
    - MySQL Driver
    - H2 Database (para testes locais)
3. Baixe o projeto e abra no IntelliJ ou VS Code.

### Configurando o `application.properties`

No diretório `src/main/resources`, configure o arquivo `application.properties`:

```properties
# Banco de dados para desenvolvimento
spring.datasource.url=jdbc:mysql://localhost:3306/listatarefas
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Banco de dados para testes
spring.h2.console.enabled=true
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
```

### Criando a Entidade `Task`

```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private boolean completed;
    
    // Getters e Setters
}
```

### Criando o Repositório `TaskRepository`

```java
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
```

### Criando o Controlador `TaskController`

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

### Testando a API no Navegador/Postman

1. **Rodar a aplicação**
    
    ```sh
    mvn spring-boot:run
    ```
    
2. **Testar no navegador**
    - Acesse `http://localhost:8080/api/tasks` para listar as tarefas.
    - Use o Postman para testar `POST`, `PUT` e `DELETE`.

---

## 2. Configurando o Frontend (Angular)

### Criando o projeto Angular

1. Instale o Angular CLI (caso ainda não tenha):
    
    ```sh
    npm install -g @angular/cli
    ```
    
2. Crie um novo projeto:
    
    ```sh
    ng new listatarefas-frontend
    ```
    
3. Entre no diretório do projeto:
    
    ```sh
    cd listatarefas-frontend
    ```
    

### Instalando dependências

```sh
npm install bootstrap
npm install @angular/common @angular/forms @angular/router @angular/http
```

### Criando o Componente `TaskBoard`

1. Gere o componente:
    
    ```sh
    ng generate component TaskBoard
    ```
    
2. Edite `task-board.component.html`:

```html
<div class="container">
  <h2>Todo List</h2>
  <input [(ngModel)]="newTask" placeholder="Nova tarefa">
  <button (click)="addTask()">Adicionar</button>

  <div class="tasks">
    <h3>A Fazer</h3>
    <ul>
      <li *ngFor="let task of tasks" (click)="toggleTask(task)">{{ task.title }}</li>
    </ul>
  </div>
</div>
```

---

## 3. Rodando e Testando a Aplicação

1. **Subir o backend:**
    
    ```sh
    mvn spring-boot:run
    ```
    
2. **Subir o frontend:**
    
    ```sh
    ng serve --open
    ```
    
3. **Acessar no navegador:**
    - Frontend: `http://localhost:4200`
    - Backend: `http://localhost:8080/api/tasks`

---

## Conclusão


---

### [ricardotecpro.github.io](https://ricardotecpro.github.io/)
