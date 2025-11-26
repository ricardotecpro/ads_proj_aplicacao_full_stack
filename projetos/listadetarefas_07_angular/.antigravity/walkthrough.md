# Walkthrough - Refatoração Lista de Tarefas 07

## Visão Geral
Este documento resume as alterações realizadas para refatorar o projeto "Lista de Tarefas 07", modernizando o backend e migrando o frontend para Angular.

## Alterações Realizadas

### Backend (Spring Boot)
- **Limpeza de Código**: Remoção de acentos e caracteres especiais para evitar problemas de encoding.
- **Arquitetura**: Refatoração para seguir estritamente o padrão MVC e princípios SOLID.
- **Perfis**: Separação de configurações em `application.properties` (comum), `application-dev.properties` (H2) e `application-test.properties`.
- **CRUD Completo**: Implementação de todos os verbos HTTP (GET, POST, PUT, PATCH, DELETE) com DTOs.
- **Testes**: Adição de testes unitários (`TarefaServiceTest`) e de integração (`TarefaControllerTest`).

### Frontend (Angular)
- **Migração**: Substituição completa do frontend Vue.js por uma nova aplicação **Angular 18+**.
- **Standalone Components**: Utilização da abordagem moderna sem `NgModule`.
- **Funcionalidades**:
    - Listagem de tarefas.
    - Criação de tarefas.
    - Edição de tarefas (título).
    - Conclusão de tarefas (checkbox).
    - Exclusão de tarefas.
- **Testes**: Implementação de testes unitários para componentes e serviços.

### Documentação
- **README.md**: Reescrito como um guia didático passo a passo, ideal para uso em sala de aula.

## Validação

### Testes Automatizados
- **Backend**: `mvn test` executado com sucesso (Passou em todos os testes).
- **Frontend**: `ng test` executado com sucesso (10 testes passando).

### Verificação Manual
A aplicação foi estruturada para rodar com:
1.  **Backend**: `mvn spring-boot:run "-Dspring.profiles.active=dev"` (Porta 8088).
2.  **Frontend**: `ng serve` (Porta 4200).

## Próximos Passos Sugeridos
- Implementar persistência real com PostgreSQL (perfil `prod`).
- Adicionar autenticação com Spring Security (JWT).
- Melhorar a interface com Angular Material (atualmente usando CSS puro para simplicidade didática).
