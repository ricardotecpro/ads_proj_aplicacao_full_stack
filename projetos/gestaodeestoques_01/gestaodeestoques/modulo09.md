# 💎 Guia Didático: Gestão de Estoques com Spring Boot

-----

## Módulo 7 (Continuação): 🎨 Completando o CRUD da Single Page Application

**Objetivo:** Aplicar os padrões de consumo de API e renderização dinâmica que aprendemos para Produtos, implementando agora o CRUD completo (Criar, Ler, Deletar) para as páginas de **Categorias** e **Fornecedores**.

-----

### Pré-requisito: API Backend Pronta

Este guia assume que seu backend Spring Boot já possui os `Controllers`, `Services`, `DTOs` e `Mappers` para `Categoria` e `Fornecedor`, expondo os seguintes endpoints:

  - `GET /api/categorias`, `POST /api/categorias`, `DELETE /api/categorias/{id}`
  - `GET /api/fornecedores`, `POST /api/fornecedores`, `DELETE /api/fornecedores/{id}`

Nosso foco será 100% no código frontend que vive em `src/main/resources/static/js/`.

-----

### \#\#\# Aula 7.6: Expandindo o Cliente de API (`api.js`)

**Conceito-Chave:** Nosso arquivo `api.js` é o nosso "conector" universal com o backend. Antes de criar as telas, precisamos ensinar a ele como se comunicar com os novos endpoints de categorias e fornecedores.

**Ação:** Substitua o conteúdo do seu arquivo `api.js` por esta versão completa, que agora inclui métodos para todas as nossas entidades.

#### Código Completo: `static/js/api.js`

```javascript
// Módulo para centralizar todas as chamadas à API
const api = {
    /**
     * Função auxiliar genérica para realizar requisições à API.
     * Ela automaticamente adiciona o token JWT e trata erros comuns.
     */
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

        if (response.status === 401 || response.status === 403) {
            auth.handleLogout(); // Se o token for inválido ou expirado, desloga o usuário.
            return;
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || `Erro na API: ${response.statusText}`);
        }
        
        if (response.status === 204) { // No Content
            return {};
        }

        return response.json();
    },

    // --- PRODUTOS ---
    getProdutos: () => api.fetch('/produtos'),
    createProduto: (produtoData) => api.fetch('/produtos', 'POST', produtoData),
    deleteProduto: (id) => api.fetch(`/produtos/${id}`, 'DELETE'),

    // --- CATEGORIAS ---
    getCategorias: () => api.fetch('/categorias'),
    createCategoria: (categoriaData) => api.fetch('/categorias', 'POST', categoriaData),
    deleteCategoria: (id) => api.fetch(`/categorias/${id}`, 'DELETE'),

    // --- FORNECEDORES ---
    getFornecedores: () => api.fetch('/fornecedores'),
    createFornecedor: (fornecedorData) => api.fetch('/fornecedores', 'POST', fornecedorData),
    deleteFornecedor: (id) => api.fetch(`/fornecedores/${id}`, 'DELETE'),
};
```

-----

### \#\#\# Aula 7.7: Implementando o CRUD de Categorias e Fornecedores

**Conceito-Chave:** A beleza de uma arquitetura baseada em componentes (mesmo em JS puro) é a **reutilização de padrões**. A lógica para buscar, exibir e deletar `Categorias` será muito similar à de `Produtos`, o que reforça o aprendizado.

**Ação:** Agora, vamos substituir as funções de renderização provisórias em `router.js` pelas implementações completas, e adicionar a lógica para lidar com os novos formulários.

#### Código Completo: `static/js/router.js`

```javascript
const appContent = document.getElementById('app-content');

// ===================================================================================
// FUNÇÕES DE RENDERIZAÇÃO DE PÁGINA
// ===================================================================================

/**
 * Renderiza a página de Produtos.
 */
const renderProdutos = async () => {
    try {
        const produtos = await api.getProdutos();
        const tableRows = produtos.map(p => `
            <tr>
                <td>${p.id}</td>
                <td><strong>${p.nome}</strong></td>
                <td>${p.quantidade}</td>
                <td>R$ ${p.preco.toFixed(2)}</td>
                <td><span class="badge badge-primary rounded-pill d-inline">${p.nomeCategoria}</span></td>
                <td>
                    <button class="btn btn-sm btn-danger btn-floating" data-id="${p.id}" data-action="delete-produto" title="Excluir"><i class="fas fa-trash"></i></button>
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
                    <thead class="bg-light"><tr><th>ID</th><th>Nome</th><th>Qtd.</th><th>Preço</th><th>Categoria</th><th>Ações</th></tr></thead>
                    <tbody>${tableRows}</tbody>
                </table>
            </div></div>`;
    } catch (error) { return `<div class="alert alert-danger">Erro ao carregar produtos.</div>`; }
};

/**
 * Renderiza a página de Categorias.
 */
const renderCategorias = async () => {
    try {
        const categorias = await api.getCategorias();
        const listItems = categorias.map(c => `
            <li class="list-group-item d-flex justify-content-between align-items-center">
                <span>${c.nome} (ID: ${c.id})</span>
                <button class="btn btn-sm btn-danger btn-floating" data-id="${c.id}" data-action="delete-categoria" title="Excluir"><i class="fas fa-trash"></i></button>
            </li>
        `).join('');
        return `
            <h1 class="h2 mb-4">Gerenciar Categorias</h1>
            <div class="row">
                <div class="col-md-7">
                    <div class="card"><div class="card-body">
                        <ul class="list-group list-group-light">${listItems}</ul>
                    </div></div>
                </div>
                <div class="col-md-5">
                    <div class="card">
                        <div class="card-header bg-dark text-white">Adicionar Nova Categoria</div>
                        <div class="card-body">
                            <form id="categoria-form">
                                <div class="form-outline mb-4" data-mdb-input-init>
                                    <input type="text" id="nome" name="nome" class="form-control" required />
                                    <label class="form-label" for="nome">Nome da Categoria</label>
                                </div>
                                <button type="submit" class="btn btn-primary btn-block" data-mdb-ripple-init>Salvar</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>`;
    } catch (error) { return `<div class="alert alert-danger">Erro ao carregar categorias.</div>`; }
};

/**
 * Renderiza a página de Fornecedores.
 */
const renderFornecedores = async () => {
    try {
        const fornecedores = await api.getFornecedores();
        const tableRows = fornecedores.map(f => `
            <tr>
                <td>${f.id}</td>
                <td>${f.nome}</td>
                <td>${f.cnpj}</td>
                <td><button class="btn btn-sm btn-danger btn-floating" data-id="${f.id}" data-action="delete-fornecedor" title="Excluir"><i class="fas fa-trash"></i></button></td>
            </tr>
        `).join('');
        return `
            <h1 class="h2 mb-4">Gerenciar Fornecedores</h1>
            <div class="row">
                <div class="col-md-7">
                    <div class="card"><div class="card-body">
                        <table class="table align-middle mb-0 bg-white">
                            <thead class="bg-light"><tr><th>ID</th><th>Nome</th><th>CNPJ</th><th>Ações</th></tr></thead>
                            <tbody>${tableRows}</tbody>
                        </table>
                    </div></div>
                </div>
                <div class="col-md-5">
                    <div class="card">
                        <div class="card-header bg-dark text-white">Adicionar Novo Fornecedor</div>
                        <div class="card-body">
                            <form id="fornecedor-form">
                                <div class="form-outline mb-4" data-mdb-input-init>
                                    <input type="text" id="nome" name="nome" class="form-control" required />
                                    <label class="form-label" for="nome">Nome do Fornecedor</label>
                                </div>
                                <div class="form-outline mb-4" data-mdb-input-init>
                                    <input type="text" id="cnpj" name="cnpj" class="form-control" required pattern="\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}" />
                                    <label class="form-label" for="cnpj">CNPJ (xx.xxx.xxx/xxxx-xx)</label>
                                </div>
                                <button type="submit" class="btn btn-primary btn-block" data-mdb-ripple-init>Salvar</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>`;
    } catch (error) { return `<div class="alert alert-danger">Erro ao carregar fornecedores.</div>`; }
};

/**
 * Renderiza o formulário de produto (sem alterações, mas incluído para completude).
 */
const renderProdutoForm = async () => {
    const [categorias, fornecedores] = await Promise.all([api.getCategorias(), api.getFornecedores()]);
    const categoriaOptions = categorias.map(c => `<option value="${c.id}">${c.nome}</option>`).join('');
    const fornecedorOptions = fornecedores.map(f => `<option value="${f.id}">${f.nome}</option>`).join('');
    return `
        <div class="row justify-content-center"><div class="col-md-8"><div class="card">
            <div class="card-header bg-dark text-white"><h2 class="h4">Novo Produto</h2></div>
            <div class="card-body">
                <form id="produto-form">
                    <div class="form-outline mb-4" data-mdb-input-init><input type="text" id="nome" name="nome" class="form-control" required /><label class="form-label" for="nome">Nome do Produto</label></div>
                    <div class="row mb-4"><div class="col-md-6"><div class="form-outline" data-mdb-input-init><input type="number" id="quantidade" name="quantidade" class="form-control" required /><label class="form-label" for="quantidade">Quantidade</label></div></div><div class="col-md-6"><div class="form-outline" data-mdb-input-init><input type="number" step="0.01" id="preco" name="preco" class="form-control" required /><label class="form-label" for="preco">Preço</label></div></div></div>
                    <select class="form-select mb-4" name="categoria_id" required><option value="" disabled selected>Selecione a Categoria</option>${categoriaOptions}</select>
                    <select class="form-select mb-4" name="fornecedor_id" required><option value="" disabled selected>Selecione o Fornecedor</option>${fornecedorOptions}</select>
                    <div class="float-end"><a href="#/produtos" class="btn btn-light">Cancelar</a><button type="submit" class="btn btn-primary">Salvar</button></div>
                </form>
            </div>
        </div></div></div>`;
};


// ===================================================================================
// ROTEADOR E GERENCIADORES DE EVENTOS
// ===================================================================================

const routes = {
    '/produtos': renderProdutos,
    '/produtos/novo': renderProdutoForm,
    '/categorias': renderCategorias,
    '/fornecedores': renderFornecedores
};

const router = async () => {
    if (!auth.isLoggedIn()) {
        window.location.pathname = '/login.html';
        return;
    }
    const path = window.location.hash.substring(1) || '/produtos';
    const renderFunction = routes[path] || (() => '<h2>Página não encontrada</h2>');
    appContent.innerHTML = await renderFunction();
    document.querySelectorAll('[data-mdb-input-init]').forEach((input) => { new mdb.Input(input).init(); });
};

window.addEventListener('load', router);
window.addEventListener('hashchange', router);

// Gerenciador de eventos centralizado para Ações (deletar)
appContent.addEventListener('click', async (event) => {
    const target = event.target.closest('button[data-action]');
    if (!target) return;

    const id = target.dataset.id;
    const action = target.dataset.action;

    if (confirm(`Tem certeza que deseja deletar o item ID ${id}?`)) {
        try {
            switch (action) {
                case 'delete-produto': await api.deleteProduto(id); break;
                case 'delete-categoria': await api.deleteCategoria(id); break;
                case 'delete-fornecedor': await api.deleteFornecedor(id); break;
            }
            alert('Item deletado com sucesso!');
            router(); // Recarrega a view atual
        } catch (error) {
            alert(`Erro ao deletar: ${error.message}. Você tem permissão de ADMIN?`);
        }
    }
});

// Gerenciador de eventos centralizado para Forms (criar)
appContent.addEventListener('submit', async (event) => {
    event.preventDefault();
    const form = event.target;
    const formData = new FormData(form);
    const data = Object.fromEntries(formData.entries());

    try {
        let successMessage = '';
        if (form.id === 'produto-form') {
            // Converte os campos para os tipos corretos
            data.quantidade = parseInt(data.quantidade);
            data.preco = parseFloat(data.preco);
            data.categoria_id = parseInt(data.categoria_id);
            data.fornecedor_id = parseInt(data.fornecedor_id);
            await api.createProduto(data);
            successMessage = 'Produto criado com sucesso!';
            window.location.hash = '#/produtos';
        } else if (form.id === 'categoria-form') {
            await api.createCategoria(data);
            successMessage = 'Categoria criada com sucesso!';
            router(); // Recarrega a página de categorias
        } else if (form.id === 'fornecedor-form') {
            await api.createFornecedor(data);
            successMessage = 'Fornecedor criado com sucesso!';
            router(); // Recarrega a página de fornecedores
        }
        if(successMessage) alert(successMessage);

    } catch (error) {
        alert(`Erro ao salvar: ${error.message}. Verifique os dados e suas permissões.`);
    }
});
```

-----

### Conclusão da Implementação

**Parabéns\!** Com as atualizações nos arquivos `api.js` e `router.js`, sua Single Page Application agora possui funcionalidades de Criar, Ler e Deletar para todas as entidades principais. Você aplicou com sucesso o mesmo padrão arquitetural em diferentes contextos, o que é uma etapa fundamental para a maestria do desenvolvimento de software.

**Próximo Desafio (Para o Aluno):** A funcionalidade de **Editar** ainda não foi implementada. O desafio agora é:

1.  Adicionar uma rota no `router.js` para `/#/produtos/editar/:id`.
2.  Criar uma função `renderEditProdutoForm(id)` que busca os dados de um produto específico com `api.getProdutoById(id)`.
3.  Preencher o formulário com os dados retornados.
4.  No `submit` do formulário, chamar a função `api.updateProduto(id, data)`.
