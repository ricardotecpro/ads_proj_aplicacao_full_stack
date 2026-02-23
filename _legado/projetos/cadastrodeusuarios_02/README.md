Etapa 1: Criando o Projeto
Vamos começar do zero usando o Spring Initializr, a ferramenta oficial para criar projetos Spring.

Configurações do Projeto:

Project: Maven

Language: Java

Spring Boot: 3.2.x ou superior

Project Metadata:

Group: br.com.cadastro

Artifact: usuarios

Package name: br.com.cadastro.usuarios

Packaging: Jar

Java: 21

Dependências Essenciais:

Spring Web: Para criar a aplicação web.

Spring Data JPA: Para salvar e buscar dados no banco.

Thymeleaf: Para construir nossas páginas HTML.

H2 Database: Nosso banco de dados em memória, que funciona localmente.

Spring Security: Para adicionar uma tela de login e proteger a aplicação.

Spring Boot DevTools: Para que o projeto reinicie automaticamente quando você alterar o código.

Clique em "GENERATE", baixe o projeto, descompacte e abra na sua IDE.

Etapa 2: Estrutura e Modelo de Dados
Uma boa organização ajuda muito. Dentro de src/main/java/br/com/cadastro/usuarios, vamos criar a seguinte estrutura de pacotes:

br.com.cadastro.usuarios
├── config/       # Configurações de segurança
├── controller/   # Controladores que lidam com as requisições web
├── entity/       # Nossas tabelas do banco de dados (Entidades)
├── repository/   # Para acesso aos dados
└── service/      # Regras de negócio (neste caso, o serviço de autenticação)

2.1. Criando as Entidades (entity)
Vamos definir as tabelas User (usuário) e Role (perfil, ex: ADMIN).

Arquivo: src/main/java/br/com/cadastro/usuarios/entity/Role.java

package br.com.cadastro.usuarios.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles")
public class Role implements GrantedAuthority {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Override
    public String getAuthority() {
        return name;
    }
    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

Arquivo: src/main/java/br/com/cadastro/usuarios/entity/User.java

package br.com.cadastro.usuarios.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails { // UserDetails é do Spring Security
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    // Métodos exigidos pelo UserDetails
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return this.roles; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
}

2.2. Criando o Repositório (repository)
Esta interface nos dará os métodos para salvar, buscar, deletar, etc., sem precisarmos escrever SQL.

Arquivo: src/main/java/br/com/cadastro/usuarios/repository/UserRepository.java

package br.com.cadastro.usuarios.repository;

import br.com.cadastro.usuarios.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

Etapa 3: Configurando o Banco de Dados H2
Simplificaremos o arquivo application.properties para usar apenas o H2.

Arquivo: src/main/resources/application.properties

# URL de conexão para o banco H2 em um arquivo
spring.datasource.url=jdbc:h2:file:./data/usuariosdb
# Driver do H2
spring.datasource.driverClassName=org.h2.Driver
# Usuário e senha do banco
spring.datasource.username=sa
spring.datasource.password=password
# Dialeto do Hibernate para H2
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# Habilita o console do H2 para visualizarmos o banco no navegador
spring.h2.console.enabled=true
# Caminho para acessar o console (ex: http://localhost:8080/h2-console)
spring.h2.console.path=/h2-console

# Gerencia a criação das tabelas automaticamente
spring.jpa.hibernate.ddl-auto=update

Etapa 4: Implementando a Segurança (Login Simples)
Vamos configurar uma tela de login tradicional.

4.1. Configuração de Segurança
Arquivo: src/main/java/br/com/cadastro/usuarios/config/SecurityConfig.java

package br.com.cadastro.usuarios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permite acesso livre aos arquivos de CSS e JS
                .requestMatchers("/css/**", "/js/**").permitAll()
                // Qualquer outra requisição precisa de autenticação
                .anyRequest().authenticated()
            )
            // Configura o formulário de login
            .formLogin(form -> form
                .loginPage("/login").permitAll() // Diz qual a URL da nossa página de login
                .defaultSuccessUrl("/", true) // Para onde ir após o login com sucesso
            )
            // Configura o logout
            .logout(logout -> logout.logoutSuccessUrl("/login"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Codificador de senhas. Essencial para a segurança.
        return new BCryptPasswordEncoder();
    }
}

4.2. Serviço de Autenticação
Este serviço é responsável por buscar o usuário no banco de dados para o Spring Security.

Arquivo: src/main/java/br/com/cadastro/usuarios/service/AuthorizationService.java

package br.com.cadastro.usuarios.service;

import br.com.cadastro.usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    }
}

Etapa 5: Criando os Controllers e as Páginas
Agora vamos criar as telas e os controllers que as servem.

5.1. Controller para Login e Páginas Principais
Arquivo: src/main/java/br/com/cadastro/usuarios/controller/UserController.java

package br.com.cadastro.usuarios.controller;

import br.com.cadastro.usuarios.entity.User;
import br.com.cadastro.usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("userForm", new User()); // Objeto para o formulário
        return "index";
    }

    @PostMapping("/save")
    public String saveUser(User user) {
        // Codifica a senha antes de salvar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/"; // Redireciona para a página principal
    }

    @GetMapping("/edit/{id}")
    public String editUser(@PathVariable("id") Long id, Model model) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return "redirect:/"; // Se não achar, volta para o início
        }
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("userForm", userOpt.get());
        return "index"; // Reutiliza a mesma página, mas com o formulário preenchido
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

5.2. Páginas HTML com Thymeleaf
Crie a pasta templates dentro de src/main/resources.

Página de Login: src/main/resources/templates/login.html

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
    <link href="[https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css](https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css)" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container">
    <div class="row justify-content-center align-items-center vh-100">
        <div class="col-md-4">
            <div class="card shadow-sm">
                <div class="card-body">
                    <h3 class="card-title text-center mb-4">Login</h3>
                    <form th:action="@{/login}" method="post">
                        <div th:if="${param.error}" class="alert alert-danger">
                            Usuário ou senha inválidos.
                        </div>
                        <div class="mb-3">
                            <label for="username" class="form-label">Usuário</label>
                            <input type="text" id="username" name="username" class="form-control" required autofocus>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Senha</label>
                            <input type="password" id="password" name="password" class="form-control" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Entrar</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>

Página Principal: src/main/resources/templates/index.html

<!DOCTYPE html>
<html lang="pt-br" xmlns:th="[http://www.thymeleaf.org](http://www.thymeleaf.org)">
<head>
    <meta charset="UTF-8">
    <title>Cadastro de Usuários</title>
    <link href="[https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css](https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css)" rel="stylesheet">
    <link href="[https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css](https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css)" rel="stylesheet">
    <link th:href="@{/css/style.css}" rel="stylesheet">
</head>
<body>
    <nav class="navbar navbar-expand-lg bg-body-tertiary shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand" href="#"><i class="bi bi-people-fill"></i> Cadastro de Usuários</a>
            <form th:action="@{/logout}" method="post" class="d-flex">
                <button class="btn btn-outline-danger" type="submit">Sair</button>
            </form>
        </div>
    </nav>
    <main class="container mt-4">
        <div class="row">
            <!-- Formulário de Cadastro/Edição -->
            <div class="col-md-4">
                <h4 th:if="${userForm.id == null}">Novo Usuário</h4>
                <h4 th:if="${userForm.id != null}">Editar Usuário</h4>
                <form action="#" th:action="@{/save}" th:object="${userForm}" method="post">
                    <input type="hidden" th:field="*{id}" />
                    <div class="mb-3">
                        <label class="form-label">Username</label>
                        <input type="text" th:field="*{username}" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Email</label>
                        <input type="email" th:field="*{email}" class="form-control" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Senha</label>
                        <input type="password" th:field="*{password}" class="form-control" placeholder="Deixe em branco para não alterar">
                    </div>
                    <button type="submit" class="btn btn-success"><i class="bi bi-check-circle-fill"></i> Salvar</button>
                    <a href="/" class="btn btn-secondary"><i class="bi bi-x-circle-fill"></i> Cancelar</a>
                </form>
            </div>

            <!-- Tabela de Usuários -->
            <div class="col-md-8">
                <h4>Usuários Cadastrados</h4>
                <table class="table table-hover">
                    <thead>
                    <tr>
                        <th>ID</th><th>Username</th><th>Email</th><th>Ações</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr th:each="user : ${users}">
                        <td th:text="${user.id}"></td>
                        <td th:text="${user.username}"></td>
                        <td th:text="${user.email}"></td>
                        <td>
                            <a th:href="@{/edit/{id}(id=${user.id})}" class="btn btn-sm btn-warning"><i class="bi bi-pencil-fill"></i></a>
                            <a th:href="@{/delete/{id}(id=${user.id})}" class="btn btn-sm btn-danger"><i class="bi bi-trash-fill"></i></a>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </main>
</body>
</html>

Etapa 6: Criando um Usuário Administrador Inicial
Para conseguirmos fazer login pela primeira vez, vamos criar um bean que insere um usuário "admin" quando a aplicação inicia.

Crie este arquivo: src/main/java/br/com/cadastro/usuarios/config/DataInitializer.java

package br.com.cadastro.usuarios.config;

import br.com.cadastro.usuarios.entity.User;
import br.com.cadastro.usuarios.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Cria o usuário admin apenas se ele não existir
            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setEmail("admin@email.com");
                // Aqui você poderia criar e associar roles (perfis)
                userRepository.save(admin);
                System.out.println(">>> Usuário 'admin' criado com senha 'admin'");
            }
        };
    }
}

Etapa 7: Rodando o Projeto!
Agora é a parte fácil!

Execute a Classe Principal: Encontre a classe UsuariosApplication.java e clique com o botão direito -> "Run" (ou use o atalho da sua IDE).

Acesse a Aplicação: Abra seu navegador e vá para http://localhost:8080.

Você será redirecionado para a tela de login.

Use o usuário admin e senha admin para entrar.

Acesse o Console do Banco: Para ver as tabelas e dados que estão sendo criados, acesse http://localhost:8080/h2-console.

No campo JDBC URL, coloque jdbc:h2:file:./data/usuariosdb.

Use o usuário sa e a senha password.

Clique em "Connect".

---

-----

### **1. Melhorando a Página Principal (CRUD)**

Vamos transformar sua página `index.html` usando `cards`, um layout de `grid` e botões estilizados. Isso organiza melhor a informação e melhora a experiência do usuário.

**Substitua o conteúdo do seu `index.html` por este:**

```html
<!DOCTYPE html>
<html lang="pt-br" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Cadastro de Usuários</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Navbar Superior -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand" href="/">
                <i class="bi bi-people-fill"></i>
                Cadastro de Usuários
            </a>
            <div class="d-flex">
                 <a href="/relatorios" class="btn btn-outline-info me-2">
                    <i class="bi bi-file-earmark-bar-graph-fill"></i> Relatórios
                </a>
                <form th:action="@{/logout}" method="post">
                    <button class="btn btn-outline-danger" type="submit">
                        <i class="bi bi-box-arrow-right"></i> Sair
                    </button>
                </form>
            </div>
        </div>
    </nav>

    <main class="container mt-4">
        <div class="row g-4">
            <!-- Coluna do Formulário -->
            <div class="col-md-4">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white">
                        <h5 th:if="${userForm.id == null}" class="mb-0"><i class="bi bi-person-plus-fill"></i> Novo Usuário</h5>
                        <h5 th:if="${userForm.id != null}" class="mb-0"><i class="bi bi-pencil-square"></i> Editar Usuário</h5>
                    </div>
                    <div class="card-body">
                        <form action="#" th:action="@{/save}" th:object="${userForm}" method="post">
                            <input type="hidden" th:field="*{id}" />
                            <div class="mb-3">
                                <label for="username" class="form-label">Username</label>
                                <input id="username" type="text" th:field="*{username}" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input id="email" type="email" th:field="*{email}" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label">Senha</label>
                                <input id="password" type="password" th:field="*{password}" class="form-control" placeholder="Deixe em branco para não alterar">
                            </div>
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="/" class="btn btn-secondary me-md-2"><i class="bi bi-x-circle"></i> Cancelar</a>
                                <button type="submit" class="btn btn-success"><i class="bi bi-check-circle-fill"></i> Salvar</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Coluna da Tabela -->
            <div class="col-md-8">
                <div class="card shadow-sm">
                    <div class="card-header">
                       <h5 class="mb-0"><i class="bi bi-list-ul"></i> Usuários Cadastrados</h5>
                    </div>
                    <div class="card-body">
                         <div class="table-responsive">
                            <table class="table table-hover table-striped align-middle">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Username</th>
                                        <th>Email</th>
                                        <th class="text-center">Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr th:each="user : ${users}">
                                        <td th:text="${user.id}"></td>
                                        <td th:text="${user.username}"></td>
                                        <td th:text="${user.email}"></td>
                                        <td class="text-center">
                                            <!-- Botão Editar -->
                                            <a th:href="@{/edit/{id}(id=${user.id})}" class="btn btn-sm btn-warning" title="Editar">
                                                <i class="bi bi-pencil-fill"></i>
                                            </a>
                                            <!-- Botão Excluir -->
                                            <a th:href="@{/delete/{id}(id=${user.id})}" class="btn btn-sm btn-danger" title="Excluir">
                                                <i class="bi bi-trash-fill"></i>
                                            </a>
                                             <!-- Botão para Gerenciar Perfil -->
                                            <button class="btn btn-sm btn-info" title="Alterar Perfil">
                                                 <i class="bi bi-shield-lock-fill"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
    
    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
```

**O que mudou:**

  * **Layout:** Usei `<div class="container">`, `<div class="row">` e `<div class="col-md-...">` para criar uma grade responsiva.
  * **Cards:** O formulário e a tabela agora estão dentro de `cards`, que criam blocos visuais bem definidos.
  * **Navbar:** Adicionei uma barra de navegação escura no topo com um link para relatórios e o botão de sair.
  * **Botões e Ícones:** Todos os botões estão estilizados com cores e ícones do Bootstrap, tornando as ações (salvar, editar, excluir) muito mais claras.
  * **Tabela:** A tabela agora tem `table-hover` (destaca a linha ao passar o mouse) e `table-striped` (linhas com cores alternadas), melhorando a legibilidade.

-----

### **2. Adicionando a Página de Relatórios**

Crie um novo arquivo HTML chamado `relatorios.html` dentro da pasta `templates`. Ele terá filtros e uma tabela para exibir os resultados.

**Código para `relatorios.html`:**

```html
<!DOCTYPE html>
<html lang="pt-br" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Relatório de Usuários</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Navbar (pode ser a mesma da index) -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand" href="/">
                <i class="bi bi-people-fill"></i>
                Cadastro de Usuários
            </a>
             <div class="d-flex">
                 <a href="/relatorios" class="btn btn-info me-2 active"> <!-- Botão ativo -->
                    <i class="bi bi-file-earmark-bar-graph-fill"></i> Relatórios
                </a>
                <form th:action="@{/logout}" method="post">
                    <button class="btn btn-outline-danger" type="submit">
                        <i class="bi bi-box-arrow-right"></i> Sair
                    </button>
                </form>
            </div>
        </div>
    </nav>

    <main class="container mt-4">
        <div class="card shadow-sm">
            <div class="card-header bg-info text-white">
                <h5 class="mb-0"><i class="bi bi-filter-circle-fill"></i> Filtros do Relatório</h5>
            </div>
            <div class="card-body">
                <form class="row g-3 align-items-end">
                    <div class="col-md-4">
                        <label for="nome" class="form-label">Nome de Usuário</label>
                        <input type="text" class="form-control" id="nome" placeholder="Filtrar por nome...">
                    </div>
                    <div class="col-md-4">
                        <label for="perfil" class="form-label">Perfil</label>
                        <select id="perfil" class="form-select">
                            <option selected>Todos</option>
                            <option value="ADMIN">Administrador</option>
                            <option value="USER">Usuário</option>
                        </select>
                    </div>
                    <div class="col-md-2">
                        <button type="submit" class="btn btn-primary w-100">
                            <i class="bi bi-search"></i> Filtrar
                        </button>
                    </div>
                     <div class="col-md-2">
                        <button type="button" class="btn btn-secondary w-100">
                            <i class="bi bi-printer-fill"></i> Exportar
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <div class="card shadow-sm mt-4">
             <div class="card-header">
                <h5 class="mb-0"><i class="bi bi-table"></i> Resultado</h5>
             </div>
             <div class="card-body">
                 <div class="table-responsive">
                     <table class="table table-striped table-hover">
                         <thead>
                             <tr>
                                 <th>ID</th>
                                 <th>Username</th>
                                 <th>Email</th>
                                 <th>Perfil</th>
                                 <th>Data de Cadastro</th>
                             </tr>
                         </thead>
                         <tbody>
                             <!-- Os dados filtrados viriam aqui via Thymeleaf -->
                             <tr>
                                 <td>1</td>
                                 <td>admin</td>
                                 <td>admin@email.com</td>
                                 <td><span class="badge bg-danger">Administrador</span></td>
                                 <td>01/01/2024</td>
                             </tr>
                             <tr>
                                 <td>2</td>
                                 <td>usuario1</td>
                                 <td>user@email.com</td>
                                 <td><span class="badge bg-primary">Usuário</span></td>
                                 <td>15/02/2024</td>
                             </tr>
                         </tbody>
                     </table>
                 </div>
             </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
```

Você precisará criar o endpoint `@GetMapping("/relatorios")` no seu `Controller` para exibir esta página.

-----

### **3. Gerenciando Perfis (Admin/Usuário)**

Para alterar o perfil, a melhor abordagem é usar um **Modal** (uma janela pop-up do Bootstrap). Ele abre ao clicar no botão azul de "escudo" sem sair da página.

Adicione este código de Modal no final do seu arquivo `index.html`, antes de fechar a tag `</body>`.

**Código do Modal para `index.html`:**

```html
<!-- Modal para Gerenciar Perfil -->
<div class="modal fade" id="perfilModal" tabindex="-1" aria-labelledby="perfilModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="perfilModalLabel"><i class="bi bi-shield-lock-fill"></i> Alterar Perfil do Usuário</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form th:action="@{/user/change-role}" method="post">
                <div class="modal-body">
                    <p>Você está alterando o perfil do usuário: <strong id="usernameModal"></strong></p>
                    <input type="hidden" id="userIdModal" name="userId" />
                    
                    <div class="mb-3">
                        <label for="role" class="form-label">Selecione o novo perfil:</label>
                        <select id="role" name="role" class="form-select">
                            <option value="ROLE_ADMIN">Administrador</option>
                            <option value="ROLE_USER">Usuário</option>
                        </select>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Salvar Alterações</button>
                </div>
            </form>
        </div>
    </div>
</div>
```

**Para fazer o modal funcionar:**

1.  Altere o botão de "escudo" na tabela do `index.html` para que ele abra o modal:

    ```html
    <!-- Botão para Gerenciar Perfil (versão atualizada) -->
    <button type="button" class="btn btn-sm btn-info" title="Alterar Perfil"
            data-bs-toggle="modal" data-bs-target="#perfilModal"
            th:attr="data-user-id=${user.id}, data-user-name=${user.username}">
        <i class="bi bi-shield-lock-fill"></i>
    </button>
    ```

2.  Adicione um pequeno script JavaScript no final do `index.html` para passar os dados do usuário para o modal:

    ```javascript
    <script>
        const perfilModal = document.getElementById('perfilModal');
        perfilModal.addEventListener('show.bs.modal', event => {
            const button = event.relatedTarget;
            const userId = button.getAttribute('data-user-id');
            const userName = button.getAttribute('data-user-name');

            const modalBodyUsername = perfilModal.querySelector('#usernameModal');
            const modalInputUserId = perfilModal.querySelector('#userIdModal');

            modalBodyUsername.textContent = userName;
            modalInputUserId.value = userId;
        });
    </script>
    ```

Agora, ao clicar no botão de escudo, um pop-up aparecerá permitindo que você altere o perfil daquele usuário específico. Você precisará criar o endpoint `@PostMapping("/user/change-role")` no seu controller para processar essa mudança.


---
<!DOCTYPE html>
<html lang="pt-br" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro de Usuários</title>
    
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" xintegrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css" rel="stylesheet">

    <!-- Estilo para hover dos botões (opcional) -->
    <style>
        .btn-hover:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            transition: all 0.2s ease-in-out;
        }
    </style>
</head>
<body class="bg-light">

    <!-- Navbar Superior -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand" href="/">
                <i class="bi bi-people-fill me-2"></i>
                Cadastro de Usuários
            </a>
            <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav ms-auto">
                    <li class="nav-item">
                        <a href="/relatorios" class="btn btn-outline-info me-2 btn-hover">
                            <i class="bi bi-file-earmark-bar-graph-fill"></i> Relatórios
                        </a>
                    </li>
                    <li class="nav-item">
                        <form th:action="@{/logout}" method="post">
                            <button class="btn btn-outline-danger btn-hover" type="submit">
                                <i class="bi bi-box-arrow-right"></i> Sair
                            </button>
                        </form>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Conteúdo Principal -->
    <main class="container mt-4">
        <div class="row g-4">
            
            <!-- Coluna do Formulário -->
            <div class="col-lg-4 col-md-12">
                <div class="card shadow-sm h-100">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0" th:if="${userForm.id == null}"><i class="bi bi-person-plus-fill me-2"></i> Novo Usuário</h5>
                        <h5 class="mb-0" th:if="${userForm.id != null}"><i class="bi bi-pencil-square me-2"></i> Editar Usuário</h5>
                    </div>
                    <div class="card-body">
                        <form action="#" th:action="@{/save}" th:object="${userForm}" method="post">
                            <input type="hidden" th:field="*{id}" />
                            <div class="mb-3">
                                <label for="username" class="form-label">Username</label>
                                <input id="username" type="text" th:field="*{username}" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label for="email" class="form-label">Email</label>
                                <input id="email" type="email" th:field="*{email}" class="form-control" required>
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label">Senha</label>
                                <input id="password" type="password" th:field="*{password}" class="form-control" placeholder="Deixe em branco para não alterar">
                            </div>
                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="/" class="btn btn-secondary me-md-2 btn-hover"><i class="bi bi-x-circle me-1"></i> Cancelar</a>
                                <button type="submit" class="btn btn-success btn-hover"><i class="bi bi-check-circle-fill me-1"></i> Salvar</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <!-- Coluna da Tabela de Usuários -->
            <div class="col-lg-8 col-md-12">
                <div class="card shadow-sm">
                    <div class="card-header">
                       <h5 class="mb-0"><i class="bi bi-list-ul me-2"></i> Usuários Cadastrados</h5>
                    </div>
                    <div class="card-body">
                         <div class="table-responsive">
                            <table class="table table-hover table-striped align-middle">
                                <thead>
                                    <tr>
                                        <th>ID</th>
                                        <th>Username</th>
                                        <th>Email</th>
                                        <th class="text-center">Ações</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr th:each="user : ${users}">
                                        <td th:text="${user.id}"></td>
                                        <td th:text="${user.username}"></td>
                                        <td th:text="${user.email}"></td>
                                        <td class="text-center">
                                            <!-- Botão Editar -->
                                            <a th:href="@{/edit/{id}(id=${user.id})}" class="btn btn-sm btn-warning btn-hover" title="Editar">
                                                <i class="bi bi-pencil-fill"></i>
                                            </a>
                                            <!-- Botão Excluir -->
                                            <a th:href="@{/delete/{id}(id=${user.id})}" class="btn btn-sm btn-danger btn-hover" title="Excluir">
                                                <i class="bi bi-trash-fill"></i>
                                            </a>
                                             <!-- Botão para Gerenciar Perfil -->
                                            <button type="button" class="btn btn-sm btn-info btn-hover" title="Alterar Perfil"
                                                    data-bs-toggle="modal" data-bs-target="#perfilModal"
                                                    th:attr="data-user-id=${user.id}, data-user-name=${user.username}">
                                                <i class="bi bi-shield-lock-fill"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </main>
    
    <!-- Modal para Gerenciar Perfil -->
    <div class="modal fade" id="perfilModal" tabindex="-1" aria-labelledby="perfilModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="perfilModalLabel"><i class="bi bi-shield-lock-fill me-2"></i> Alterar Perfil do Usuário</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <form th:action="@{/user/change-role}" method="post">
                    <div class="modal-body">
                        <p>Você está alterando o perfil do usuário: <strong id="usernameModal"></strong></p>
                        <input type="hidden" id="userIdModal" name="userId" />
                        
                        <div class="mb-3">
                            <label for="role" class="form-label">Selecione o novo perfil:</label>
                            <select id="role" name="role" class="form-select">
                                <option value="ROLE_ADMIN">Administrador</option>
                                <option value="ROLE_USER">Usuário</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary btn-hover" data-bs-dismiss="modal">Cancelar</button>
                        <button type="submit" class="btn btn-primary btn-hover">Salvar Alterações</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" xintegrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>

    <!-- Script para popular o Modal -->
    <script>
        const perfilModal = document.getElementById('perfilModal');
        if (perfilModal) {
            perfilModal.addEventListener('show.bs.modal', event => {
                // Botão que acionou o modal
                const button = event.relatedTarget;
                
                // Extrai informações dos atributos data-*
                const userId = button.getAttribute('data-user-id');
                const userName = button.getAttribute('data-user-name');

                // Atualiza o conteúdo do modal
                const modalBodyUsername = perfilModal.querySelector('#usernameModal');
                const modalInputUserId = perfilModal.querySelector('#userIdModal');

                modalBodyUsername.textContent = userName;
                modalInputUserId.value = userId;
            });
        }
    </script>
</body>
</html>


