# 🚀 Construindo e Implantando o "Controle de Gastos" com Spring Boot e htmx
v1.1

Olá\! Sou um arquiteto de sistemas e, neste guia, vamos construir juntos uma aplicação web moderna e robusta chamada **Controle de Gastos**. O objetivo é criar um gerenciador financeiro simples, mas com uma experiência de usuário fluida, similar a uma **Single Page Application (SPA)**, sem a complexidade de frameworks JavaScript.

Ao final, você terá uma aplicação funcional implantada na nuvem, pronta para ser usada e compartilhada.

**O que você vai aprender:**

  * Estruturar um projeto **Spring Boot** do zero.
  * Modelar dados e usar o **Spring Data JPA**.
  * Criar uma interface reativa com **Thymeleaf** e **htmx**.
  * Gerenciar diferentes ambientes (desenvolvimento e produção) com **Spring Profiles**.
  * Fazer o deploy de um banco de dados **PostgreSQL** no **Neon**.
  * Fazer o deploy de uma aplicação Java no **Render**.

Vamos começar\!

-----

### 🗂️ Fase 1: Preparação do Ambiente e Controle de Versão

Todo projeto profissional começa com um bom controle de versão. Vamos usar Git e hospedá-lo no GitHub.

1.  **Crie a Pasta do Projeto:**
    Abra seu terminal e crie uma pasta para o projeto.

    ```bash
    mkdir controle-de-gastos
    cd controle-de-gastos
    ```

2.  **Inicialize o Repositório Git:**

    ```bash
    git init
    ```

3.  **Crie o Repositório no GitHub:**

      * Vá para o [GitHub](https://github.com) e crie um novo repositório (pode ser público ou privado).
      * **NÃO** inicialize com `README`, `.gitignore` ou licença. Queremos um repositório limpo.
      * Após criar, o GitHub lhe dará os comandos para conectar seu repositório local. Copie-os e execute no seu terminal. Serão parecidos com isto:

    <!-- end list -->

    ```bash
    git remote add origin https://github.com/seu-usuario/controle-de-gastos.git
    git branch -M main
    # Faremos o primeiro push após criar o projeto na próxima fase.
    ```

-----

### 🏗️ Fase 2: Criação do Esqueleto do Projeto

Usaremos o **Spring Initializr** para gerar a base da nossa aplicação com todas as dependências necessárias.

1.  Acesse o [Spring Initializr](https://start.spring.io/).

2.  Preencha os campos da seguinte forma:

      * **Project:** Maven
      * **Language:** Java
      * **Spring Boot:** A versão mais recente (ex: 3.x.x)
      * **Project Metadata:**
          * **Group:** `br.com.controledegastos`
          * **Artifact:** `controle-de-gastos`
          * **Name:** `controle-de-gastos`
          * **Description:** `Aplicação para controle de gastos pessoais`
          * **Package name:** `br.com.controledegastos`
      * **Packaging:** Jar
      * **Java:** 17

3.  No campo **Dependencies**, clique em "ADD DEPENDENCIES" e adicione as seguintes:

      * `Spring Web`: Para criar controllers e APIs REST.
      * `Spring Data JPA`: Para persistência de dados de forma simples.
      * `Thymeleaf`: Nosso motor de templates para renderizar o HTML.
      * `H2 Database`: Banco de dados em memória para o ambiente de desenvolvimento.
      * `PostgreSQL Driver`: Para conectar com nosso banco de produção.
      * `Spring Boot DevTools`: Para live reload durante o desenvolvimento.

4.  Clique em **GENERATE**. Um arquivo `.zip` será baixado.

5.  **Descompacte** o conteúdo do `.zip` dentro da pasta `controle-de-gastos` que criamos na Fase 1. A estrutura de arquivos deve ficar assim:

    ```
    controle-de-gastos/
    ├── .git/
    ├── .mvn/
    ├── src/
    ├── mvnw
    ├── mvnw.cmd
    └── pom.xml
    ```

6.  **Faça o primeiro commit:**

    ```bash
    git add .
    git commit -m "🚀 Feat: Initial project structure from Spring Initializr"
    git push -u origin main
    ```

-----

### 💾 Fase 3: Modelagem de Dados

Vamos definir a estrutura do nosso "Lançamento" financeiro e o repositório para acessá-lo no banco de dados.

1.  **Crie o Enum `TipoLancamento`:**
    Dentro de `src/main/java/br/com/controledegastos/`, crie um novo pacote chamado `model`. Dentro dele, crie um Enum `TipoLancamento.java`.

    ```java
    // src/main/java/br/com/controledegastos/model/TipoLancamento.java
    package br.com.controledegastos.model;

    public enum TipoLancamento {
        RECEITA,
        DESPESA
    }
    ```

2.  **Crie a Entidade `Lancamento`:**
    No mesmo pacote `model`, crie a classe `Lancamento.java`.

    ```java
    // src/main/java/br/com/controledegastos/model/Lancamento.java
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

3.  **Crie o Repositório `LancamentoRepository`:**
    Crie um novo pacote chamado `repository`. Dentro dele, crie a interface `LancamentoRepository.java`.

    ```java
    // src/main/java/br/com/controledegastos/repository/LancamentoRepository.java
    package br.com.controledegastos.repository;

    import br.com.controledegastos.model.Lancamento;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    @Repository
    public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    }
    ```

    O `JpaRepository` já nos fornece todos os métodos de CRUD (`save`, `findById`, `findAll`, `deleteById`, etc.) magicamente\!

-----

### ⚙️ Fase 4: Lógica de Backend (Controller)

O `Controller` irá receber as requisições do navegador, processá-las e devolver o HTML.

1.  **Crie o `LancamentoController`:**
    Crie um novo pacote chamado `controller`. Dentro dele, crie a classe `LancamentoController.java`.

    ```java
    // src/main/java/br/com/controledegastos/controller/LancamentoController.java
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

    @Controller
    public class LancamentoController {

        @Autowired
        private LancamentoRepository lancamentoRepository;

        // Método para carregar a página principal
        @GetMapping("/")
        public String index(Model model) {
            List<Lancamento> lancamentos = lancamentoRepository.findAll();
            lancamentos.sort(Comparator.comparing(Lancamento::getData).reversed());
            model.addAttribute("lancamentos", lancamentos);
            model.addAttribute("novoLancamento", new Lancamento());
            model.addAttribute("tipos", TipoLancamento.values());
            return "index"; // Retorna o arquivo templates/index.html
        }

        // Método para adicionar um novo lançamento (via htmx)
        @PostMapping("/lancamentos")
        public String addLancamento(@ModelAttribute Lancamento novoLancamento, Model model) {
            lancamentoRepository.save(novoLancamento);
            
            // Após salvar, recarregamos a lista e a retornamos como um fragmento
            List<Lancamento> lancamentos = lancamentoRepository.findAll();
            lancamentos.sort(Comparator.comparing(Lancamento::getData).reversed());
            model.addAttribute("lancamentos", lancamentos);
            
            // Retorna apenas o fragmento da tabela, não a página inteira
            return "index :: lista-lancamentos"; 
        }

        // Método para excluir um lançamento (via htmx)
        @DeleteMapping("/lancamentos/{id}")
        public String deleteLancamento(@PathVariable Long id, Model model) {
            lancamentoRepository.deleteById(id);
            
            // Após deletar, recarregamos a lista e a retornamos como um fragmento
            List<Lancamento> lancamentos = lancamentoRepository.findAll();
            lancamentos.sort(Comparator.comparing(Lancamento::getData).reversed());
            model.addAttribute("lancamentos", lancamentos);
            
            // Retorna apenas o fragmento da tabela
            return "index :: lista-lancamentos";
        }
    }
    ```

    **Ponto Chave:** Note que os métodos `addLancamento` e `deleteLancamento` retornam `"index :: lista-lancamentos"`. Isso é uma instrução para o Thymeleaf renderizar apenas o pedaço (fragmento) do HTML chamado `lista-lancamentos` dentro do arquivo `index.html`. É essa a mágica que o htmx usará para atualizar a página dinamicamente.

-----

### 🎨 Fase 5: Construção da Interface com Thymeleaf e htmx

Agora vamos criar a interface que o usuário verá. Ela será um único arquivo HTML que será atualizado dinamicamente pelo htmx.

1.  **Crie o arquivo `index.html`:**
    Dentro de `src/main/resources/templates/`, crie o arquivo `index.html`.

2.  **Adicione o conteúdo HTML:**
    Este código inclui um formulário para adicionar lançamentos e uma tabela para listá-los. Observe os atributos `hx-*`, que são os comandos do htmx.

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
            form { display: flex; gap: 10px; margin-bottom: 20px; flex-wrap: wrap; }
            input, select, button { padding: 10px; border-radius: 5px; border: 1px solid #ddd; }
            button { background-color: #007bff; color: white; border: none; cursor: pointer; }
            button:hover { background-color: #0056b3; }
            table { width: 100%; border-collapse: collapse; }
            th, td { padding: 10px; border-bottom: 1px solid #ddd; text-align: left; }
            .despesa { color: #d9534f; }
            .receita { color: #5cb85c; }
            .delete-btn { background-color: #d9534f; }
            .delete-btn:hover { background-color: #c9302c; }
        </style>
        <script src="https://unpkg.com/htmx.org@1.9.12"></script>
    </head>
    <body>
        <div class="container">
            <h1>Meu Controle de Gastos 💰</h1>

            <form hx-post="/lancamentos"      
                  hx-target="#lista-lancamentos" 
                  hx-swap="outerHTML"            
                  hx-on::after-request="this.reset()">
                <input type="text" name="descricao" placeholder="Descrição" required>
                <input type="number" step="0.01" name="valor" placeholder="Valor" required>
                <select name="tipo" required>
                    <option th:each="tipoOpt : ${tipos}" th:value="${tipoOpt}" th:text="${tipoOpt}"></option>
                </select>
                <button type="submit">Adicionar</button>
            </form>

            <div id="lista-lancamentos" th:fragment="lista-lancamentos">
                <table>
                    <thead>
                        <tr>
                            <th>Descrição</th>
                            <th>Valor</th>
                            <th>Data</th>
                            <th>Tipo</th>
                            <th>Ação</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr th:each="lancamento : ${lancamentos}">
                            <td th:text="${lancamento.descricao}"></td>
                            <td th:text="|R$ ${#numbers.formatDecimal(lancamento.valor, 1, 'POINT', 2, 'COMMA')}|"
                                th:class="${lancamento.tipo.name() == 'DESPESA' ? 'despesa' : 'receita'}">
                            </td>
                            <td th:text="${#temporals.format(lancamento.data, 'dd/MM/yyyy')}"></td>
                            <td th:text="${lancamento.tipo.name()}"></td>
                            <td>
                                <button class="delete-btn"
                                        hx-delete="@{/lancamentos/{id}(id=${lancamento.id})}"
                                        hx-target="#lista-lancamentos"
                                        hx-swap="outerHTML"
                                        hx-confirm="Tem certeza que deseja excluir?">
                                    Excluir
                                </button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </body>
    </html>
    ```

    **Como o htmx funciona aqui:**

      * **Adicionar:** O `<form>` tem `hx-post="/lancamentos"`. Ao submeter, ele envia um POST para essa URL. `hx-target="#lista-lancamentos"` e `hx-swap="outerHTML"` dizem para pegar a resposta (que é o fragmento da tabela atualizada) e substituir todo o elemento `<div id="lista-lancamentos">`.
      * **Excluir:** O `<button>` de exclusão tem `hx-delete="/lancamentos/..."`. Ao clicar, ele envia uma requisição DELETE. A resposta (a tabela atualizada) também substitui o `<div id="lista-lancamentos">`.

-----

### 🌍 Fase 6: Configuração de Ambientes

Vamos configurar a aplicação para usar o H2 em desenvolvimento e o PostgreSQL em produção.

1.  **Arquivo `application.properties` (Desenvolvimento):**
    Este arquivo, localizado em `src/main/resources/`, já é o perfil padrão. Vamos configurá-lo para o H2.

    ```properties
    # Configurações do H2 Database (perfil 'default')
    spring.datasource.url=jdbc:h2:mem:testdb
    spring.datasource.driverClassName=org.h2.Driver
    spring.datasource.username=sa
    spring.datasource.password=
    spring.h2.console.enabled=true

    # JPA
    spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
    spring.jpa.hibernate.ddl-auto=update
    ```

    Agora você pode rodar sua aplicação localmente. Abra a classe `ControleDeGastosApplication.java` na sua IDE e execute-a. Acesse `http://localhost:8080` e `http://localhost:8080/h2-console` (URL do JDBC: `jdbc:h2:mem:testdb`) para testar.

2.  **Arquivo `application-prod.properties` (Produção):**
    Na mesma pasta, crie um novo arquivo chamado `application-prod.properties`. Este arquivo será ativado quando o perfil `prod` estiver ativo.

    ```properties
    # Configurações do PostgreSQL (perfil 'prod')
    # Usaremos variáveis de ambiente no Render para preencher estes valores
    spring.datasource.url=${DB_URL}
    spring.datasource.username=${DB_USERNAME}
    spring.datasource.password=${DB_PASSWORD}

    # JPA
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
    ```

    Os valores `${...}` serão substituídos por variáveis de ambiente que configuraremos na plataforma de deploy.

3.  **Commit das mudanças:**

    ```bash
    git add .
    git commit -m "✨ Feat: Implement CRUD and SPA interface with htmx"
    git push origin main
    ```

-----

### ☁️ Fase 7: Deploy em Produção

É hora de colocar nossa aplicação no ar\!

#### 7.1. Criando o Banco de Dados no Neon

1.  Vá para [Neon](https://neon.tech) e faça login (pode usar sua conta do GitHub).
2.  Crie um novo projeto. Dê um nome, como `controle-de-gastos-db`.
3.  Após a criação, você será levado a um painel. Na seção **Connection Details**, você encontrará as informações que precisamos.
4.  O Neon fornece uma URL de conexão completa. Guarde as seguintes partes:
      * **Host:** (ex: `ep-plain-snow-123456.us-east-2.aws.neon.tech`)
      * **Database:** (o nome do seu banco)
      * **User:** (o nome do seu usuário)
      * **Password:** (a senha que foi gerada)

#### 7.2. Fazendo o Deploy da Aplicação no Render

1.  Vá para [Render](https://render.com) e faça login (também pode usar sua conta do GitHub).

2.  No painel, clique em **New +** \> **Web Service**.

3.  Conecte seu repositório do GitHub e selecione o repositório `controle-de-gastos`.

4.  Na tela de configuração, preencha:

      * **Name:** `controle-de-gastos` (ou o que preferir).
      * **Region:** Escolha a mais próxima de você.
      * **Branch:** `main`.
      * **Runtime:** `Java`. O Render geralmente detecta isso.
      * **Build Command:** `./mvnw clean install`
      * **Start Command:** `java -jar target/controle-de-gastos-0.0.1-SNAPSHOT.jar`
      * **Instance Type:** **Free**.

5.  Agora, a parte mais importante: as **Variáveis de Ambiente**. Clique em **Advanced**.

      * Clique em **Add Environment Variable** e adicione as seguintes chaves e valores:

        | Chave | Valor |
        | :--- | :--- |
        | `SPRING_PROFILES_ACTIVE` | `prod` |
        | `DB_USERNAME` | O usuário do seu banco de dados Neon. |
        | `DB_PASSWORD` | A senha do seu banco de dados Neon. |
        | `DB_URL` | `jdbc:postgresql://<HOST_DO_NEON>/<DB_NAME_DO_NEON>?sslmode=require` |

        **Atenção:** Substitua `<HOST_DO_NEON>` e `<DB_NAME_DO_NEON>` pelos valores que você pegou do painel do Neon. A parte `?sslmode=require` é **essencial** para a conexão funcionar.


Um problema novo é que o **Render** não detecta automaticamente projetos **Java/Spring Boot**, porque ele só lista “runtimes oficiais”. Para rodar **Spring Boot** no Render, você precisa usar a opção **“Docker”** ou configurar manualmente via **Build Command + Start Command**.

---

## 🔹 Caminho 1 — Sem Docker (mais simples)

1. Vá em **New Web Service** → **Connect Repo** → selecione o repositório `controle-de-gastos`.
2. Quando Render pedir a configuração, escolha **Runtime: Docker (ou Other)** (mesmo se não aparecer Java).
3. Preencha manualmente:

   * **Name**: `controle-de-gastos`
   * **Region**: (ex. Ohio se você está no Brasil)
   * **Branch**: `main`
   * **Build Command**:

     ```bash
     ./mvnw clean install -DskipTests
     ```

     ou, se não tiver wrapper (`mvnw`):

     ```bash
     mvn clean install -DskipTests
     ```
   * **Start Command**:

     ```bash
     java -jar target/*.jar
     ```
4. Certifique-se de que o seu projeto **gera o JAR executável** (padrão do Spring Boot). No `pom.xml`, deve ter:

   ```xml
   <build>
       <plugins>
           <plugin>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-maven-plugin</artifactId>
           </plugin>
       </plugins>
   </build>
   ```

   Isso garante que o JAR gerado seja “fat jar” (`target/controle-de-gastos-0.0.1-SNAPSHOT.jar`).

---

## 🔹 Caminho 2 — Usando Docker (mais flexível)

Se quiser controlar melhor, crie um arquivo **`Dockerfile`** na raiz do projeto:

```dockerfile
# Etapa 1: build do JAR
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: imagem final
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

No Render:

* **Runtime**: Docker
* Ele vai detectar o `Dockerfile` automaticamente e construir sua imagem.

---

## 🔹 Conferindo variáveis de ambiente

No painel do Render → **Environment**:

* Configure variáveis como `SPRING_PROFILES_ACTIVE=prod` ou credenciais do banco (`DATABASE_URL`, `JDBC_URL`, etc).
* Se estiver usando PostgreSQL do Render, ele te dá os dados prontos (host, user, password).

---

👉 Resumindo:

* O Render **não tem runtime Java pronto**, mas você pode **forçar** com `Build Command` + `Start Command` ou criar um **Dockerfile**.
* O jeito mais fácil é **Build Command = mvn package** e **Start Command = java -jar target/\*.jar**.

---




6.  Role para baixo e clique em **Create Web Service**.

O Render irá buscar seu código do GitHub, construir a aplicação e iniciá-la. O primeiro deploy pode levar alguns minutos. Você pode acompanhar o progresso nos logs.

Quando terminar, o Render fornecerá uma URL pública (ex: `https://controle-de-gastos.onrender.com`), e sua aplicação estará **ao vivo\!** 🎉

-----

### ✅ Conclusão

Parabéns\! Você construiu do zero e implantou uma aplicação web completa com uma stack moderna. Você aprendeu a combinar o poder do Spring Boot no backend com a simplicidade e reatividade do htmx no frontend, criando uma experiência de usuário ágil sem escrever JavaScript complexo.

**Próximos Passos Sugeridos:**

  * Adicionar validação nos campos do formulário.
  * Implementar a funcionalidade de **edição** de um lançamento.
  * Melhorar o CSS e a aparência geral da aplicação.
  * Adicionar um sistema de usuários e autenticação com Spring Security.

Espero que este guia tenha sido útil e didático. Agora você tem uma base sólida para criar muitas outras aplicações incríveis\!

---

## Apêndices

Em **Java**, tanto **classe** quanto **enum** são tipos definidos pelo programador, mas têm papéis bem diferentes:

---

## 🔹 Classe

* **Definição**: É um molde para criar objetos (instâncias).
* **Características**:

  * Pode ter **atributos** (variáveis de instância).
  * Pode ter **métodos** (comportamentos).
  * Pode ser instanciada com `new`.
  * Pode herdar de outra classe (`extends`) e implementar interfaces (`implements`).
  * Exemplo:

    ```java
    public class Pessoa {
        private String nome;
        private int idade;

        public Pessoa(String nome, int idade) {
            this.nome = nome;
            this.idade = idade;
        }

        public void apresentar() {
            System.out.println("Olá, meu nome é " + nome);
        }
    }

    Pessoa p = new Pessoa("Ricardo", 35);
    p.apresentar();
    ```

---

## 🔹 Enum

* **Definição**: Um tipo especial usado para representar um **conjunto fixo e finito de constantes**.
* **Características**:

  * É uma classe especial, mas **não pode ser instanciada com `new`**.
  * Cada valor definido no `enum` é implicitamente um objeto único e imutável.
  * Pode ter atributos e métodos, mas o uso típico é representar opções pré-definidas.
  * Sempre herda implicitamente de `java.lang.Enum`, logo não pode herdar de outra classe.
  * Exemplo:

    ```java
    public enum DiaSemana {
        SEGUNDA, TERCA, QUARTA, QUINTA, SEXTA, SABADO, DOMINGO;
    }

    DiaSemana hoje = DiaSemana.SEGUNDA;

    if (hoje == DiaSemana.SEGUNDA) {
        System.out.println("Início da semana!");
    }
    ```

---

## 🔹 Diferenças principais

| Aspecto            | Classe                                                       | Enum                                                      |
| ------------------ | ------------------------------------------------------------ | --------------------------------------------------------- |
| Instanciação       | Pode criar várias instâncias com `new`.                      | Instâncias são fixas, definidas no código.                |
| Herança            | Pode herdar de outra classe e implementar interfaces.        | Sempre herda de `Enum`, só pode implementar interfaces.   |
| Uso                | Representar entidades do mundo real ou abstrações.           | Representar **conjuntos fixos de valores constantes**.    |
| Mutabilidade       | Objetos podem ser mutáveis (se atributos não forem `final`). | Constantes do enum são **imutáveis e únicas**.            |
| Criação de objetos | Feita dinamicamente em tempo de execução.                    | Criados estaticamente em tempo de carregamento da classe. |

---

👉 Resumindo:

* Use **classe** para modelar objetos que podem variar (Pessoa, Produto, Conta, etc).
* Use **enum** para modelar um conjunto **limitado e fixo** de valores (Dias da semana, Estados de um pedido, Cores, etc).

---

## 🔹 Interface

* **Definição**: Um **contrato** que define **métodos** (assinaturas) que uma classe deve implementar.
* **Características**:

  * Não pode ser instanciada.
  * Não possui estado (variáveis de instância normais). Pode ter apenas **constantes** (`public static final`).
  * Métodos definidos **não têm implementação** (até Java 7).

    * A partir do Java 8, podem ter **métodos default** (com implementação) e **métodos static**.
    * A partir do Java 9, podem ter **métodos privados** (para reuso interno da interface).
  * Uma classe pode **implementar várias interfaces** (polimorfismo múltiplo).
  * Usada para **abstração** e **contratos de comportamento**.

---

### Exemplo simples:

```java
// Interface
public interface Animal {
    void emitirSom();  // método abstrato (sem corpo)
}

// Classe que implementa a interface
public class Cachorro implements Animal {
    @Override
    public void emitirSom() {
        System.out.println("Au Au");
    }
}

public class Gato implements Animal {
    @Override
    public void emitirSom() {
        System.out.println("Miau");
    }
}

// Uso
Animal a1 = new Cachorro();
Animal a2 = new Gato();

a1.emitirSom(); // Au Au
a2.emitirSom(); // Miau
```

---

## 🔹 Diferenças principais (Classe × Enum × Interface)

| Aspecto            | Classe                                                        | Enum                                                 | Interface                                                                             |
| ------------------ | ------------------------------------------------------------- | ---------------------------------------------------- | ------------------------------------------------------------------------------------- |
| Instanciação       | Pode criar objetos com `new`.                                 | Não pode instanciar, só usar valores fixos.          | Não pode instanciar.                                                                  |
| Herança            | Pode herdar **1 classe** e implementar **muitas interfaces**. | Já herda de `Enum`, pode só implementar interfaces.  | Não herda, só pode estender outras interfaces.                                        |
| Estado (atributos) | Pode ter atributos variáveis e métodos com corpo.             | Pode ter atributos e métodos, mas valores são fixos. | Só constantes (`public static final`).                                                |
| Métodos            | Podem ter corpo, estáticos e construtores.                    | Podem ter corpo e até construtores (privados).       | Só assinatura; desde Java 8, pode ter `default`, `static`, e desde Java 9, `private`. |
| Uso típico         | Modelar objetos do mundo real.                                | Representar conjunto fixo de constantes.             | Definir contrato de comportamento.                                                    |

---

👉 Em resumo:

* **Classe** = define **como algo é** (atributos + métodos).
* **Enum** = define **um conjunto limitado de valores** (tipos fixos).
* **Interface** = define **o que algo faz** (contrato de comportamento).

---


👌 **Organização de pacotes em projetos Java**.

Quando criamos pacotes como `controller`, `model`, `repository`, normalmente estamos aplicando o **padrão de projeto MVC (Model-View-Controller)** — muito comum em aplicações web com **Spring Boot**.

---

## 🔹 1. **Model**

* Representa as **entidades do domínio** (os dados do sistema).
* Contém **atributos** e às vezes regras de negócio básicas.
* Geralmente são mapeados para tabelas do banco de dados usando **JPA/Hibernate** (`@Entity`).
* Exemplo:

  ```java
  package org.brothers.todolist.model;

  import jakarta.persistence.Entity;
  import jakarta.persistence.GeneratedValue;
  import jakarta.persistence.GenerationType;
  import jakarta.persistence.Id;

  @Entity
  public class Tarefa {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      private String descricao;
      private boolean concluida;

      // getters e setters
  }
  ```

---

## 🔹 2. **Repository**

* Responsável pela **acesso aos dados** (persistência).
* Em projetos Spring, usamos **interfaces** que estendem `JpaRepository` ou `CrudRepository`.
* O Spring gera automaticamente a implementação.
* Exemplo:

  ```java
  package org.brothers.todolist.repository;

  import org.brothers.todolist.model.Tarefa;
  import org.springframework.data.jpa.repository.JpaRepository;

  public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
      // podemos criar métodos de consulta customizados, ex:
      // List<Tarefa> findByConcluida(boolean concluida);
  }
  ```

---

## 🔹 3. **Controller**

* Camada que recebe as **requisições do usuário** (HTTP).
* Define os **endpoints da API REST** com `@RestController` e `@RequestMapping`.
* Usa o `Repository` ou um `Service` para manipular os dados e devolver respostas.
* Exemplo:

  ```java
  package org.brothers.todolist.controller;

  import org.brothers.todolist.model.Tarefa;
  import org.brothers.todolist.repository.TarefaRepository;
  import org.springframework.web.bind.annotation.*;

  import java.util.List;

  @RestController
  @RequestMapping("/tarefas")
  public class TarefaController {

      private final TarefaRepository repository;

      public TarefaController(TarefaRepository repository) {
          this.repository = repository;
      }

      @GetMapping
      public List<Tarefa> listar() {
          return repository.findAll();
      }

      @PostMapping
      public Tarefa adicionar(@RequestBody Tarefa tarefa) {
          return repository.save(tarefa);
      }

      @PutMapping("/{id}")
      public Tarefa atualizar(@PathVariable Long id, @RequestBody Tarefa tarefa) {
          tarefa.setId(id);
          return repository.save(tarefa);
      }

      @DeleteMapping("/{id}")
      public void remover(@PathVariable Long id) {
          repository.deleteById(id);
      }
  }
  ```

---

## 🔹 Estrutura típica do projeto (Spring Boot, padrão MVC)

```
todolist/
 └── src/main/java/org/brothers/todolist/
     ├── controller/    → controladores REST (recebem requisições)
     ├── model/         → classes de domínio (entidades JPA)
     ├── repository/    → interfaces de persistência (DAO/JPA)
     ├── service/       → (opcional) regras de negócio
     └── TodolistApplication.java (classe principal)
```

---

👉 Resumindo:

* **Model** → define os **dados** (entidades).
* **Repository** → faz a **ponte com o banco de dados**.
* **Controller** → recebe a **requisição do usuário** e devolve a resposta.

---

👌 **Thymeleaf** e **htmx**, que aparecem quando queremos lidar com a **View** em aplicações Java (na camada de **apresentação** do MVC).

---

## 🔹 Thymeleaf

* **O que é**: Um **template engine** para Java, muito usado em aplicações **Spring Boot**.
* **Para que serve**:

  * Permite misturar **HTML + expressões dinâmicas** (como `${objeto.atributo}`) renderizadas no **servidor**.
  * Gera páginas HTML completas antes de enviar para o navegador.
* **Características**:

  * Sintaxe parecida com HTML puro, mas com atributos especiais (`th:text`, `th:if`, `th:each` etc).
  * Substitui JSP em projetos modernos com Spring.
  * Integra bem com `Spring MVC` (controladores podem passar modelos de dados para as views).

### Exemplo com Thymeleaf

**Controller:**

```java
@Controller
public class TarefaController {
    private final TarefaRepository repository;

    public TarefaController(TarefaRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/tarefas")
    public String listar(Model model) {
        model.addAttribute("tarefas", repository.findAll());
        return "tarefas"; // aponta para templates/tarefas.html
    }
}
```

**Template Thymeleaf (`templates/tarefas.html`):**

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Tarefas</title>
</head>
<body>
    <h1>Lista de Tarefas</h1>
    <ul>
        <li th:each="t : ${tarefas}" th:text="${t.descricao}"></li>
    </ul>
</body>
</html>
```

✅ O servidor gera o HTML pronto antes de enviar ao navegador.

---

## 🔹 htmx

* **O que é**: Uma **biblioteca JavaScript leve** que adiciona **interatividade ao HTML** sem precisar escrever muito JS ou usar frameworks como React/Angular.
* **Para que serve**:

  * Permite atualizar **partes da página** via **requisições AJAX**, **WebSockets** ou **SSE**.
  * Fica na camada do **cliente** (navegador).
* **Características**:

  * Usa atributos `hx-*` no HTML (`hx-get`, `hx-post`, `hx-target`, etc).
  * Facilita criar páginas dinâmicas sem precisar recarregar tudo.
  * Pode ser usado junto com Thymeleaf (server-side render) para melhorar a experiência.

### Exemplo com htmx

**HTML com htmx:**

```html
<h1>Lista de Tarefas</h1>

<!-- Botão que faz um POST para criar tarefa -->
<form hx-post="/tarefas" hx-target="#lista-tarefas" hx-swap="outerHTML">
    <input type="text" name="descricao" placeholder="Nova tarefa">
    <button type="submit">Adicionar</button>
</form>

<!-- Lista que será atualizada dinamicamente -->
<div id="lista-tarefas" hx-get="/tarefas" hx-trigger="load">
    <!-- O servidor devolve um fragmento HTML aqui -->
</div>

<script src="https://unpkg.com/htmx.org@1.9.10"></script>
```

✅ O navegador envia requisições para o servidor e **atualiza só um trecho do HTML**, sem recarregar a página toda.

---

## 🔹 Comparação (Thymeleaf × htmx)

| Aspecto              | Thymeleaf                                 | htmx                                                  |
| -------------------- | ----------------------------------------- | ----------------------------------------------------- |
| Onde atua            | Servidor (renderiza HTML antes de enviar) | Cliente (navegador, atualiza partes da página)        |
| Tipo de renderização | **Server-side rendering (SSR)**           | **Client-side enhancement (AJAX/HTML over the wire)** |
| Uso típico           | Gerar páginas HTML completas              | Atualizar dinamicamente partes da página              |
| Integração           | Muito usado com Spring MVC                | Pode ser usado junto com Thymeleaf ou até sem ele     |
| Complexidade         | Simples para quem já conhece Spring       | Mais leve que React/Angular, mas poderoso             |

---

👉 Resumindo:

* **Thymeleaf** gera o HTML completo no **servidor** (ótimo para SEO e páginas dinâmicas clássicas).
* **htmx** permite **atualizações parciais** no **cliente** sem precisar recarregar toda a página.

---
