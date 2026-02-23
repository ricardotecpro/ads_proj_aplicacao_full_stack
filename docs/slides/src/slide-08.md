# Aula 08 - Boas Práticas e Validação ✅
## Qualidade e Segurança no Backend

---

## Agenda 📅

1. Por que Validar Tudo? <!-- .element: class="fragment" -->
2. Validação vs Sanitização <!-- .element: class="fragment" -->
3. Schema Validation (Ex: Zod) <!-- .element: class="fragment" -->
4. Clean Code (Código Limpo) <!-- .element: class="fragment" -->
5. Tratamento de Erros Profissional <!-- .element: class="fragment" -->
6. Middlewares Globais <!-- .element: class="fragment" -->

---

## 1. Regra de Ouro: Desconfiança 🛡️

- O cliente é o "lado perigoso" da aplicação. <!-- .element: class="fragment" -->
- Validações evitam dados corrompidos. <!-- .element: class="fragment" -->
- **Defesa em Profundidade**: Garanta a regra no banco E no código. <!-- .element: class="fragment" -->

---

## 2. Validar vs Sanitizar 🧼

- **Validar**: Checar (Idade > 18?). <!-- .element: class="fragment" -->
- **Sanitizar**: Limpar (Remover `<script>`). <!-- .element: class="fragment" -->

---

## 3. Schema Validation 📐

- Crie "moldes" para seus dados. <!-- .element: class="fragment" -->
- Validação centralizada e reutilizável. <!-- .element: class="fragment" -->

```javascript
const userSchema = {
    nome: string().min(3),
    email: string().email()
};
```

---

## 4. O Backend Elegante (Clean Code) ✨

- **DRY**: Don't Repeat Yourself (Não repita lógica). <!-- .element: class="fragment" -->
- **KISS**: Keep It Simple, Stupid (Mantenha o simples). <!-- .element: class="fragment" -->
- Nomes de funções que explicam o que está acontecendo. <!-- .element: class="fragment" -->

---

## 5. Tratamento de Erros 🚨

- Controller trata o fluxo, não o detalhe técnico. <!-- .element: class="fragment" -->
- **Try/Catch Global**: Evite crashes. <!-- .element: class="fragment" -->
- Mensagens amigáveis para o cliente. <!-- .element: class="fragment" -->

---

## 6. Logs vs Mensagens 📜

- **Terminal/Log**: Detalhe técnico completo. <!-- .element: class="fragment" -->
- **Cliente (JSON)**: Apenas o que ele precisa saber. <!-- .element: class="fragment" -->

> "Ocorreu um erro interno" (Cliente) ✅
> "Query failed at line 42 due to NULL constraint" (Logs) ✅

---

## 7. Prática: O Schema Perfeito 💻

- Validando um produto complexo. <!-- .element: class="fragment" -->
- Tratando erros de tipo (String no lugar de Number). <!-- .element: class="fragment" -->

---

## Desafio: Limpeza ⚡

Se você recebe um texto de um post com muitos espaços em branco no final, você deve **Validar** ou **Sanitizar**?

---

## Resumo ✅

- Backend robusto exige validação rigorosa. <!-- .element: class="fragment" -->
- Limpe os dados antes de salvar (Sanitize). <!-- .element: class="fragment" -->
- Middleware Global centraliza a gestão de falhas. <!-- .element: class="fragment" -->
- Código limpo economiza meses de manutenção. <!-- .element: class="fragment" -->

---

## Próxima Aula: Módulo 3! 🔐

### Segurança e Autenticação

- Quem é você? (Authentication). <!-- .element: class="fragment" -->
- O que você pode fazer? (Authorization). <!-- .element: class="fragment" -->

---

## Dúvidas? ✅
