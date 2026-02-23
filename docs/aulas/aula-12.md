# Aula 12 - Introdução ao Frontend Moderno (React)

!!! tip "Objetivo"
    Entender o que são SPAs e o React.

## 1. O que é uma SPA? 📄

Antigamente, cada clique em um site fazia a página "piscar" e recarregar tudo do zero (HTML, CSS, JS). Nas **Single Page Applications (SPAs)**:
*   A página carrega apenas uma vez.
*   Quando você clica em algo, apenas o conteúdo necessário é trocado (via Javascript).
*   É muito mais rápido e parece um aplicativo de celular.

### Arquitetura SPA (Mermaid)

```mermaid
graph LR
    Client([Browser/SPA]) -- Requisição JSON --> API([Backend/API])
    API -- Resposta JSON --> Client
    Client -- Manipula DOM --> UI([Interface Dinâmica])
```

## 2. Por que React?
Conteúdo aqui.

## 3. Vite
Conteúdo aqui.

## 4. Componentes
Conteúdo aqui.

## 5. Props
Conteúdo aqui.
