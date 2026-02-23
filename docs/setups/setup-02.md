# Setup Frontend SPA 🎨

O desenvolvimento de Single Page Applications (SPA) exige um ambiente focado em ferramentas de build e gerenciadores de pacotes.

## 1. Node.js e NPM/Yarn 📦
O ecossistema frontend moderno roda sobre o Node.js.
- **NPM**: Vem instalado com o Node.js.
- **Yarn**: Alternativa popular (`npm install --global yarn`).
- **Verificação**: `node -v` e `npm -v`.

## 2. Frameworks e Bibliotecas 🧱
Neste curso, exploramos os conceitos base que se aplicam a:
- **React**: `npx create-react-app meu-app`
- **Vue.js**: `npm create vue@latest`
- **Angular**: `npm install -g @angular/cli`

## 3. Extensões do VS Code para Frontend
- **ES7+ React/Redux/React-Native snippets** (ou equivalentes para Vue).
- **ESLint**: Para manter o código limpo e padronizado.
- **Tailwind CSS IntelliSense** (se estiver usando Tailwind).

## 4. Debugging no Navegador 🏎️
- **Chrome DevTools**: F12 ou Ctrl+Shift+I.
- **React Developer Tools**: Extensão para Chrome/Firefox.
- **Redux DevTools**: Essencial para gerenciamento de estado complexo.

## 5. Mocking de API (Opcional) 🎭
Caso queira desenvolver o frontend antes do backend estar pronto:
- **JSON Server**: `npm install -g json-server`
- **MSW (Mock Service Worker)**: Para interceptação de chamadas de rede.

---

!!! info "Dica de Performance"
    Utilize o **Vite** para criar seus projetos frontend. Ele é extremamente rápido e moderno:
    ```bash
    npm create vite@latest
    ```
