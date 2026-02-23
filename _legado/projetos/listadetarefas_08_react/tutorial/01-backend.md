# 🎓 Módulo 1: Backend com Spring Boot

**Objetivo**: Criar uma API RESTful capaz de criar, ler, atualizar e deletar tarefas (CRUD).

---

## 1. O que é uma API REST?
Imagine um garçom (API). Você (Frontend) pede um prato (Dados) olhando o cardápio (Endpoints). O garçom leva o pedido para a cozinha (Backend/Banco de Dados) e traz a comida.
- **GET**: Buscar dados.
- **POST**: Criar dados.
- **PUT**: Atualizar dados.
- **DELETE**: Remover dados.

---

## 2. Criando o Projeto

1. Acesse [start.spring.io](https://start.spring.io/).
2. Configure:
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: 3.x.x (Stable)
   - **Dependencies**: Spring Web, Spring Data JPA, H2 Database, Lombok.
3. Gere o projeto, baixe e extraia na pasta `backend`.

---

## 3. Mão na Massa: O Modelo (Model)

Crie o arquivo `src/main/java/br/com/liston/api/model/Tarefa.java`.

```java
@Entity
@Data // Lombok cria Getters/Setters automaticamente
@NoArgsConstructor
public class Tarefa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;
    private boolean concluida;
}
```

*Explicação: `@Entity` diz que essa classe vira uma tabela no banco de dados.*

---

## 4. O Repositório (Repository)

Crie `src/main/java/br/com/liston/api/repository/TarefaRepository.java`.

```java
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
}
```

*Explicação: Ao estender `JpaRepository`, ganhamos métodos prontos como `save()`, `findAll()` e `deleteById()` sem escrever SQL.*

---

## 5. O Controlador (Controller)

Crie `src/main/java/br/com/liston/api/controller/TarefaController.java`.

```java
@RestController
@RequestMapping("/api/tarefas")
public class TarefaController {

    @Autowired
    private TarefaRepository repository;

    @GetMapping
    public List<Tarefa> listar() {
        return repository.findAll();
    }

    @PostMapping
    public Tarefa criar(@RequestBody Tarefa tarefa) {
        return repository.save(tarefa);
    }
    
    // ... Implementar PUT e DELETE como desafio!
}
```

---

## 🛑 Pare e Teste

1. Rode o projeto: `./mvnw spring-boot:run`.
2. Abra o navegador em `http://localhost:8080/api/tarefas`.
3. Você deve ver `[]` (lista vazia).

---

## 🏆 Desafio
Implemente os métodos `atualizar` (@PutMapping) e `deletar` (@DeleteMapping) no Controller.
