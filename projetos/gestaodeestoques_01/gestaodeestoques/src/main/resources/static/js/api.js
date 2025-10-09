const api = {
    fetch: async (endpoint, method = 'GET', body = null) => {
        const token = auth.getToken();
        const headers = { 'Content-Type': 'application/json' };
        if (token) headers['Authorization'] = `Bearer ${token}`;

        const config = { method, headers };
        if (body) config.body = JSON.stringify(body);

        const response = await fetch(`/api${endpoint}`, config);

        if (response.status === 401 || response.status === 403) {
            auth.handleLogout();
            throw new Error('Sessão inválida. Por favor, faça o login novamente.');
        }
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || `Erro na API: ${response.statusText}`);
        }
        return response.status === 204 ? {} : response.json();
    },
    // Produtos
    getProdutos: () => api.fetch('/produtos'),
    getProdutoById: (id) => api.fetch(`/produtos/${id}`),
    createProduto: (data) => api.fetch('/produtos', 'POST', data),
    updateProduto: (id, data) => api.fetch(`/produtos/${id}`, 'PUT', data),
    deleteProduto: (id) => api.fetch(`/produtos/${id}`, 'DELETE'),
    // Categorias
    getCategorias: () => api.fetch('/categorias'),
    getCategoriaById: (id) => api.fetch(`/categorias/${id}`),
    createCategoria: (data) => api.fetch('/categorias', 'POST', data),
    updateCategoria: (id, data) => api.fetch(`/categorias/${id}`, 'PUT', data),
    deleteCategoria: (id) => api.fetch(`/categorias/${id}`, 'DELETE'),
    // Fornecedores
    getFornecedores: () => api.fetch('/fornecedores'),
    getFornecedorById: (id) => api.fetch(`/fornecedores/${id}`),
    createFornecedor: (data) => api.fetch('/fornecedores', 'POST', data),
    updateFornecedor: (id, data) => api.fetch(`/fornecedores/${id}`, 'PUT', data),
    deleteFornecedor: (id) => api.fetch(`/fornecedores/${id}`, 'DELETE'),
    // Admin
    getUsuarios: () => api.fetch('/usuarios'),
    getUsuarioById: (id) => api.fetch(`/usuarios/${id}`),
    createUsuario: (data) => api.fetch('/usuarios', 'POST', data),
    updateUsuario: (id, data) => api.fetch(`/usuarios/${id}`, 'PUT', data),
    deleteUsuario: (id) => api.fetch(`/usuarios/${id}`, 'DELETE'),
    getPapeis: () => api.fetch('/papeis'),
};