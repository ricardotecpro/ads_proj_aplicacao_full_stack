const appContent = document.getElementById('app-content');

// ===================================================================================
// FUNÇÕES DE RENDERIZAÇÃO DE PÁGINA (VIEWS)
// ===================================================================================

/** Renderiza a view de Produtos (Lista) */
const renderProdutos = async () => {
    try {
        const produtos = await api.getProdutos();
        const tableRows = produtos.map(p => `
            <tr>
                <td>${p.id}</td>
                <td><strong>${p.nome}</strong></td>
                <td>${p.quantidade}</td>
                <td>R$ ${p.preco.toFixed(2)}</td>
                <td><span class="badge badge-primary rounded-pill">${p.nomeCategoria}</span></td>
                <td>
                    <a href="#/produtos/editar/${p.id}" class="btn btn-sm btn-warning btn-floating" title="Editar"><i class="fas fa-pencil-alt"></i></a>
                    <button class="btn btn-sm btn-danger btn-floating" data-id="${p.id}" data-action="delete-produto" title="Excluir"><i class="fas fa-trash"></i></button>
                </td>
            </tr>`).join('');
        appContent.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h1 class="h2">Produtos em Estoque</h1>
                <a href="#/produtos/novo" class="btn btn-primary btn-rounded"><i class="fas fa-plus me-2"></i>Adicionar Produto</a>
            </div>
            <div class="card"><div class="card-body">
                <table class="table align-middle mb-0 bg-white table-hover">
                    <thead class="bg-light"><tr><th>ID</th><th>Nome</th><th>Qtd.</th><th>Preço</th><th>Categoria</th><th>Ações</th></tr></thead>
                    <tbody>${tableRows.length > 0 ? tableRows : '<tr><td colspan="6" class="text-center">Nenhum produto encontrado.</td></tr>'}</tbody>
                </table>
            </div></div>`;
    } catch (error) { appContent.innerHTML = `<div class="alert alert-danger">Erro ao carregar produtos. Verifique o console.</div>`; }
};

/** Renderiza a view de Categorias (Lista e Formulário de Criar/Editar) */
const renderCategorias = async (params) => {
    try {
        const id = params ? params.id : null;
        const isEditMode = id !== null;
        const [categorias, categoriaParaEditar] = await Promise.all([
            api.getCategorias(),
            isEditMode ? api.getCategoriaById(id) : Promise.resolve(null)
        ]);
        const listItems = categorias.map(c => `
            <li class="list-group-item d-flex justify-content-between align-items-center">
                <span>${c.nome}</span>
                <div>
                    <a href="#/categorias/editar/${c.id}" class="btn btn-sm btn-warning btn-floating" title="Editar"><i class="fas fa-pencil-alt"></i></a>
                    <button class="btn btn-sm btn-danger btn-floating" data-id="${c.id}" data-action="delete-categoria" title="Excluir"><i class="fas fa-trash"></i></button>
                </div>
            </li>`).join('');
        appContent.innerHTML = `
            <h1 class="h2 mb-4">Gerenciar Categorias</h1>
            <div class="row">
                <div class="col-md-7"><div class="card"><div class="card-header">Categorias Existentes</div><div class="card-body">
                    <ul class="list-group list-group-light">${listItems.length > 0 ? listItems : '<li class="list-group-item">Nenhuma categoria encontrada.</li>'}</ul>
                </div></div></div>
                <div class="col-md-5"><div class="card">
                    <div class="card-header bg-dark text-white">${isEditMode ? `Editar Categoria (ID: ${id})` : 'Adicionar Nova Categoria'}</div>
                    <div class="card-body">
                        <form id="categoria-form" data-id="${id || ''}">
                            <div class="form-outline mb-4" data-mdb-input-init>
                                <input type="text" id="nome" name="nome" class="form-control" value="${categoriaParaEditar ? categoriaParaEditar.nome : ''}" required />
                                <label class="form-label" for="nome">Nome da Categoria</label>
                            </div>
                            <div class="float-end">
                               <a href="#/categorias" class="btn btn-light">Cancelar</a>
                               <button type="submit" class="btn btn-primary">${isEditMode ? 'Salvar Alterações' : 'Salvar'}</button>
                            </div>
                        </form>
                    </div>
                </div></div>
            </div>`;
    } catch (error) { appContent.innerHTML = `<div class="alert alert-danger">Erro ao carregar categorias.</div>`; }
};

// ... (as outras funções de renderização completas para Fornecedores, Usuários, Papéis e Formulário de Produto)

// ===================================================================================
// ROTEADOR E GERENCIADORES DE EVENTOS
// ===================================================================================

const routes = {
    '/produtos': renderProdutos,
    '/produtos/novo': () => renderProdutoForm(null),
    '/produtos/editar/:id': renderProdutoForm,
    '/categorias': () => renderCategorias(null),
    '/categorias/editar/:id': renderCategorias,
    '/fornecedores': () => renderFornecedores(null),
    '/fornecedores/editar/:id': renderFornecedores,
    '/usuarios': renderUsuarios,
    '/papeis': renderPapeis,
};

const router = async () => {
    if (!auth.isLoggedIn()) { window.location.pathname = '/login.html'; return; }
    const path = window.location.hash.substring(1) || '/produtos';
    let match = null, params = {};
    for (const route in routes) {
        const routeRegex = new RegExp(`^${route.replace(/:\w+/g, '([\\w-]+)')}$`);
        const pathMatch = path.match(routeRegex);
        if (pathMatch) {
            match = route;
            const paramNames = (route.match(/:\w+/g) || []).map(name => name.substring(1));
            paramNames.forEach((name, i) => params[name] = pathMatch[i + 1]);
            break;
        }
    }
    const renderFunction = match ? () => routes[match](params) : () => { appContent.innerHTML = '<h2>404 - Página Não Encontrada</h2>'; };
    await renderFunction();
    document.querySelectorAll('[data-mdb-input-init]').forEach(input => new mdb.Input(input).init());
};

window.addEventListener('load', router);
window.addEventListener('hashchange', router);

// ... (código completo dos listeners de 'click' e 'submit' para todas as entidades)