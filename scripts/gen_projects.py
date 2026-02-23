import os

projects = [
    ("01", "Cinto de Utilidades", "Configurar o ambiente de desenvolvimento backend (VS Code, Docker, Postman)."),
    ("02", "Desenho de Arquitetura", "Criar um diagrama de microsserviços para um sistema de e-commerce usando Mermaid."),
    ("03", "Design de Endpoints", "Desenhar os contratos de uma API de catálogo de produtos."),
    ("04", "Documentação Swagger", "Criar uma especificação OpenAPI básica para o serviço de produtos."),
    ("05", "CRUD de Produtos", "Implementar as rotas básicas de Create, Read, Update e Delete."),
    ("06", "Integração com Banco", "Conectar sua API a um banco de dados PostgreSQL usando Docker."),
    ("07", "Testes Unitários com Jest", "Escrever testes para a lógica de negócio do serviço de produtos."),
    ("08", "Testes de Integração", "Validar a comunicação entre a API e o Banco de Dados."),
    ("09", "Autenticação JWT", "Implementar o fluxo de login e geração de tokens."),
    ("10", "Middleware de Segurança", "Proteger rotas sensíveis verificando a validade do JWT."),
    ("11", "Controle RBAC", "Implementar diferentes níveis de acesso (Admin vs Usuário)."),
    ("12", "Setup da SPA", "Iniciar um projeto React ou Vue utilizando o Vite."),
    ("13", "Componentização UI", "Criar componentes reutilizáveis para a listagem de produtos."),
    ("14", "Consumo de APIs", "Integrar o frontend com o backend usando Fetch ou Axios."),
    ("15", "Roteamento Dinâmico", "Configurar rotas protegidas no frontend para a área administrativa."),
]

base_path = r"d:\SourceCode\REPOS\github.io\ads_proj_aplicacao_full_stack\docs\projetos"

for num, title, goal in projects:
    filename = f"projeto-{num}.md"
    content = f"""# Projeto {num} - {title} 🚀

## Objetivo
{goal}

## Atividades
1. Siga as instruções da Aula {num}.
2. Implemente a funcionalidade proposta.
3. Valide o funcionamento básico.

---
!!! tip "Dica"
    Não esqueça de versionar seu código no Git!
"""
    with open(os.path.join(base_path, filename), "w", encoding="utf-8") as f:
        f.write(content)

print("Placeholders de projetos criados com sucesso!")
