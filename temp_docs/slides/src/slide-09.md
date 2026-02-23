# Aula 09 - Segurança e Autenticação com JWT 🔐
## Portas trancadas e Crachás Digitais

---

## Agenda 📅

1. Autenticação vs Autorização <!-- .element: class="fragment" -->
2. O Fim das Sessões (Stateful) <!-- .element: class="fragment" -->
3. O que é JWT? <!-- .element: class="fragment" -->
4. Estrutura: Header, Payload, Signature <!-- .element: class="fragment" -->
5. Fluxo de Login completo <!-- .element: class="fragment" -->
6. Melhores Práticas de Segurança <!-- .element: class="fragment" -->

---

## 1. Quem é Você? (Autenticação) 🚦

- Validar a identidade do usuário. <!-- .element: class="fragment" -->
- Login e Senha. <!-- .element: class="fragment" -->
- **Autorização**: O que você PODE fazer? (Níveis de acesso). <!-- .element: class="fragment" -->

---

## 2. Por que JWT? 🤔

- Abordagem **Stateless** (Sem estado). <!-- .element: class="fragment" -->
- O servidor não guarda sessão na memória (escalável!). <!-- .element: class="fragment" -->
- Perfeito para Microserviços e Mobile. <!-- .element: class="fragment" -->

---

## 3. Estrutura do Token 🎫

```text
[Header].[Payload].[Signature]
```

- **Header**: Algoritmo (ex: HS256). <!-- .element: class="fragment" -->
- **Payload**: Os dados (id, role, nome). <!-- .element: class="fragment" -->
- **Signature**: O lacre de segurança. <!-- .element: class="fragment" -->

---

## 4. O Coração do JWT: A Assinatura 🖋️

- Usa uma `SECRET_KEY` no servidor. <!-- .element: class="fragment" -->
- Garante que o token não foi "hackeado" ou alterado. <!-- .element: class="fragment" -->

---

## 5. Fluxo de Login 🌊

1. Envia credenciais -> 2. Servidor valida -> 3. Gera JWT -> 4. Frontend guarda o Token -> 5. Envia no Header em cada requisição. <!-- .element: class="fragment" -->

---

## 6. Segurança em Mobile 📱

- Nunca guarde em arquivos de texto! <!-- .element: class="fragment" -->
- Use **EncryptedSharedPreferences** (Android) ou **Keychain** (iOS). <!-- .element: class="fragment" -->

---

## 7. Melhores Práticas 🏆

- Use chaves secretas longas e seguras. <!-- .element: class="fragment" -->
- Defina tempo de expiração (`expiresIn`). <!-- .element: class="fragment" -->
- Protocolo **HTTPS** é obrigatório! <!-- .element: class="fragment" -->

---

## Desafio de Segurança ⚡

O Payload do JWT é criptografado ou apenas codificado? Posso guardar a senha do usuário lá?

---

## Resumo ✅

- JWT permite autenticação rápida e escalável. <!-- .element: class="fragment" -->
- Header + Payload + Signature. <!-- .element: class="fragment" -->
- Stateless = Servidor mais leve. <!-- .element: class="fragment" -->

---

## Próxima Aula: Controle de Acesso 🛡️

### Quem manda aqui? (RBAC)

- Middlewares de autorização. <!-- .element: class="fragment" -->
- Protegendo rotas por nível de usuário. <!-- .element: class="fragment" -->

---

## Dúvidas? 🔐
