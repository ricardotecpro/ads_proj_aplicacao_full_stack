# 🚀 Construindo, Implantando e Dominando o "Controle de Gastos"
v1.3
Bem-vindo ao guia definitivo para criar a aplicação **Controle de Gastos**. Esta versão foi aprimorada com base em experiências práticas de build e deploy, garantindo um processo mais robusto e educativo. Vamos construir uma aplicação Spring Boot com uma interface dinâmica usando htmx e implantá-la na nuvem de forma profissional com Docker.

**Tecnologias Finais:**

  * **Backend**: Java 21, Spring Boot, Spring Data JPA
  * **Frontend**: Thymeleaf + htmx
  * **Banco de Dados**: H2 (Desenvolvimento), PostgreSQL (Produção no Neon)
  * **Deploy**: Docker, Render

-----

### 1\. A Arquitetura e os Conceitos

#### 1.1. Arquitetura do Projeto (MVC)

Usaremos o padrão Model-View-Controller para organizar nosso código, resultando em uma aplicação de fácil manutenção.
graph TD
    subgraph Browser
        A[👨‍💻 Usuário]
    end

    subgraph "Aplicação Spring Boot (Container Docker)"
        C[LancamentoController] -- Usa --> D{LancamentoRepository}
        D -- Gerencia --> E[Lancamento - Entidade]
        C -- Renderiza --> B[View: Thymeleaf + htmx]
    end
    
    subgraph "Banco de Dados (Neon)"
        F[(PostgreSQL)]
    end

    A <-->|Requisições HTTP| B
    B -- Aciona via htmx --> C
    E -- Mapeada para --> F


#### 1.2. Como Funciona o htmx

Htmx potencializa nosso HTML para que ele possa fazer requisições ao servidor e atualizar partes da página sem precisar recarregar tudo e sem escrever JavaScript complexo. Ele funciona respondendo a 4 perguntas através de atributos HTML:

1.  **O que aciona?** (`hx-trigger`): Um clique, o envio de um formulário, etc.
2.  **Que requisição fazer?** (`hx-get`, `hx-post`, `hx-put`, `hx-delete`): Os verbos HTTP.
3.  **Qual o alvo da atualização?** (`hx-target`): Um seletor CSS que aponta para onde a resposta deve ser colocada.
4.  **Como atualizar?** (`hx-swap`): A estratégia para inserir a resposta (ex: substituir o conteúdo interno `innerHTML` ou o elemento inteiro `outerHTML`).

O servidor, por sua vez, responde com pequenos fragmentos de HTML, e não com JSON.

-----

### 2\. Fase 1: Preparação do Ambiente e Controle de Versão

1.  **Crie a Pasta e Inicie o Git:**

    ```bash
    mkdir controle-de-gastos
    cd controle-de-gastos
    git init
    ```

2.  **Crie o Repositório no GitHub** e conecte-o ao seu repositório local.

-----

### 3\. Fase 2: Criação do Esqueleto do Projeto

1.  Acesse [start.spring.io](https://start.spring.io) e configure:

      * **Project:** Maven
      * **Language:** Java
      * **Java:** 21
      * **Dependencies:** `Spring Web`, `Spring Data JPA`, `Thymeleaf`, `H2 Database`, `PostgreSQL Driver`, `Spring Boot DevTools`.

2.  **GENERATE**, baixe e descompacte o projeto na sua pasta.

3.  **(Prevenção de Erro 1) Corrija a Permissão de Execução:** Antes do primeiro commit, execute este comando no terminal para evitar o erro `Permission denied` nos sistemas de deploy:

    ```bash
    git update-index --chmod=+x mvnw
    ```

4.  **(Prevenção de Erro 2) Force a Codificação UTF-8:** Abra o arquivo `pom.xml` e adicione a propriedade `<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>`. Isso previne o erro `MalformedInputException`.

    **Arquivo: `pom.xml` (seção `<properties>`)**

    ```xml
    <properties>
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    ```

5.  **Faça o primeiro commit:**

    ```bash
    git add .
    git commit -m "🚀 Feat: Initial project structure with build fixes"
    git push -u origin main
    ```

-----

### 4\. Fase 3: Modelagem e Persistência de Dados

**Arquivo: `src/.../model/TipoLancamento.java`**

```java
package br.com.controledegastos.model;

public enum TipoLancamento {
    RECEITA,
    DESPESA
}
```

**Arquivo: `src/.../model/Lancamento.java`**

```java
package br.com.controledegastos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Lancamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descricao;
    private BigDecimal valor;
    private LocalDate data = LocalDate.now();
    @Enumerated(EnumType.STRING)
    private TipoLancamento tipo;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public TipoLancamento getTipo() { return tipo; }
    public void setTipo(TipoLancamento tipo) { this.tipo = tipo; }
}
```

**Arquivo: `src/.../repository/LancamentoRepository.java`**

```java
package br.com.controledegastos.repository;

import br.com.controledegastos.model.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
}
```

-----

### 5\. Fase 4: Lógica de Backend (Controller com CRUD Completo)

**Arquivo: `src/.../controller/LancamentoController.java`**

```java
package br.com.controledegastos.controller;

import br.com.controledegastos.model.Lancamento;
import br.com.controledegastos.model.TipoLancamento;
import br.com.controledegastos.repository.LancamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Controller
public class LancamentoController {

    @Autowired
    private LancamentoRepository lancamentoRepository;

    private void carregarLancamentos(Model model) {
        List<Lancamento> lancamentos = lancamentoRepository.findAll();
        lancamentos.sort(Comparator.comparing(Lancamento::getData).reversed());
        model.addAttribute("lancamentos", lancamentos);
    }

    @GetMapping("/")
    public String index(Model model) {
        carregarLancamentos(model);
        model.addAttribute("novoLancamento", new Lancamento());
        model.addAttribute("tipos", TipoLancamento.values());
        return "index";
    }

    @PostMapping("/lancamentos")
    public String addLancamento(@ModelAttribute Lancamento novoLancamento, Model model) {
        lancamentoRepository.save(novoLancamento);
        carregarLancamentos(model);
        return "index :: lista-lancamentos";
    }

    @DeleteMapping("/lancamentos/{id}")
    public String deleteLancamento(@PathVariable Long id, Model model) {
        lancamentoRepository.deleteById(id);
        carregarLancamentos(model);
        return "index :: lista-lancamentos";
    }

    @GetMapping("/lancamentos/editar/{id}")
    public String editLancamento(@PathVariable Long id, Model model) {
        Optional<Lancamento> lancamentoOpt = lancamentoRepository.findById(id);
        if (lancamentoOpt.isPresent()) {
            model.addAttribute("lancamentoParaEditar", lancamentoOpt.get());
            model.addAttribute("tipos", TipoLancamento.values());
            return "index :: form-edicao";
        }
        return "index :: lista-lancamentos";
    }

    @PutMapping("/lancamentos/{id}")
    public String updateLancamento(@PathVariable Long id, @ModelAttribute Lancamento lancamentoAtualizado, Model model) {
        Optional<Lancamento> lancamentoOpt = lancamentoRepository.findById(id);
        if (lancamentoOpt.isPresent()) {
            Lancamento lancamentoExistente = lancamentoOpt.get();
            lancamentoExistente.setDescricao(lancamentoAtualizado.getDescricao());
            lancamentoExistente.setValor(lancamentoAtualizado.getValor());
            lancamentoExistente.setTipo(lancamentoAtualizado.getTipo());
            lancamentoRepository.save(lancamentoExistente);
        }
        carregarLancamentos(model);
        return "index :: lista-lancamentos";
    }
}
```

-----

### 6\. Fase 5: Configuração de Ambientes

**Arquivo: `src/main/resources/application.properties`**

```properties
# Configurações do H2 Database (perfil 'default')
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
```

**Arquivo: `src/main/resources/application-prod.properties`**

```properties
# Configurações do PostgreSQL (perfil 'prod')
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

-----

### 7\. Fase 6: Construção da Interface (Frontend Comentado)

**Arquivo: `src/main/resources/templates/index.html`**

```html
<!DOCTYPE html>
<html lang="pt-br" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Controle de Gastos</title>
    <style>
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif; margin: 40px; background-color: #f4f4f9; color: #333; }
        .container { max-width: 800px; margin: auto; background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        form { display: flex; gap: 10px; margin-bottom: 20px; flex-wrap: wrap; align-items: center; }
        input, select, button { padding: 10px; border-radius: 5px; border: 1px solid #ddd; font-size: 14px; }
        button { color: white; border: none; cursor: pointer; }
        .add-btn { background-color: #007bff; }
        table { width: 100%; border-collapse: collapse; }
        th, td { padding: 12px; border-bottom: 1px solid #ddd; text-align: left; vertical-align: middle; }
        .despesa { color: #d9534f; font-weight: bold; }
        .receita { color: #5cb85c; font-weight: bold; }
        .action-btn { font-size: 12px; padding: 6px 10px; }
        .edit-btn { background-color: #f0ad4e; }
        .delete-btn { background-color: #d9534f; }
        .save-btn { background-color: #5cb85c; }
        .cancel-btn { background-color: #777; }
    </style>
    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
</head>
<body>
    <div class="container">
        <h1>Meu Controle de Gastos 💰</h1>

        <form 
              hx-post="/lancamentos"
              hx-target="#lista-lancamentos"
              hx-swap="innerHTML"
              hx-on::after-request="this.reset()">
            <input type="text" name="descricao" placeholder="Descrição" required>
            <input type="number" step="0.01" name="valor" placeholder="Valor" required>
            <select name="tipo" required>
                <option th:each="tipoOpt : ${tipos}" th:value="${tipoOpt}" th:text="${tipoOpt}"></option>
            </select>
            <button type="submit" class="add-btn">Adicionar</button>
        </form>

        <div id="lista-lancamentos">
        
            <table th:fragment="lista-lancamentos">
                <thead>
                    <tr>
                        <th>Descrição</th>
                        <th>Valor</th>
                        <th>Data</th>
                        <th>Tipo</th>
                        <th>Ações</th>
                    </tr>
                </thead>
                <tbody>
                    <tr th:each="lancamento : ${lancamentos}" th:id="'lancamento-' + ${lancamento.id}">
                        <td th:text="${lancamento.descricao}"></td>
                        <td th:text="|R$ ${#numbers.formatDecimal(lancamento.valor, 1, 'POINT', 2, 'COMMA')}|"
                            th:class="${lancamento.tipo.name() == 'DESPESA' ? 'despesa' : 'receita'}">
                        </td>
                        <td th:text="${#temporals.format(lancamento.data, 'dd/MM/yyyy')}"></td>
                        <td th:text="${lancamento.tipo.name()}"></td>
                        <td>
                            <button class.action-btn edit-btn"
                                    th:hx-get="@{/lancamentos/editar/{id}(id=${lancamento.id})}"
                                    th:hx-target="'#lancamento-' + ${lancamento.id}"
                                    hx-swap="outerHTML">
                                Alterar
                            </button>
                            
                            <button class.action-btn delete-btn"
                                    th:hx-delete="@{/lancamentos/{id}(id=${lancamento.id})}"
                                    hx-target="#lista-lancamentos"
                                    hx-swap="innerHTML"
                                    hx-confirm="Tem certeza que deseja excluir?">
                                Excluir
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    </div>

    <tr th:fragment="form-edicao" th:id="'lancamento-' + ${lancamentoParaEditar.id}">
        <form>
            <td><input type="text" name="descricao" th:value="${lancamentoParaEditar.descricao}"></td>
            <td><input type="number" step="0.01" name="valor" th:value="${lancamentoParaEditar.valor}"></td>
            <td th:text="${#temporals.format(lancamentoParaEditar.data, 'dd/MM/yyyy')}"></td>
            <td>
                <select name="tipo">
                    <option th:each="tipoOpt : ${tipos}"
                            th:value="${tipoOpt}"
                            th:text="${tipoOpt}"
                            th:selected="${tipoOpt == lancamentoParaEditar.tipo}"></option>
                </select>
            </td>
            <td>
                <button class="action-btn save-btn"
                        th:hx-put="@{/lancamentos/{id}(id=${lancamentoParaEditar.id})}"
                        th:hx-target="'#lista-lancamentos'"
                        hx-swap="innerHTML"
                        th:hx-include="'#lancamento-' + ${lancamentoParaEditar.id} + ' form'">
                    Salvar
                </button>
                
                <button class="action-btn cancel-btn"
                        th:hx-get="@{/}"
                        th:hx-target="'#lista-lancamentos'"
                        hx-swap="innerHTML">
                    Cancelar
                </button>
            </td>
        </form>
    </tr>
</body>
</html>
```

-----

### 8\. Fase 7: Containerização com Docker

**Arquivo: `Dockerfile`**

```dockerfile
# Estágio 1: Build da Aplicação com OpenJDK
FROM openjdk:21-jdk as builder
WORKDIR /app
COPY .mvn/ .mvn
COPY mvnw .
COPY pom.xml .
RUN ./mvnw dependency:go-offline
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Estágio 2: Imagem Final de Execução (mais leve)
FROM openjdk:21-jre
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Envie o código final para o GitHub antes de prosseguir.**

-----

### 9\. Fase 8: Deploy em Produção (Neon + Render)

#### 9.1. Crie o Banco de Dados no Neon

  * Crie um projeto no [Neon](https://neon.tech) e guarde as credenciais de conexão.

#### 9.2. Faça o Deploy no Render com Docker

1.  No painel do Render, crie um **New Web Service** e conecte seu repositório.
2.  Na configuração:
      * **Runtime**: Selecione **`Docker`**.
      * **Build/Start Commands**: Deixe estes campos **em branco**.
3.  Vá até **Advanced** e adicione as **Environment Variables**:
      * `SPRING_PROFILES_ACTIVE`: `prod`
      * `DB_URL`: `jdbc:postgresql://<HOST_DO_NEON>/<DB_NAME_DO_NEON>?sslmode=require`
      * `DB_USERNAME`: (Seu usuário do Neon)
      * `DB_PASSWORD`: (Sua senha do Neon)
4.  Clique em **Create Web Service**.

-----

### ✅ Conclusão

Parabéns\! Você finalizou o guia completo, construindo e implantando uma aplicação robusta e moderna. O projeto agora conta com um CRUD completo e um processo de deploy profissional e à prova de falhas usando Docker.
