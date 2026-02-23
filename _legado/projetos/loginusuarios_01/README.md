# CRUD de Utilizadores com Spring Boot, Thymeleaf e Perfis de Acesso

Este documento detalha a construção de uma aplicação web monolítica completa para registo, autenticação e gestão de utilizadores, com distinção entre perfis de Administrador e Utilizador comum.

## 🏛️ **Arquitetura e Melhorias**

Mantemos a arquitetura monolítica com Spring Boot e Thymeleaf, mas com melhorias cruciais:

1.  **Perfis de Acesso (Roles)**: Implementamos uma distinção clara entre `ADMIN` e `USER`.
      * **ADMIN**: Pode ver e apagar todos os utilizadores.
      * **USER**: Apenas pode ver a lista de utilizadores.
2.  **Segurança Aprimorada**: O Spring Security agora gere as permissões com base nos perfis.
3.  **Interface Moderna com Bootstrap**: Todas as páginas foram redesenhadas com Bootstrap 5 para uma experiência de utilizador profissional e responsiva, incluindo ícones e um layout mais limpo.
4.  **Criação Automática do Admin**: Um utilizador `admin` é criado na primeira execução para garantir o acesso inicial ao sistema.

-----

### **Fase 1: Configuração do Projeto (Spring Initializr)**

Começamos por gerar o projeto com a configuração especificada.

  * **Project**: `Maven`
  * **Language**: `Java`
  * **Spring Boot**: 3.3.x
  * **Group**: `br.com.curso`
  * **Artifact**: `loginusuarios`
  * **Java**: `21`
  * **Dependencies**: `Spring Web`, `Thymeleaf`, `Spring Data JPA`, `H2 Database`, `Spring Security`.

Após gerar e abrir o projeto na sua IDE, vamos ao código.

-----

### **Fase 2: Modelagem dos Dados com Perfis**

#### **1. Enumeração `Role`**

Para gerir os perfis, a melhor prática é usar um `Enum`. Crie o pacote `model` e, dentro dele, o ficheiro `Role.java`.

```java
package br.com.curso.loginusuarios.model;

public enum Role {
    USER,
    ADMIN
}
```

#### **2. Entidade `User` Atualizada**

Agora, vamos adicionar o campo `role` à nossa entidade `User`.

Ficheiro: `src/main/java/br/com/curso/loginusuarios/model/User.java`

```java
package br.com.curso.loginusuarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;
    private String senha;

    @Enumerated(EnumType.STRING) // Diz ao JPA para guardar o nome do enum (ex: "ADMIN")
    @Column(nullable = false)
    private Role role;

    // --- Getters e Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
```

#### **3. Repositório `UserRepository`**

O repositório não precisa de alterações. Ele continua a ser a nossa ponte com a base de dados.

Ficheiro: `src/main/java/br/com/curso/loginusuarios/repository/UserRepository.java`

```java
package br.com.curso.loginusuarios.repository;

import br.com.curso.loginusuarios.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

-----

### **Fase 3: Configuração da Segurança com Perfis**

#### **1. `SecurityConfig` Atualizada**

A configuração de segurança é agora mais específica, definindo que apenas utilizadores com o perfil `ADMIN` podem apagar outros utilizadores.

Ficheiro: `src/main/java/br/com/curso/loginusuarios/config/SecurityConfig.java`

```java
package br.com.curso.loginusuarios.config;

import br.com.curso.loginusuarios.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público a estas páginas e recursos estáticos
                .requestMatchers("/registar", "/login", "/css/**", "/js/**").permitAll()
                // Apenas ADMINS podem aceder a URLs que começam com /usuarios/apagar
                .requestMatchers(HttpMethod.POST, "/usuarios/apagar/**").hasAuthority(Role.ADMIN.name())
                // Qualquer outro pedido exige autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/usuarios", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL para acionar o logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }
}
```

#### **2. Serviço `CustomUserDetailsService`**

O serviço agora precisa de carregar os perfis (`authorities`) do utilizador para que o Spring Security possa tomar decisões.

Ficheiro: `src/main/java/br/com/curso/loginusuarios/service/CustomUserDetailsService.java`

```java
package br.com.curso.loginusuarios.service;

import br.com.curso.loginusuarios.model.User;
import br.com.curso.loginusuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado com o email: " + email));

        // Constrói o UserDetails, incluindo a autoridade (perfil) do utilizador
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getSenha(),
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()))
        );
    }
    
    public void save(User user) {
        user.setSenha(passwordEncoder.encode(user.getSenha()));
        userRepository.save(user);
    }
}
```

-----

### **Fase 4: Controladores Web e Lógica de Negócio**

#### **1. `DataInitializer` (Criador do Utilizador Admin)**

Para garantir o acesso inicial, esta classe cria um utilizador `admin` com a senha `admin123` na primeira vez que a aplicação é executada.

Crie o pacote `util` e, dentro dele, a classe `DataInitializer.java`:

```java
package br.com.curso.loginusuarios.util;

import br.com.curso.loginusuarios.model.Role;
import br.com.curso.loginusuarios.model.User;
import br.com.curso.loginusuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Se não houver nenhum utilizador com o perfil ADMIN, cria um
        if (userRepository.findAll().stream().noneMatch(user -> user.getRole() == Role.ADMIN)) {
            User admin = new User();
            admin.setNome("Administrador");
            admin.setEmail("admin@email.com");
            admin.setSenha(passwordEncoder.encode("admin123")); // Senha inicial
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println(">>> Utilizador ADMIN padrão criado com sucesso!");
        }
    }
}
```

#### **2. `AuthController` (Login e Registo)**

Este controlador agora atribui o perfil `USER` a todos os novos registos.

Ficheiro: `src/main/java/br/com/curso/loginusuarios/controller/AuthController.java`

```java
package br.com.curso.loginusuarios.controller;

import br.com.curso.loginusuarios.model.Role;
import br.com.curso.loginusuarios.model.User;
import br.com.curso.loginusuarios.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private CustomUserDetailsService userService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/registar")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registar";
    }

    @PostMapping("/registar")
    public String processRegistration(@ModelAttribute User user) {
        user.setRole(Role.USER); // Todos os novos utilizadores são do tipo USER
        userService.save(user);
        return "redirect:/login?success";
    }
}
```

#### **3. `UserController` (Gestão de Utilizadores)**

Este controlador foi expandido para incluir a funcionalidade de apagar utilizadores, que só será acessível a um `ADMIN`.

Ficheiro: `src/main/java/br/com/curso/loginusuarios/controller/UserController.java`

```java
package br.com.curso.loginusuarios.controller;

import br.com.curso.loginusuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/usuarios")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "usuarios";
    }

    // Endpoint para apagar um utilizador, acessível via POST para segurança
    @PostMapping("/apagar/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        // Adicionar verificação para não se auto-apagar, se necessário
        userRepository.deleteById(id);
        return "redirect:/usuarios?deleted";
    }
}
```

-----

### **Fase 5: Interface com Thymeleaf e Bootstrap 5**

Todas as páginas foram redesenhadas para serem mais modernas e informativas.

#### **1. Página de Login (`login.html`)**

Um design mais limpo e com feedback claro para o utilizador.

Ficheiro: `src/main/resources/templates/login.html`

```html
<!DOCTYPE html>
<html lang="pt" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Gestão de Utilizadores</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">
<div class="container">
    <div class="row justify-content-center align-items-center" style="height: 100vh;">
        <div class="col-md-5 col-lg-4">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4">
                    <h3 class="card-title text-center mb-4">
                        <i class="bi bi-box-arrow-in-right me-2"></i>Aceder ao Sistema
                    </h3>
                    
                    <div th:if="${param.error}" class="alert alert-danger" role="alert">
                        Email ou senha inválidos.
                    </div>
                    <div th:if="${param.logout}" class="alert alert-info" role="alert">
                        Sessão terminada com sucesso.
                    </div>
                    <div th:if="${param.success}" class="alert alert-success" role="alert">
                        Registo efetuado! Faça o login para continuar.
                    </div>

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
                        <small class="text-muted">Não tem uma conta? <a th:href="@{/registar}">Registe-se aqui</a></small>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
```

#### **2. Página de Registo (`registar.html`)**

Formulário de registo simples e direto.

Ficheiro: `src/main/resources/templates/registar.html`

```html
<!DOCTYPE html>
<html lang="pt" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registar - Gestão de Utilizadores</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body class="bg-light">
<div class="container">
    <div class="row justify-content-center align-items-center" style="height: 100vh;">
        <div class="col-md-5 col-lg-4">
            <div class="card shadow-sm border-0">
                <div class="card-body p-4">
                    <h3 class="card-title text-center mb-4">
                       <i class="bi bi-person-plus-fill me-2"></i>Criar Nova Conta
                    </h3>
                    <form th:action="@{/registar}" th:object="${user}" method="post">
                        <div class="mb-3">
                            <label for="nome" class="form-label">Nome Completo</label>
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
                         <small class="text-muted">Já tem uma conta? <a th:href="@{/login}">Faça o login</a></small>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
```

#### **3. Página de Utilizadores (`usuarios.html`)**

Esta página agora utiliza o dialeto de segurança do Thymeleaf (`xmlns:sec`) para exibir condicionalmente o botão de apagar. Apenas utilizadores com a autoridade `ADMIN` verão este botão.

Ficheiro: `src/main/resources/templates/usuarios.html`

```html
<!DOCTYPE html>
<html lang="pt" xmlns:th="http://www.thymeleaf.org" xmlns:sec="http://www.thymeleaf.org/extras/spring-security">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Painel de Utilizadores</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">
            <i class="bi bi-shield-lock-fill me-2"></i>Painel de Gestão
        </a>
        <form class="d-flex" th:action="@{/logout}" method="post">
            <button class="btn btn-outline-light" type="submit">
                <i class="bi bi-box-arrow-right me-2"></i>Sair
            </button>
        </form>
    </div>
</nav>

<div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h1 class="h3">Utilizadores Registados</h1>
    </div>

    <div th:if="${param.deleted}" class="alert alert-warning" role="alert">
        Utilizador apagado com sucesso.
    </div>
    
    <div class="card border-0 shadow-sm">
        <div class="card-body">
             <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead class="table-light">
                        <tr>
                            <th>ID</th>
                            <th>Nome</th>
                            <th>Email</th>
                            <th>Perfil</th>
                            <th sec:authorize="hasAuthority('ADMIN')" class="text-end">Ações</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="user : ${users}">
                            <td th:text="${user.id}"></td>
                            <td th:text="${user.nome}"></td>
                            <td th:text="${user.email}"></td>
                            <td>
                                <span th:if="${user.role.name() == 'ADMIN'}" class="badge bg-success" th:text="${user.role.name()}"></span>
                                <span th:if="${user.role.name() == 'USER'}" class="badge bg-secondary" th:text="${user.role.name()}"></span>
                            </td>
                            <!-- Este formulário só será visível para utilizadores com o perfil ADMIN -->
                            <td sec:authorize="hasAuthority('ADMIN')" class="text-end">
                                <form th:action="@{/usuarios/apagar/{id}(id=${user.id})}" method="post" onsubmit="return confirm('Tem a certeza que deseja apagar este utilizador?');">
                                    <button type="submit" class="btn btn-sm btn-outline-danger" th:disabled="${#authentication.name == user.email}">
                                        <i class="bi bi-trash-fill"></i> Apagar
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>
</body>
</html>
```

-----

### **Fase 6: Executar e Testar**

1.  **Execute a Aplicação**: Corra a classe `LoginusuariosApplication.java`.
2.  **Login como Admin**: Aceda a [http://localhost:8080/login](https://www.google.com/search?q=http://localhost:8080/login). Use as credenciais `admin@email.com` e `admin123`.
3.  **Teste as Permissões de Admin**: Na página de utilizadores, verá a coluna "Ações" com o botão "Apagar".
4.  **Registe um Novo Utilizador**: Faça logout e aceda a [http://localhost:8080/registar](https://www.google.com/search?q=http://localhost:8080/registar). Crie uma conta normal.
5.  **Login como User**: Faça login com a nova conta que criou.
6.  **Teste as Permissões de User**: Na página de utilizadores, a coluna "Ações" e o botão "Apagar" não estarão visíveis.


---
# Para acesso ao H2

## SecurityConfig

```java
package br.com.curso.loginusuarios.config;

import br.com.curso.loginusuarios.model.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Configurações específicas para o H2 Console
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso público a estas páginas, recursos estáticos e ao H2 Console
                .requestMatchers("/registar", "/login", "/css/**", "/js/**", "/h2-console/**").permitAll()
                // Apenas ADMINS podem aceder a URLs que começam com /usuarios/apagar
                .requestMatchers(HttpMethod.POST, "/usuarios/apagar/**").hasAuthority(Role.ADMIN.name())
                // Qualquer outro pedido exige autenticação
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                .defaultSuccessUrl("/usuarios", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout") // URL para acionar o logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );
        return http.build();
    }
}

```

---

