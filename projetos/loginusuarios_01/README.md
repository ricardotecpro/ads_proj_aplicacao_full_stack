# CRUD - Login de Usuários

---

## 🏛️ Arquitetura

A aplicação seguirá o padrão **Monolítico Servidor-Cliente**, onde o Spring Boot atua como o centro de tudo:

1. **Modelo (Model)**: Classes Java que representam os dados (ex: a entidade `User`).
2. **Visão (View)**: Ficheiros HTML com Thymeleaf que renderizam a interface para o utilizador.
3. **Controlador (Controller)**: Classes Java que recebem os pedidos do utilizador, processam a lógica de negócio e devolvem a visão apropriada.
4. **Segurança (Security)**: O Spring Security irá proteger as páginas e gerir o fluxo de autenticação.

---

## 🚀 Fase 1: Criação e Configuração do Projeto

### 1. Gerar o Projeto no Spring Initializr

Aceda a [start.spring.io](https://start.spring.io) e preencha:

- **Project**: `Maven`
- **Language**: `Java`
- **Spring Boot**: Última versão (ex: 3.3.1)
- **Project Metadata**:
  - **Group**: `br.com.curso`
  - **Artifact**: `loginusuarios`
  - **Java**: `21`
- **Dependencies**:
  - `Spring Web`
  - `Thymeleaf`
  - `Spring Data JPA`
  - `H2 Database`
  - `Spring Security`

### 2. Configurar a Base de Dados (`application.properties`)

```properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
````

---

## 🧩 Fase 2: Modelagem dos Dados

### Entidade `User`

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String email;
    private String senha;
    // getters e setters
}
```

### Repositório `UserRepository`

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

---

## 🔐 Fase 3: Configuração da Segurança

### Classe `SecurityConfig`

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/registar", "/css/**").permitAll()
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .defaultSuccessUrl("/usuarios", true))
            .logout(logout -> logout.logoutSuccessUrl("/login?logout"));
        return http.build();
    }
}
```

### Serviço `CustomUserDetailsService`

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado: " + email));
        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getSenha())
            .authorities(new ArrayList<>())
            .build();
    }

    public void save(User user) {
        user.setSenha(passwordEncoder.encode(user.getSenha()));
        userRepository.save(user);
    }
}
```

---

## 🖥️ Fase 4: Controladores

### `AuthController.java`

```java
@Controller
public class AuthController {
    @Autowired private CustomUserDetailsService userService;

    @GetMapping("/login")
    public String showLoginPage() { return "login"; }

    @GetMapping("/registar")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registar";
    }

    @PostMapping("/registar")
    public String processRegistration(@ModelAttribute User user) {
        userService.save(user);
        return "redirect:/login?success";
    }
}
```

### `UserController.java`

```java
@Controller
public class UserController {
    @Autowired private UserRepository userRepository;

    @GetMapping("/usuarios")
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "usuarios";
    }
}
```

---

## 🎨 Fase 5: Páginas Thymeleaf

### `login.html`

```html
<form th:action="@{/login}" method="post">
  <input type="email" name="username" required>
  <input type="password" name="password" required>
  <button type="submit">Entrar</button>
</form>
<a th:href="@{/registar}">Registe-se aqui</a>
```

### `registar.html`

```html
<form th:action="@{/registar}" th:object="${user}" method="post">
  <input type="text" th:field="*{nome}" required>
  <input type="email" th:field="*{email}" required>
  <input type="password" th:field="*{senha}" required>
  <button type="submit">Registar</button>
</form>
<a th:href="@{/login}">Já tem conta? Login</a>
```

### `usuarios.html`

```html
<table>
  <tr th:each="user : ${users}">
    <td th:text="${user.id}"></td>
    <td th:text="${user.nome}"></td>
    <td th:text="${user.email}"></td>
  </tr>
</table>
<form th:action="@{/logout}" method="post">
  <button type="submit">Sair</button>
</form>
```

---

## 🏃 Fase 6: Executar e Testar

1. Execute `LoginusuariosApplication.java`.
2. Registe em [http://localhost:8080/registar](http://localhost:8080/registar).
3. Faça login em [http://localhost:8080/login](http://localhost:8080/login).
4. Veja utilizadores em [http://localhost:8080/usuarios](http://localhost:8080/usuarios).
5. (Opcional) Explore [http://localhost:8080/h2-console](http://localhost:8080/h2-console).

---


**Atualizar as páginas Thymeleaf** (`login.html`, `registar.html` e `usuarios.html`) para ficarem prontas com **Bootstrap 5**, mantendo a estrutura bonita e responsiva.


````markdown
## 🎨 Fase 5: Páginas Thymeleaf com Bootstrap

### `login.html`

```html
<!DOCTYPE html>
<html lang="pt" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title>Login</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container">
  <div class="row justify-content-center align-items-center" style="height:100vh;">
    <div class="col-md-5">
      <div class="card shadow">
        <div class="card-body">
          <h3 class="card-title text-center mb-4">Aceder ao Sistema</h3>

          <!-- Mensagens -->
          <div th:if="${param.error}" class="alert alert-danger">Email ou senha inválidos.</div>
          <div th:if="${param.logout}" class="alert alert-info">Sessão terminada com sucesso.</div>
          <div th:if="${param.success}" class="alert alert-success">Registo efetuado com sucesso!</div>

          <form th:action="@{/login}" method="post">
            <div class="mb-3">
              <label for="username" class="form-label">Email</label>
              <input type="email" id="username" name="username" class="form-control" required autofocus>
            </div>
            <div class="mb-3">
              <label for="password" class="form-label">Senha</label>
              <input type="password" id="password" name="password" class="form-control" required>
            </div>
            <div class="d-grid">
              <button type="submit" class="btn btn-primary">Entrar</button>
            </div>
          </form>
          <div class="text-center mt-3">
            <a th:href="@{/registar}">Não tem conta? Registe-se aqui</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
````

---

### `registar.html`

```html
<!DOCTYPE html>
<html lang="pt" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title>Registar</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container">
  <div class="row justify-content-center align-items-center" style="height:100vh;">
    <div class="col-md-5">
      <div class="card shadow">
        <div class="card-body">
          <h3 class="card-title text-center mb-4">Criar Conta</h3>

          <form th:action="@{/registar}" th:object="${user}" method="post">
            <div class="mb-3">
              <label for="nome" class="form-label">Nome</label>
              <input type="text" id="nome" th:field="*{nome}" class="form-control" required>
            </div>
            <div class="mb-3">
              <label for="email" class="form-label">Email</label>
              <input type="email" id="email" th:field="*{email}" class="form-control" required>
            </div>
            <div class="mb-3">
              <label for="senha" class="form-label">Senha</label>
              <input type="password" id="senha" th:field="*{senha}" class="form-control" required>
            </div>
            <div class="d-grid">
              <button type="submit" class="btn btn-success">Registar</button>
            </div>
          </form>

          <div class="text-center mt-3">
            <a th:href="@{/login}">Já tem conta? Faça login</a>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>
```

---

### `usuarios.html`

```html
<!DOCTYPE html>
<html lang="pt" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8">
  <title>Lista de Utilizadores</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <div class="container-fluid">
    <a class="navbar-brand" href="#">Painel</a>
    <form class="d-flex" th:action="@{/logout}" method="post">
      <button class="btn btn-outline-light" type="submit">Sair</button>
    </form>
  </div>
</nav>

<div class="container mt-4">
  <h1 class="mb-4">Utilizadores Registados</h1>
  <table class="table table-striped table-hover">
    <thead class="table-dark">
      <tr>
        <th>ID</th>
        <th>Nome</th>
        <th>Email</th>
      </tr>
    </thead>
    <tbody>
      <tr th:each="user : ${users}">
        <td th:text="${user.id}"></td>
        <td th:text="${user.nome}"></td>
        <td th:text="${user.email}"></td>
      </tr>
    </tbody>
  </table>
</div>
</body>
</html>
```

```

👉 Agora todos os HTMLs estão com **Bootstrap 5 integrado**, com **layout responsivo, cartões, alertas e tabelas estilizadas**.  
