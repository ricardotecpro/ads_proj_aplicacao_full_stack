# Plano de Implementação

## Objetivos
Refatorar a aplicação "Lista de Tarefas 08", modernizando o backend com Spring Boot e migrando o frontend para React.

## Mudanças Propostas

### Backend
- **Configuração**: Separar `application.properties` em `application-dev.properties`, `application-prod.properties`, etc.
- **Código**: Remover acentos e caracteres especiais (ex: 'ç') de nomes de variáveis, métodos e comentários.
- **Arquitetura**: Reforçar o padrão MVC. Revisar Services e Controllers.
- **Qualidade**: Aplicar Clean Code e SOLID.

### Frontend
- **Migração**: Substituir o projeto Angular existente por um novo projeto React.
- **Estrutura**: Criar estrutura de componentes, hooks e services.
- **Integração**: Conectar com a API do backend.

## Plano de Verificação
- Compilar o backend com Maven.
- Executar testes unitários (se houver) ou criar novos.
- Iniciar o servidor Spring Boot.
- Iniciar o servidor de desenvolvimento React.
- Testar o fluxo completo de CRUD de tarefas.

### Mobile (Futuro)
- **Tecnologia**: React Native com Expo.
- **Justificativa**: O Expo facilita a configuração do ambiente, permitindo testar no próprio celular sem precisar instalar o Android Studio completo inicialmente.
- **Estratégia**:
    - Reutilizar a lógica de serviço (`api.js` e `TaskService.js`) do frontend React, adaptando apenas a URL base (para IP da máquina local).
    - Criar telas equivalentes (`HomeScreen`, `TaskFormScreen`).
    - Usar componentes nativos (`View`, `Text`, `FlatList`, `TouchableOpacity`).
