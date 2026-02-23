Neste guia, vamos focar nos conceitos fundamentais de uma **Single Page Application (SPA)**, construindo uma interface de usuário do zero com **JavaScript puro ("vanilla")**. Esta abordagem, embora mais verbosa que usar um framework como React ou Vue, é a melhor forma de aprender os mecanismos que operam por baixo do capô.

# 💎 Guia Didático Definitivo: Gestão de Estoques com Spring Boot

-----

## Módulo 7: 🎨 Introdução ao Frontend SPA (Single Page Application)

**Objetivo:** Mostrar como o backend robusto que foi construído pode ser consumido por um cliente web moderno. Ao final deste módulo, o aluno terá uma SPA funcional que realiza login, exibe dados e interage com a API, tudo sem recarregar a página.

-----

### \#\#\# Aula 7.1: O que é uma SPA? (Comparação com a abordagem de Thymeleaf)

**Conceito-Chave: A Mudança de Paradigma**
Até agora, poderíamos ter usado o **Thymeleaf**, uma abordagem de **Renderização no Lado do Servidor (Server-Side Rendering - SSR)**.

  - **Fluxo SSR (Thymeleaf):**
    1.  O navegador pede uma página (ex: `/produtos`).
    2.  O **servidor Spring Boot** busca os dados no banco, processa um template HTML, injeta os dados no HTML e envia uma **página HTML completa** de volta.
    3.  Cada clique em um link repete todo o processo.
  - **Analogia SSR:** Pedir um "prato feito" completo em um restaurante.

Agora, vamos adotar a abordagem de **Renderização no Lado do Cliente (Client-Side Rendering - CSR)**, a base de uma **SPA**.

  - **Fluxo CSR (SPA):**
    1.  O navegador pede a aplicação **uma única vez**. O servidor envia uma "casca" HTML quase vazia e vários arquivos JavaScript.
    2.  O **JavaScript**, executando no navegador do cliente, é quem pede os **dados puros (JSON)** à nossa API.
    3.  O JavaScript então usa esses dados para construir e renderizar o HTML dinamicamente, atualizando apenas as partes necessárias da página.
  - **Analogia CSR:** Ter uma cozinha (o JavaScript) e pedir apenas os ingredientes crus (o JSON) ao fornecedor (a API) para montar seus próprios pratos.

| Característica        | SSR (Thymeleaf)                               | CSR (Single Page Application)                       |
| :-------------------- | :-------------------------------------------- | :-------------------------------------------------- |
| **Carga Inicial** | Mais rápida para a primeira visualização.     | Mais lenta (baixa todo o app), mas instantânea depois. |
| **Navegação** | Lenta, recarrega a página a cada clique.      | Rápida e fluida, sem recarregamento da página.        |
| **Carga no Servidor** | Maior (precisa renderizar HTML a cada vez).   | Menor (serve apenas JSON e arquivos estáticos).     |
| **Arquitetura** | Acoplada (Frontend e Backend no mesmo processo). | Desacoplada (Backend é uma API, Frontend é um cliente). |

-----

### \#\#\# Aula 7.2: Estruturando o Frontend

Nossa SPA viverá inteiramente no diretório `src/main/resources/static/`. O próprio Spring Boot servirá esses arquivos.

**Ação:** Crie os arquivos HTML e uma folha de estilos básica.

#### Código: `static/login.html`

```html
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Gestão de Estoque</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/7.2.0/mdb.min.css" rel="stylesheet"/>
    <link href="/css/style.css" rel="stylesheet" />
</head>
<body>
    <div class="container">
        <div class="row justify-content-center align-items-center vh-100">
            <div class="col-md-6 col-lg-4">
                <div class="card shadow-5-strong">
                    <div class="card-header text-center bg-dark text-white p-4">
                        <h2 class="h4 mb-1">Gestão de Estoque</h2>
                    </div>
                    <div class="card-body p-4">
                        <div id="error-message" class="alert alert-danger d-none" role="alert"></div>
                        <form id="login-form">
                            <div class="form-outline mb-4" data-mdb-input-init>
                                <input type="text" id="username" class="form-control" required />
                                <label class="form-label" for="username">Usuário (admin)</label>
                            </div>
                            <div class="form-outline mb-4" data-mdb-input-init>
                                <input type="password" id="password" class="form-control" required />
                                <label class="form-label" for="password">Senha (admin123)</label>
                            </div>
                            <button type="submit" class="btn btn-primary btn-block mb-4" data-mdb-ripple-init>Entrar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/7.2.0/mdb.umd.min.js"></script>
    <script src="/js/auth.js"></script>
</body>
</html>
```

#### Código: `static/index.html` (A "casca" da nossa SPA)

```html
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestão de Estoque</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet"/>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/7.2.0/mdb.min.css" rel="stylesheet"/>
    <link href="/css/style.css" rel="stylesheet"/>
</head>
<body>
    <header>
        <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
            <div class="container-fluid">
                <a class="navbar-brand" href="#/produtos"><i class="fas fa-boxes-stacked me-2"></i>Gestão de Estoque</a>
                <ul class="navbar-nav d-flex flex-row">
                    <li class="nav-item me-3">
                        <a class="nav-link" href="#/categorias">Categorias</a>
                    </li>
                    <li class="nav-item me-3">
                        <a class="nav-link" href="#/fornecedores">Fornecedores</a>
                    </li>
                    <li class="nav-item">
                        <button id="logout-button" class="btn btn-outline-danger btn-rounded">Sair</button>
                    </li>
                </ul>
            </div>
        </nav>
    </header>

    <main id="app-content" class="container my-5">
        </main>

    <script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/mdb-ui-kit/7.2.0/mdb.umd.min.js"></script>
    <script src="/js/auth.js"></script>
    <script src="/js/api.js"></script>
    <script src="/js/router.js"></script>
</body>
</html>
```

#### Código: `static/css/style.css`

```css
/* Estilos globais para a aplicação */
.table-hover tbody tr:hover {
    cursor: pointer;
    background-color: rgba(0, 0, 0, 0.075);
}
```

-----

### \#\#\# Aula 7.3: Gerenciando Autenticação no Cliente (`auth.js`)

**Conceito-Chave:** O `localStorage` é um mecanismo do navegador que nos permite salvar dados (como o token JWT) que persistem mesmo se o navegador for fechado.

**Ação:** Crie o arquivo `js/auth.js` para centralizar toda a lógica de autenticação do lado do cliente.

#### Código: `static/js/auth.js`

```javascript
// Este objeto encapsula toda a lógica de autenticação
const auth = {
    // Salva o token no localStorage
    saveToken: (token) => {
        localStorage.setItem('jwt_token', token);
    },
    // Pega o token do localStorage
    getToken: () => {
        return localStorage.getItem('jwt_token');
    },
    // Remove o token para fazer logout
    removeToken: () => {
        localStorage.removeItem('jwt_token');
    },
    // Verifica se existe um token
    isLoggedIn: () => {
        const token = auth.getToken();
        return !!token; // Retorna true se o token existir, false caso contrário
    },
    // Lida com o processo de login
    handleLogin: async (username, password) => {
        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ login: username, password: password })
            });

            if (!response.ok) {
                throw new Error('Usuário ou senha inválidos.');
            }

            const data = await response.json();
            auth.saveToken(data.token); // Salva o token recebido
            window.location.href = '/index.html'; // Redireciona para a página principal
        } catch (error) {
            const errorDiv = document.getElementById('error-message');
            errorDiv.textContent = error.message;
            errorDiv.classList.remove('d-none');
        }
    },
    // Lida com o processo de logout
    handleLogout: () => {
        auth.removeToken();
        window.location.href = '/login.html'; // Redireciona para a página de login
    }
};

// Adiciona o listener de evento APENAS se estivermos na página de login
if (window.location.pathname.endsWith('login.html')) {
    document.getElementById('login-form').addEventListener('submit', (event) => {
        event.preventDefault(); // Impede o envio padrão do formulário
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        auth.handleLogin(username, password);
    });
}

// Adiciona o listener de evento APENAS se estivermos na página principal
if (window.location.pathname.endsWith('index.html')) {
    document.getElementById('logout-button').addEventListener('click', auth.handleLogout);
}
```

-----

### \#\#\# Aula 7.4: Consumindo a API (`api.js`)

**Conceito-Chave:** Centralizar as chamadas de API em um único módulo (`api.js`) é uma boa prática. Isso nos permite adicionar lógica comum a todas as requisições, como incluir o token de autorização.

**Ação:** Crie o arquivo `js/api.js` que fará as chamadas `fetch` para nosso backend.

#### Código: `static/js/api.js`

```javascript
// Módulo para centralizar todas as chamadas à API
const api = {
    // Função auxiliar genérica para requisições
    fetch: async (endpoint, method = 'GET', body = null) => {
        const token = auth.getToken();
        const headers = {
            'Content-Type': 'application/json'
        };

        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            method: method,
            headers: headers
        };

        if (body) {
            config.body = JSON.stringify(body);
        }

        const response = await fetch(`/api${endpoint}`, config);

        // Se o token for inválido/expirado, o servidor retornará 403.
        // Devemos deslogar o usuário.
        if (response.status === 403 || response.status === 401) {
            auth.handleLogout();
            return;
        }

        if (!response.ok) {
            throw new Error(`Erro na API: ${response.statusText}`);
        }
        
        // Retorna um JSON vazio para respostas 204 No Content
        if (response.status === 204) {
            return {};
        }

        return response.json();
    },

    // Funções específicas para cada recurso
    getProdutos: () => api.fetch('/produtos'),
    deleteProduto: (id) => api.fetch(`/produtos/${id}`, 'DELETE'),
    
    getCategorias: () => api.fetch('/categorias'),
    // ... adicione outras funções conforme necessário (getFornecedores, createProduto, etc.)
};
```

-----

### \#\#\# Aula 7.5: Roteamento no Lado do Cliente (`router.js`)

**Conceito-Chave:** Um roteador de cliente observa a URL e, em vez de pedir uma nova página ao servidor, ele decide qual conteúdo JavaScript deve renderizar na tela. Usaremos uma técnica simples baseada no "hash" (`#`) da URL.

**Ação:** Crie o arquivo `js/router.js` para controlar qual "página" é exibida.

#### Código: `static/js/router.js`

```javascript
const appContent = document.getElementById('app-content');

// Templates HTML para cada "página"
const renderProdutos = async () => {
    try {
        const produtos = await api.getProdutos();
        const tableRows = produtos.map(p => `
            <tr>
                <td>${p.id}</td>
                <td>${p.nome}</td>
                <td>${p.quantidade}</td>
                <td>R$ ${p.preco.toFixed(2)}</td>
                <td><span class="badge badge-primary rounded-pill d-inline">${p.nomeCategoria}</span></td>
                <td>
                    <button class="btn btn-sm btn-danger btn-floating" data-id="${p.id}" data-action="delete">
                        <i class="fas fa-trash"></i>
                    </button>
                </td>
            </tr>
        `).join('');

        return `
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h1 class="h2">Produtos em Estoque</h1>
                <a href="#/produtos/novo" class="btn btn-primary btn-rounded"><i class="fas fa-plus me-2"></i>Adicionar Produto</a>
            </div>
            <div class="card"><div class="card-body">
                <table class="table align-middle mb-0 bg-white table-hover">
                    <thead class="bg-light">
                        <tr><th>ID</th><th>Nome</th><th>Qtd.</th><th>Preço</th><th>Categoria</th><th>Ações</th></tr>
                    </thead>
                    <tbody>${tableRows}</tbody>
                </table>
            </div></div>
        `;
    } catch (error) {
        return `<div class="alert alert-danger">Erro ao carregar produtos.</div>`;
    }
};

const renderCategorias = async () => {
    // Lógica similar para buscar e renderizar categorias
    return '<h1>Página de Categorias (a implementar)</h1>';
};

// Mapa de rotas
const routes = {
    '/produtos': renderProdutos,
    '/categorias': renderCategorias,
    // Adicione outras rotas aqui
};

// Função principal do roteador
const router = async () => {
    // Proteção de rota: se não estiver logado, manda para o login.
    if (!auth.isLoggedIn()) {
        window.location.pathname = '/login.html';
        return;
    }

    // Pega o caminho do hash ou vai para a página padrão
    const path = window.location.hash.substring(1) || '/produtos';
    const renderFunction = routes[path] || (() => '<h2>Página não encontrada</h2>');
    
    appContent.innerHTML = await renderFunction();
};

// O roteador é acionado quando a página carrega ou o hash da URL muda
window.addEventListener('load', router);
window.addEventListener('hashchange', router);

// Delegação de eventos para botões de ação (ex: deletar)
appContent.addEventListener('click', async (event) => {
    const target = event.target.closest('[data-action="delete"]');
    if (target) {
        const id = target.dataset.id;
        if (confirm(`Tem certeza que deseja deletar o produto ID ${id}?`)) {
            try {
                await api.deleteProduto(id);
                alert('Produto deletado com sucesso!');
                router(); // Recarrega a view atual
            } catch (error) {
                alert('Erro ao deletar produto. Você tem permissão de ADMIN?');
            }
        }
    }
});
```

-----

### Conclusão do Módulo 7

**Parabéns\!** Você construiu uma Single Page Application funcional do zero. Embora simples, ela demonstra todos os conceitos fundamentais:

  - Separação total entre frontend e backend.
  - Consumo de uma API REST segura com JWT.
  - Gerenciamento de estado de autenticação no cliente.
  - Renderização dinâmica de conteúdo e roteamento sem recarregar a página.

Este conhecimento é a base para o uso de frameworks modernos como **React, Vue e Angular**, que resolvem muitos desses problemas (roteamento, renderização, gerenciamento de estado) de forma mais eficiente e escalável.

No **próximo e último módulo**, aprenderemos a empacotar nossa aplicação backend com **Docker**, preparando-a para o deploy em qualquer ambiente.
