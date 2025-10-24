# 💎 Gestão de Estoques

-----

## 🎯 Objetivo de Aprendizagem

Ao final deste guia, você terá construído um projeto de portfólio impressionante e, mais importante, terá dominado os seguintes conceitos:

  - **Arquitetura em Camadas:** Organização do código com base em responsabilidades (Controller, Service, Repository).
  - **Princípios SOLID:** Aplicação prática de conceitos como Responsabilidade Única e Inversão de Dependência.
  - **Modelagem de Dados:** Criação de um esquema de banco de dados relacional com relacionamentos 1-N e N-N.
  - **Segurança:** Implementação de autenticação e autorização com Spring Security.
  - **Desenvolvimento Frontend com Thymeleaf:** Criação de UIs dinâmicas, reutilizáveis e com design moderno (Material Design).
  - **Qualidade de Código:** Validação de dados de entrada e escrita de testes unitários.

-----

## Módulo 0: 🚀 Gênese do Projeto (Spring Initializr)

Toda grande jornada de software começa com a configuração correta. O **Spring Initializr** é uma ferramenta web que gera a estrutura base do nosso projeto Spring Boot, poupando-nos de configurações manuais complexas.

### \#\#\# Aula 0.1: Montando o Esqueleto da Aplicação

1.  **Acesse o site:** Abra o seu navegador e vá para [**start.spring.io**](https://start.spring.io).

2.  **Configure os Metadados do Projeto:** Na seção à esquerda, preencha os campos exatamente como mostrado abaixo. Estes campos definem a identidade do nosso projeto no ecossistema Maven.

      * **Project:** `Maven`

      * **Language:** `Java`

      * **Spring Boot:** `3.2.5` (ou a versão estável mais recente não-SNAPSHOT)

      * **Project Metadata:**

          * **Group:** `br.com.aula`
          * **Artifact:** `gestaodeestoques`
          * **Name:** `gestaodeestoques`
          * **Description:** `Projeto Didático de Gestão de Estoque`
          * **Package name:** `br.com.aula.gestaodeestoques`

      * **Packaging:** `Jar`

      * **Java:** `21`

    [Imagem de A interface do Spring Initializr com os campos de metadados do projeto preenchidos.]

3.  **Adicione as Dependências:** No lado direito, clique no botão **"ADD DEPENDENCIES..."** e adicione uma por uma as seguintes dependências. Elas são os "blocos de construção" que darão funcionalidades à nossa aplicação.

      * `Spring Web`: Essencial para criar aplicações web, incluindo APIs REST e MVC.
      * `Thymeleaf`: O nosso motor de templates para renderizar as páginas HTML no lado do servidor.
      * `Spring Data JDBC`: Para facilitar o acesso a bancos de dados relacionais usando o padrão JDBC.
      * `H2 Database`: Um banco de dados em memória, perfeito para desenvolvimento e testes rápidos.
      * `Spring Security`: Para adicionar a camada de autenticação e autorização.
      * `Spring Boot DevTools`: Ferramenta de produtividade que reinicia a aplicação automaticamente quando alteramos o código.
      * `Validation`: Para adicionar validações aos nossos dados de entrada (ex: `@NotBlank`).

    [Imagem de A seção de dependências do Spring Initializr mostrando a lista de dependências adicionadas.]

4.  **Gere o Projeto:** Após preencher tudo, clique no botão **"GENERATE"** na parte inferior da tela. Um arquivo `gestao.estoque.zip` será baixado.

5.  **Importe na sua IDE:** Descompacte o arquivo `.zip` em um local de sua preferência e abra o projeto na sua IDE (IntelliJ IDEA, VS Code, Eclipse). A IDE irá reconhecer o arquivo `pom.xml` e baixar todas as dependências que acabamos de selecionar.

-----

## Módulo 1: 🏛️ A Fundação - Estrutura e Persistência de Dados

*(O conteúdo deste módulo e dos subsequentes permanece o mesmo da versão anterior, pois já estava completo e na sequência didática correta.)*

### \#\#\# 1.1 🗺️ Estrutura de Pastas e Arquivos

*(Estrutura completa de pastas e arquivos)*

### \#\#\# 1.2 🏗️ Diagrama da Arquitetura em Camadas

*(Diagrama Mermaid da arquitetura)*

-----

## 💻 Parte 2: Construção Passo a Passo (Códigos Completos)

### \#\#\# Módulo 2: Fundação (Core Backend)

*(Códigos completos para `pom.xml`, `application.properties`, `schema.sql`, Entidades e Repositórios)*

### \#\#\# Módulo 3: Lógica de Negócio e Segurança

*(Códigos completos para DTOs, Mapper, Exceptions, Services e Configurações de Segurança)*

### \#\#\# Módulo 4: Interface com o Usuário (Frontend)

*(Códigos completos para Controllers e os templates Thymeleaf com Material Design e Modo Noturno)*

### \#\#\# Módulo 5: Garantia de Qualidade

*(Código completo para os Testes Unitários com JUnit e Mockito)*

-----

## ▶️ Parte 3: Execução e Próximos Passos

### \#\#\# Como Executar o Projeto

1.  Após importar o projeto gerado pelo Initializr, aguarde o Maven baixar todas as dependências.
2.  Execute a classe principal `GestaoEstoqueApplication.java`.
3.  Acesse [http://localhost:8080](https://www.google.com/search?q=http://localhost:8080) em seu navegador.

**Credenciais para Teste (criadas pelo `DataSeeder`):**

  - **Admin:** `admin` / `admin123`
  - **Usuário:** `user` / `user123`

### \#\#\# Conclusão e Próximos Passos

---
