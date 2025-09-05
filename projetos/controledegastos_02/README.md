# 🚀 Construindo o "Controle de Gastos" do Zero
v2.0

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

### Estrutura Final do Projeto

Antes de começarmos, esta é a estrutura de arquivos e pastas que teremos ao final do projeto:

```
controle-de-gastos/
├── .git/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/controledegastos/
│   │   │       ├── ControleDeGastosApplication.java
│   │   │       ├── controller/
│   │   │       │   └── LancamentoController.java
│   │   │       ├── model/
│   │   │       │   ├── Lancamento.java
│   │   │       │   └── TipoLancamento.java
│   │   │       └── repository/
│   │   │           └── LancamentoRepository.java
│   │   └── resources/
│   │       ├── templates/
│   │       │   └── index.html
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/
├── .gitignore
├── Dockerfile         <-- Arquivo de deploy
├── HELP.md
├── mvnw
├── mvnw.cmd
└── pom.xml            <-- Configuração do projeto
```

-----

### Fase 1: Configuração Inicial do Projeto

Nesta fase, preparamos nosso ambiente e o esqueleto do projeto, já aplicando correções para evitar erros comuns.

1.  **Inicie o Git** em uma nova pasta e conecte-a a um repositório no GitHub.

2.  **Gere o Projeto no Spring Initializr** ([start.spring.io](https://start.spring.io)) com as seguintes dependências: `Spring Web`, `Spring Data JPA`, `Thymeleaf`, `H2 Database`, `PostgreSQL Driver`, `Spring Boot DevTools`.

3.  **Descompacte** o projeto.

4.  **(Ação Preventiva 1) Ajuste a Permissão de Execução:** Para evitar o erro `Permission denied` no deploy, execute no terminal:

    ```bash
    git update-index --chmod=+x mvnw
    ```

5.  **(Ação Preventiva 2) Garanta a Codificação Correta:** Substitua o conteúdo do seu `pom.xml` pelo código abaixo. Ele já inclui a versão 21 do Java e a propriedade que força a codificação UTF-8, evitando erros de `parsing`.

    **Arquivo:** `pom.xml`

    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
        <modelVersion>4.0.0</modelVersion>
        <parent>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-parent</artifactId>
            <version>3.5.5</version>
            <relativePath/> </parent>
        <groupId>br.com.controledegastos</groupId>
        <artifactId>controle-de-gastos</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <name>controle-de-gastos</name>
        <description>Aplicação para controle de gastos pessoais</description>
        <properties>
            <java.version>21</java.version>
            <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        </properties>
        <dependencies>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-data-jpa</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-thymeleaf</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-web</artifactId>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-devtools</artifactId>
                <scope>runtime</scope>
                <optional>true</optional>
            </dependency>
            <dependency>
                <groupId>com.h2database</groupId>
                <artifactId>h2</artifactId>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>org.postgresql</groupId>
                <artifactId>postgresql</artifactId>
                <scope>runtime</scope>
            </dependency>
            <dependency>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-test</artifactId>
                <scope>test</scope>
            </dependency>
        </dependencies>
        <build>
            <plugins>
                <plugin>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-maven-plugin</artifactId>
                </plugin>
            </plugins>
        </build>
    </project>
    ```

6.  **Faça o primeiro commit** com a estrutura inicial corrigida.

-----

### Fase 2: A Camada de Dados (Model e Repository)

Começamos definindo a estrutura dos nossos dados e como vamos acessá-los.

#### **Arquivo:** `src/main/java/br/com/controledegastos/model/TipoLancamento.java`

```java
package br.com.controledegastos.model;

public enum TipoLancamento {
    RECEITA,
    DESPESA
}
```

#### **Arquivo:** `src/main/java/br/com/controledegastos/model/Lancamento.java`

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

#### **Arquivo:** `src/main/java/br/com/controledegastos/repository/LancamentoRepository.java`

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

### Fase 3: A Camada de Lógica (Controller)

Com o acesso aos dados definido, criamos o Controller que irá orquestrar as ações da nossa aplicação (CRUD).

#### **Arquivo:** `src/main/java/br/com/controledegastos/controller/LancamentoController.java`

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
        // Garante que o objeto exista para o parser do Thymeleaf na carga inicial da página.
        model.addAttribute("lancamentoParaEditar", new Lancamento());
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
            lancamentoExistente.setData(lancamentoAtualizado.getData());
            lancamentoRepository.save(lancamentoExistente);
        }
        carregarLancamentos(model);
        return "index :: lista-lancamentos";
    }
}
```

-----

### Fase 4: A Camada de Apresentação (View)

Agora, criamos a interface com a qual o usuário irá interagir, usando um layout moderno e dinâmico.

#### **Arquivo:** `src/main/resources/templates/index.html`

```html
<!DOCTYPE html>
<html lang="pt-br" xmlns:th="http://www.thymeleaf.org" class="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Controle de Gastos</title>
    
    <style>
        /* CSS Variables para Temas Light/Dark */
        :root {
            --bg-color: #f4f5f7;
            --card-bg-color: #ffffff;
            --text-color: #172b4d;
            --subtext-color: #6b778c;
            --border-color: #dfe1e6;
            --primary-color: #0052cc;
            --primary-hover-color: #0065ff;
            --green-color: #36b37e;
            --red-color: #de350b;
            --yellow-color: #ffab00;
            --grey-color: #6b778c;
            --shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
        }

        html.dark {
            --bg-color: #172b4d;
            --card-bg-color: #283e5d;
            --text-color: #b0c0db;
            --subtext-color: #8c9cb5;
            --border-color: #42526e;
            --primary-color: #4c9aff;
            --primary-hover-color: #69b4ff;
        }

        /* Estilos Gerais */
        body {
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            margin: 0;
            padding: 40px;
            background-color: var(--bg-color);
            color: var(--text-color);
            transition: background-color 0.3s, color 0.3s;
        }
        .container {
            max-width: 800px;
            margin: auto;
            background: var(--card-bg-color);
            padding: 24px;
            border-radius: 8px;
            box-shadow: var(--shadow);
            transition: background-color 0.3s;
        }

        /* Formulários */
        form {
            display: flex;
            gap: 12px;
            margin-bottom: 24px;
            flex-wrap: wrap;
        }
        input, select {
            padding: 10px;
            border-radius: 5px;
            border: 1px solid var(--border-color);
            font-size: 14px;
            background-color: var(--bg-color);
            color: var(--text-color);
        }
        button {
            padding: 10px 15px;
            border-radius: 5px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            color: white;
            cursor: pointer;
            transition: background-color 0.2s;
        }
        .add-btn { background-color: var(--primary-color); }
        .add-btn:hover { background-color: var(--primary-hover-color); }

        /* Estrutura da Lista (Flexbox) */
        .list-header, .list-item, .edit-form-row {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 0;
            border-bottom: 1px solid var(--border-color);
        }
        .list-header {
            font-weight: 600;
            color: var(--subtext-color);
            font-size: 12px;
            text-transform: uppercase;
        }
        .col-desc { flex: 4; min-width: 0; }
        .col-valor { flex: 2; min-width: 0; }
        .col-data { flex: 2; min-width: 0; }
        .col-tipo { flex: 2; min-width: 0; }
        .col-acoes { flex: 3; min-width: 0; justify-content: flex-end; display: flex;}

        .valor { text-align: right; }
        .valor.despesa { color: var(--red-color); }
        .valor.receita { color: var(--green-color); }

        /* Botões de Ação */
        .actions-wrapper { display: flex; gap: 8px; }
        .action-btn { font-size: 12px; padding: 6px 12px; }
        .edit-btn { background-color: var(--yellow-color); }
        .delete-btn { background-color: var(--red-color); }
        .save-btn { background-color: var(--green-color); }
        .cancel-btn { background-color: var(--grey-color); }
        
        /* Tema e Rodapé */
        .footer {
            text-align: center;
            margin-top: 24px;
            font-size: 12px;
            color: var(--subtext-color);
        }
        #theme-toggle {
            position: fixed;
            top: 20px;
            right: 20px;
            background-color: var(--card-bg-color);
            color: var(--text-color);
            border: 1px solid var(--border-color);
            width: 40px;
            height: 40px;
            border-radius: 50%;
            font-size: 20px;
            display: flex;
            align-items: center;
            justify-content: center;
        }
    </style>
    
    <script src="https://unpkg.com/htmx.org@1.9.12"></script>
</head>
<body>

    <button id="theme-toggle">🌙</button>

    <div class="container">
        <h1>Meu Controle de Gastos 💰</h1>

        <form
              hx-post="/lancamentos"
              hx-target="#lista-lancamentos"
              hx-swap="innerHTML"
              hx-on::after-request="this.reset()">
            <input type="text" name="descricao" placeholder="Descrição" required style="flex-grow: 2;">
            <input type="number" step="0.01" name="valor" placeholder="Valor" required style="flex-grow: 1;">
            <select name="tipo" required style="flex-grow: 1;">
                <option th:each="tipoOpt : ${tipos}" th:value="${tipoOpt}" th:text="${tipoOpt}"></option>
            </select>
            <button type="submit" class="add-btn" style="flex-grow: 1;">Adicionar</button>
        </form>

        <div id="lista-lancamentos">
            <div th:fragment="lista-lancamentos">
                <div class="list-header">
                    <div class="col-desc">Descrição</div>
                    <div class="col-valor">Valor</div>
                    <div class="col-data">Data</div>
                    <div class="col-tipo">Tipo</div>
                    <div class="col-acoes">Ações</div>
                </div>

                <div th:each="lancamento : ${lancamentos}" th:id="'lancamento-' + ${lancamento.id}" class="list-item">
                    <div class="col-desc" th:text="${lancamento.descricao}"></div>
                    <div class="col-valor valor" th:text="|R$ ${#numbers.formatDecimal(lancamento.valor, 1, 'POINT', 2, 'COMMA')}|"
                         th:classappend="${lancamento.tipo.name() == 'DESPESA' ? 'despesa' : 'receita'}">
                    </div>
                    <div class="col-data" th:text="${#temporals.format(lancamento.data, 'dd/MM/yyyy')}"></div>
                    <div class="col-tipo" th:text="${lancamento.tipo.name()}"></div>
                    <div class="col-acoes">
                        <div class="actions-wrapper">
                            <button class="action-btn edit-btn"
                                    th:hx-get="@{/lancamentos/editar/{id}(id=${lancamento.id})}"
                                    th:hx-target="'#lancamento-' + ${lancamento.id}"
                                    hx-swap="outerHTML">
                                Alterar
                            </button>
                            <button class="action-btn delete-btn"
                                    th:hx-delete="@{/lancamentos/{id}(id=${lancamento.id})}"
                                    hx-target="#lista-lancamentos"
                                    hx-swap="innerHTML"
                                    hx-confirm="Tem certeza que deseja excluir?">
                                Excluir
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <div class="footer">
        Softhouse(c)2025
    </div>

    <div style="display: none;">
        <th:block th:fragment="form-edicao">
            <div th:id="'lancamento-' + ${lancamentoParaEditar.id}" class="edit-form-row">
                <div class="col-desc">
                    <input type="text" name="descricao" th:value="${lancamentoParaEditar.descricao}">
                </div>
                <div class="col-valor">
                    <input type="number" step="0.01" name="valor" th:value="${lancamentoParaEditar.valor}">
                </div>
                <div class="col-data">
                    <input type="date" name="data" th:value="${#temporals.format(lancamentoParaEditar.data, 'yyyy-MM-dd')}">
                </div>
                <div class="col-tipo">
                    <select name="tipo">
                        <option th:each="tipoOpt : ${tipos}"
                                th:value="${tipoOpt}"
                                th:text="${tipoOpt}"
                                th:selected="${tipoOpt == lancamentoParaEditar.tipo}"></option>
                    </select>
                </div>
                <div class="col-acoes">
                    <div class="actions-wrapper">
                        <button class="action-btn save-btn"
                                th:hx-put="@{/lancamentos/{id}(id=${lancamentoParaEditar.id})}"
                                hx-target="#lista-lancamentos"
                                hx-swap="innerHTML"
                                hx-include="closest .edit-form-row">
                            Salvar
                        </button>
                        <button class="action-btn cancel-btn"
                                th:hx-get="@{/}"
                                hx-target="#lista-lancamentos"
                                hx-swap="innerHTML">
                            Cancelar
                        </button>
                    </div>
                </div>
            </div>
        </th:block>
    </div>
    
    <script>
        const themeToggle = document.getElementById('theme-toggle');
        const html = document.documentElement;

        themeToggle.addEventListener('click', () => {
            if (html.classList.contains('dark')) {
                html.classList.remove('dark');
                html.classList.add('light');
                themeToggle.textContent = '🌙';
            } else {
                html.classList.remove('light');
                html.classList.add('dark');
                themeToggle.textContent = '☀️';
            }
        });
    </script>

</body>
</html>
```

-----

### Fase 5: Containerização e Deploy

Finalmente, preparamos a aplicação para o mundo real.

#### **Arquivo:** `Dockerfile`

[cite\_start]O Dockerfile é um manual de instruções para construir um container com nossa aplicação[cite: 1, 2]. Este é otimizado para builds mais rápidos e uma imagem final mais leve.

  * [cite\_start]**Estágio 1 (`builder`):** Usa um JDK completo para compilar o projeto com Maven[cite: 1]. [cite\_start]A instrução `dependency:go-offline` armazena as dependências em cache, acelerando builds futuros[cite: 2].
  * [cite\_start]**Estágio 2 (final):** Usa uma imagem JRE, que é menor, e copia apenas o `.jar` compilado, resultando em um container mais seguro e eficiente[cite: 2].

<!-- end list -->

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

#### Deploy no Render

1.  **Crie o Banco de Dados no Neon:** Obtenha suas credenciais de conexão.
2.  **Configure o Serviço no Render:**
      * Crie um **New Web Service** e conecte seu repositório.
      * Selecione **Docker** como **Runtime**.
      * Deixe `Build Command` e `Start Command` **em branco**.
      * Adicione as **Environment Variables** (`SPRING_PROFILES_ACTIVE`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`).
      * Clique em **Create Web Service**.

-----

### ✅ Conclusão

Parabéns\! Você concluiu um guia completo que não apenas constrói uma aplicação funcional, mas também incorpora as melhores práticas e soluções para problemas reais de desenvolvimento e deploy.

---

### 🚀 [ricardotecpro.github.io](https://ricardotecpro.github.io/)
