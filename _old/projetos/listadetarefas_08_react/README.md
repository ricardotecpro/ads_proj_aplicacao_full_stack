# 📝 Lista de Tarefas 08 - Guia Completo Full Stack (Spring Boot + React)

Bem-vindo ao guia definitivo para desenvolver uma aplicação web moderna utilizando **Spring Boot** no backend e **React** no frontend. Este projeto foi desenhado para ser didático, robusto e seguir as melhores práticas de engenharia de software.

## 🎯 Objetivos do Projeto

- **Backend Robusto**: API RESTful com Spring Boot 3 e Java 21.
- **Frontend Moderno**: SPA (Single Page Application) com React e Vite.
- **Boas Práticas**: Clean Code, SOLID, MVC.
- **Banco de Dados**: H2 (Dev) e PostgreSQL (Prod).
- **Segurança**: Preparado para Spring Security (CORS configurado).

---

## 🛠️ Stack Tecnológica

### Backend (API)
- **Java 21**: Linguagem moderna e performática.
- **Spring Boot 3**: Framework líder para desenvolvimento Java.
- **Spring Data JPA**: Abstração para persistência de dados.
- **H2 Database**: Banco em memória para testes rápidos.
- **PostgreSQL**: Banco relacional robusto para produção.
- **Lombok**: Redução de código boilerplate.

### Frontend (Web)
- **React**: Biblioteca JS para interfaces de usuário.
- **Vite**: Build tool ultra-rápida.
- **Axios**: Cliente HTTP para consumir a API.
- **React Router DOM**: Navegação SPA.
- **CSS Moderno**: Variáveis CSS, Flexbox, Grid e Design Responsivo.

---

## 🚀 Guia de Configuração e Execução

### Pré-requisitos
- **Java JDK 21** instalado.
- **Node.js** (versão 18 ou superior) instalado.
- **Maven** (opcional, pois usamos o `mvnw` incluído).
- **PostgreSQL** (apenas para o perfil de produção).

### 1. Configurando o Backend

O backend possui dois perfis configurados: `dev` e `production`.

1.  Navegue até a pasta `backend`:
    ```bash
    cd backend
    ```

2.  **Executar em Modo de Desenvolvimento (H2)**:
    O perfil `dev` é o padrão. Ele usa um banco de dados em memória que é recriado a cada reinicialização.
    ```bash
    ./mvnw spring-boot:run
    ```
    - A API estará disponível em: `http://localhost:8080`
    - Console H2: `http://localhost:8080/h2-console`
    - Swagger UI: `http://localhost:8080/swagger-ui.html`

3.  **Executar em Modo de Produção (PostgreSQL)**:
    Para usar o PostgreSQL, altere o perfil no arquivo `application.properties` para `production` ou passe via linha de comando:
    ```bash
    ./mvnw spring-boot:run -Dspring-boot.run.profiles=production
    ```
    *Certifique-se de ter um banco de dados chamado `liston_db` criado no seu PostgreSQL local.*

### 2. Configurando o Frontend

1.  Navegue até a pasta `frontend`:
    ```bash
    cd frontend
    ```

2.  Instale as dependências:
    ```bash
    npm install
    ```

3.  Inicie o servidor de desenvolvimento:
    ```bash
    npm run dev
    ```
    - O frontend estará disponível em: `http://localhost:5173`

---

## 📚 Estrutura do Projeto

### Backend (`/backend`)
A estrutura segue o padrão MVC (Model-View-Controller) adaptado para API REST:

- **`controller`**: Recebe as requisições HTTP (`TarefaController`).
- **`service`**: Contém a regra de negócio (`TarefaService`).
- **`repository`**: Interface de acesso ao banco de dados (`TarefaRepository`).
- **`model`**: Entidades JPA que representam as tabelas (`Tarefa`).
- **`dto`**: Objetos de transferência de dados (`TarefaRequestDTO`, `TarefaResponseDTO`).
- **`config`**: Configurações de segurança, CORS e banco de dados.

### Frontend (`/frontend`)
A estrutura segue o padrão de componentes do React:

- **`src/components`**: Componentes reutilizáveis (`Header`, `Footer`).
- **`src/pages`**: Páginas da aplicação (`HomePage`, `TaskFormPage`).
- **`src/services`**: Comunicação com a API (`api.js`, `TaskService.js`).
- **`src/assets`**: Imagens e estilos globais.

---

## 📝 Passo a Passo de Desenvolvimento (Resumo)

Se você quiser recriar este projeto do zero, siga estes passos:

1.  **Crie o Backend**:
    - Use o [Spring Initializr](https://start.spring.io/) para gerar o projeto com Web, JPA, H2, Lombok.
    - Crie a entidade `Tarefa`.
    - Crie o repositório `TarefaRepository`.
    - Crie o serviço `TarefaService` com as regras de negócio.
    - Crie o controlador `TarefaController` para expor os endpoints.
    - Configure o `application.properties`.

2.  **Crie o Frontend**:
    - Use `npm create vite@latest frontend -- --template react`.
    - Instale `axios` e `react-router-dom`.
    - Configure o `api.js` para apontar para `localhost:8080`.
    - Crie os componentes e páginas para listar e criar tarefas.
    - Estilize com CSS moderno.

3.  **Integre**:
    - Certifique-se de habilitar o **CORS** no Spring Boot (`SecurityConfig.java`) para permitir requisições do frontend (`localhost:5173`).

---

## 🤝 Contribuição

Sinta-se à vontade para fazer um fork deste projeto e enviar Pull Requests com melhorias!

---
**Desenvolvido com 💙 por [Seu Nome]**
