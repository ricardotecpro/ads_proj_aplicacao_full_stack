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