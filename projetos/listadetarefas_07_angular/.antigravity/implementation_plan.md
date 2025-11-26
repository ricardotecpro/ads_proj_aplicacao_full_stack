# Plano de Implementação - Refatoração Lista de Tarefas 07

## Descrição do Objetivo
Refatorar o projeto existente "Lista de Tarefas 07" para atender aos padrões modernos de engenharia de software. Isso inclui limpar o backend Spring Boot (SOLID, MVC, Perfis), implementar testes automatizados robustos e migrar completamente o frontend de Vue.js para uma aplicação Angular Standalone moderna com CRUD completo.

## Revisão do Usuário Necessária
> [!IMPORTANT]
> **Migração do Frontend**: O frontend existente será **deletado** e substituído por uma nova aplicação **Angular 20+**.
> **Testes**: Serão adicionados testes unitários e de integração para garantir a qualidade do código.

## Alterações Propostas

### Backend (`backend`)

#### [MODIFICAR] Configuração
- Dividir `application.properties` em perfis (dev, prod, test).
- Remover acentos de comentários e propriedades.

#### [MODIFICAR] Código Fonte (`br.com.tarefas.api`)
- **Classe Principal**: Renomear para `ListaTarefasApplication`.
- **Controller**: Refatorar `TarefaController` para usar DTOs e garantir verbos HTTP corretos.
- **Service**: Isolamento de regras de negócio em `TarefaService`.
- **CRUD Completo**:
    - `POST /tarefas`: Criar tarefa.
    - `GET /tarefas`: Listar todas.
    - `GET /tarefas/{id}`: Buscar por ID (Novo).
    - `PUT /tarefas/{id}`: Atualizar tarefa completa (Novo).
    - `PATCH /tarefas/{id}/status`: Atualizar status.
    - `DELETE /tarefas/{id}`: Deletar tarefa.

#### [NOVO] Testes Automatizados
- **Unitários**: `TarefaServiceTest` usando JUnit 5 e Mockito.
- **Integração**: `TarefaControllerTest` usando `MockMvc` para validar endpoints.

### Frontend (`frontend`)

#### [DELETAR] App Vue Existente
- Remover todos os arquivos em `frontend/`.

#### [NOVO] App Angular
- Gerar aplicação Angular 18+ (Standalone).
- **Componentes**:
    - `TarefaListComponent`: Listagem e ações de deletar/concluir.
    - `TarefaFormComponent`: Criar e Editar tarefas.
- **Serviços**:
    - `TarefaService`: Comunicação com API.
- **Testes**:
    - Testes unitários para componentes e serviços (`.spec.ts`).

### Documentação

#### [MODIFICAR] `README.md`
- **Guia Didático**: Reescrever como um material de apoio para professores e alunos iniciantes.
- **Conteúdo**:
    - Introdução ao projeto e tecnologias.
    - Pré-requisitos (Java, Node, Maven).
    - Passo a passo da estrutura do código (Backend e Frontend).
    - Como rodar e testar a aplicação.
    - Explicação dos conceitos aplicados (MVC, SOLID, REST).

## Plano de Verificação

### Testes Automatizados
- **Backend**: Executar `mvn test`. Esperado: Todos os testes passarem (Service e Controller).
- **Frontend**: Executar `ng test --watch=false`. Esperado: Todos os testes de componentes passarem.

### Verificação Manual
1.  **Fluxo Completo**:
    - Criar tarefa "Teste 1".
    - Editar título para "Teste 1 Editado".
    - Marcar como concluída.
    - Deletar tarefa.
    - Verificar persistência no banco (H2 console ou reload da página).
