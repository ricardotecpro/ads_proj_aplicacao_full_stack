# Aula 11 - Refresh Token e Segurança Avançada 🏗️
## Blindando sua API contra o mundo

---

## Agenda 📅

1. O Problema do Token Curto ⏰ <!-- .element: class="fragment" -->
2. Refresh Tokens (O que são?) <!-- .element: class="fragment" -->
3. CORS: Origens e Destinos <!-- .element: class="fragment" -->
4. Helmet: Headers de Aço <!-- .element: class="fragment" -->
5. Rate Limit: Contra Brute Force <!-- .element: class="fragment" -->
6. Ataques Comuns (XSS, Injection) <!-- .element: class="fragment" -->

---

## 1. Por que Tokens Expiram? ⏰

- Segurança! Se roubarem o token, ele dura pouco. <!-- .element: class="fragment" -->
- **Problema**: O usuário odeia fazer login toda hora. <!-- .element: class="fragment" -->

---

## 2. Refresh Token 🔁

- Um token de longa duração (7 dias+). <!-- .element: class="fragment" -->
- Serve apenas para trocar por um novo Access Token. <!-- .element: class="fragment" -->
- Deve ser invalidado se o usuário deslogar. <!-- .element: class="fragment" -->

---

## 3. CORS: Cross-Origin Resource Sharing 🌍

- "Quem pode me chamar?". <!-- .element: class="fragment" -->
- Resolvido via Headers no Servidor. <!-- .element: class="fragment" -->
- **Nunca** use `origin: '*'` em ambientes reais! <!-- .element: class="fragment" -->

---

## 4. Helmet: Proteção de Headers 🪖

- Remove o `X-Powered-By` (não diz que é Express). <!-- .element: class="fragment" -->
- Adiciona proteção contra Clickjacking e XSS. <!-- .element: class="fragment" -->

---

## 5. Rate Limiting 🔨

- 5 tentativas de login por minuto? Sim. <!-- .element: class="fragment" -->
- Evita que robôs tentem descobrir senhas via "força bruta". <!-- .element: class="fragment" -->

---

## 6. Onde salvar os Tokens? 🛡️

- **Frontend**: LocalStorage? Seguro? <!-- .element: class="fragment" -->
- **Melhor Prática**: Cookies `HttpOnly` + `Secure`. <!-- .element: class="fragment" -->

---

## 7. Melhores Práticas de Segurança 🏆

1. Use HTTPS sempre. <!-- .element: class="fragment" -->
2. Valide TODAS as entradas do usuário. <!-- .element: class="fragment" -->
3. Mantenha as bibliotecas atualizadas. <!-- .element: class="fragment" -->

---

## Desafio de Segurança ⚡

Qual a diferença entre 401 e 403 no contexto de Refresh Tokens? Se eu recebo 401, eu tento o refresh ou deslogo o usuário?

---

## Resumo ✅

- Refresh Token equilibra UX e Segurança. <!-- .element: class="fragment" -->
- CORS e Helmet são as portas do seu castelo. <!-- .element: class="fragment" -->
- Proteja-se contra robôs com Rate Limit. <!-- .element: class="fragment" -->

---

## Próximo Módulo: Front-End Moderno 🎨

### Saindo das APIs e indo para a Web!

- Introdução ao React/Vite. <!-- .element: class="fragment" -->
- Consumindo nossas APIs no navegador. <!-- .element: class="fragment" -->

---

## Dúvidas? 🏗️
