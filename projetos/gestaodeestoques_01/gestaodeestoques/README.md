    # 💎 Gestão de Estoques

---
Excelente pergunta. Como especialista em engenharia de software e educador, a forma como estruturamos o conhecimento é tão importante quanto o próprio conhecimento. Para transformar o processo completo de construção da nossa aplicação em um guia didático eficaz para alunos, a melhor abordagem é dividir o conteúdo em módulos bem definidos, onde cada um representa um marco de aprendizado claro e constrói sobre o anterior.

Considerando a complexidade e a variedade de conceitos que abordamos (do banco de dados ao JWT e ao frontend), a separação ideal seria em **8 módulos**.

Esta divisão permite que os alunos foquem em um conjunto coeso de habilidades por vez, desde a fundação do backend até a criação da interface do usuário e o deploy, facilitando a absorção e a prática.

---

## 📚 Estrutura Modular Proposta para o Guia Didático

Aqui está o detalhamento de como os 8 módulos podem ser organizados para maximizar o aprendizado.

### Módulo 1: 🏛️ Fundamentos e Persistência de Dados
**Objetivo:** Construir a base da aplicação. Ao final, o aluno terá uma aplicação que se conecta a um banco de dados e entende a estrutura dos dados, mas ainda sem lógica de negócio ou interface.
-   **Aula 1.1:** Gênese do Projeto (Configuração no Spring Initializr).
-   **Aula 1.2:** Modelagem do Banco de Dados (Criação do `schema.sql` e Diagrama ER).
-   **Aula 1.3:** Mapeamento Objeto-Relacional (Criação das entidades `record` no pacote `model`).
-   **Aula 1.4:** A Camada de Acesso a Dados (Implementação das interfaces `Repository` com Spring Data).

### Módulo 2: 🧠 Lógica de Negócio e Arquitetura de Serviços
**Objetivo:** Isolar as regras de negócio do resto do código, aplicando princípios de design de software (SOLID). O aluno aprenderá a criar o "cérebro" da aplicação.
-   **Aula 2.1:** A Importância da Camada de Serviço (Introdução ao Princípio da Responsabilidade Única - SRP).
-   **Aula 2.2:** Desacoplando as Camadas com DTOs (Data Transfer Objects) e Mappers.
-   **Aula 2.3:** Programando para Interfaces (Definição da `ProdutoService` e o Princípio da Inversão de Dependência - DIP).
-   **Aula 2.4:** Implementando a Lógica de Negócio (Criação da classe `ProdutoServiceImpl`).

### Módulo 3: 🔌 Construindo a API REST
**Objetivo:** Expor a lógica de negócio para o mundo exterior através de endpoints HTTP. Ao final, o aluno terá uma API funcional, porém ainda insegura.
-   **Aula 3.1:** Introdução a APIs REST e `@RestController`.
-   **Aula 3.2:** Mapeando Verbos HTTP (GET, POST, PUT, DELETE) para operações de CRUD.
-   **Aula 3.3:** Manipulando Respostas HTTP com `ResponseEntity` (Status codes: 200, 201, 404, etc.).
-   **Aula 3.4:** Implementando os `Controllers` para Produtos, Categorias e Fornecedores.

### Módulo 4: 🔒 Segurança de APIs com Spring Security e JWT
**Objetivo:** Proteger a API. Este é um módulo denso e crucial que ensina o padrão de mercado para segurança em arquiteturas desacopladas.
-   **Aula 4.1:** Teoria: Autenticação Stateless vs. Stateful e o Fluxo JWT (com Diagrama de Sequência).
-   **Aula 4.2:** Implementando o `JwtTokenProvider` (Geração e validação dos tokens).
-   **Aula 4.3:** O Filtro de Autenticação (Implementação do `JwtAuthenticationFilter`).
-   **Aula 4.4:** Refatorando o `SecurityConfig` para uma API Stateless.
-   **Aula 4.5:** Criando o Endpoint de Login (`AuthenticationController`).

### Módulo 5:  professionalism professionalizando a API: Documentação e Erros
**Objetivo:** Transformar a API funcional em uma API profissional, fácil de usar e previsível.
-   **Aula 5.1:** Documentação Interativa com OpenAPI (`springdoc-openapi`).
-   **Aula 5.2:** Enriquecendo a Documentação do Swagger (Anotações `@Operation`, `@ApiResponse`, etc.).
-   **Aula 5.3:** A Importância de Respostas de Erro Padronizadas (Criando um `ErrorResponseDTO`).
-   **Aula 5.4:** Tratamento de Exceções Global com `@RestControllerAdvice`.

### Módulo 6: ✅ Garantia de Qualidade com Testes
**Objetivo:** Ensinar o aluno a validar a própria lógica e garantir que futuras alterações não quebrem o código existente.
-   **Aula 6.1:** Fundamentos de Testes (JUnit 5 e o conceito de Mocks com Mockito).
-   **Aula 6.2:** Testes Unitários para a Camada de Serviço (`ProdutoServiceImplTest`).
-   **Aula 6.3:** (Avançado) Testes de Integração para a Camada de Controller (`@WebMvcTest`).

### Módulo 7: 🎨 Introdução ao Frontend SPA (Single Page Application)
**Objetivo:** Mostrar como o backend robusto que foi construído pode ser consumido por um cliente moderno. Este módulo foca nos conceitos, usando JavaScript puro ("vanilla").
-   **Aula 7.1:** O que é uma SPA? (Comparação com a abordagem de Thymeleaf).
-   **Aula 7.2:** Estruturando o Frontend (Arquivos `index.html`, `login.html`).
-   **Aula 7.3:** Consumindo a API (Usando `fetch` e o módulo `api.js`).
-   **Aula 7.4:** Gerenciando Autenticação no Cliente (`auth.js` e `localStorage` para o JWT).
-   **Aula 7.5:** Roteamento no Lado do Cliente (`router.js`).

### Módulo 8: 🚀 Deployment e Containerização com Docker
**Objetivo:** Ensinar o aluno a empacotar a aplicação para distribuição e execução em qualquer ambiente.
-   **Aula 8.1:** O que é Docker e por que usar containers?
-   **Aula 8.2:** Criando um `Dockerfile` para a aplicação Spring Boot.
-   **Aula 8.3:** (Avançado) Orquestrando a aplicação com um banco de dados PostgreSQL usando `docker-compose`.

---

### Tabela Resumo da Sequência Didática

| Módulo | Título                                               | Conceitos-Chave                                       | Resultado Esperado                                               | Próxima Etapa            |
| :----: | :--------------------------------------------------- | :---------------------------------------------------- | :--------------------------------------------------------------- | :----------------------- |
| **1** | Fundamentos e Persistência de Dados                  | Spring Initializr, JDBC, SQL, Entidades, Repositories | Uma aplicação com camada de dados funcional.                     | Lógica de Negócio        |
| **2** | Lógica de Negócio e Arquitetura de Serviços          | SOLID, Camada de Serviço, DTOs, Mappers               | Lógica de negócio isolada e organizada.                          | Expor como API           |
| **3** | Construindo a API REST                               | `@RestController`, `ResponseEntity`, Verbos HTTP      | Endpoints de CRUD funcionais, mas inseguros.                     | Segurança                |
| **4** | Segurança de APIs com Spring Security e JWT          | Stateless, JWT, Filtros de Segurança, `UserDetailsService` | API com autenticação e autorização robustas.                     | Profissionalização     |
| **5** | Profissionalizando a API: Documentação e Erros       | OpenAPI/Swagger, `@ControllerAdvice`, DTOs de Erro    | API auto-documentada e com tratamento de erros padronizado.      | Testes                   |
| **6** | Garantia de Qualidade com Testes                     | Testes Unitários/Integração, JUnit 5, Mockito         | Lógica de negócio validada por uma suíte de testes.              | Interface do Usuário     |
| **7** | Introdução ao Frontend SPA                           | SPA, `fetch`, `localStorage`, Roteamento no Cliente   | Um protótipo de frontend que consome a API.                      | Deploy                   |
| **8** | Deployment e Containerização com Docker              | Containers, Dockerfile, `docker-compose`              | Aplicação empacotada e pronta para ser executada em qualquer lugar. | Fim do Curso             |

Esta estrutura de **8 módulos** garante uma progressão lógica, abordando teoria e prática em doses equilibradas, o que é ideal para um ambiente de sala de aula.
