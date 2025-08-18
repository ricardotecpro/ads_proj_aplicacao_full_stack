**Tutorial sequencial e didático** para criar um **Chat em Tempo Real com WebSockets**, usando **Node.js** para o backend e **HTML + JavaScript** para o frontend.

https://gist.github.com/ricardotecpro/f7e2928a73b6434b77d304e752e44773

---

# **📝 Criando um Chat em Tempo Real com WebSockets**

Neste tutorial, você aprenderá a criar um chat simples usando **WebSockets** com **Node.js** no backend e **HTML + JavaScript** no frontend.

## **📌 1. Pré-requisitos**

Antes de começar, certifique-se de ter instalado:

- **[Node.js](https://nodejs.org/)** (verifique executando `node -v` no terminal)
- **Um editor de código** (VS Code, por exemplo)

---

## **📂 2. Estrutura do Projeto**

Crie uma pasta para o projeto e dentro dela organize os arquivos da seguinte forma:

```
chat-websocket/
│── server/             # Backend (servidor WebSocket)
│   ├── server.js       # Código do servidor WebSocket
│── client/             # Frontend (interface do chat)
│   ├── index.html      # Página do chat
│   ├── script.js       # Código JavaScript do cliente
│   ├── styles.css      # Estilização do chat
│── package.json        # Configuração do projeto Node.js
│── README.md           # Instruções do projeto
```

Agora, siga as etapas abaixo para criar o chat WebSocket. 🚀

---

## **🛠️ 3. Configurando o Servidor WebSocket**

### **3.1 Criando o projeto Node.js**

Abra um terminal na pasta `chat-websocket` e inicialize um projeto Node.js:

```sh
npm init -y
```

Isso criará um arquivo `package.json`.

### **3.2 Instalando a biblioteca WebSocket**

Agora, instale a biblioteca **ws**, que será usada para criar o servidor WebSocket:

```sh
npm install ws
```

### **3.3 Criando o servidor WebSocket**

Crie a pasta `server/` e dentro dela o arquivo `server.js` com o seguinte código:

📌 **server/server.js**

```js
const WebSocket = require('ws');

const server = new WebSocket.Server({ port: 8080 });

server.on('connection', (socket) => {
    console.log('Cliente conectado.');

    socket.on('message', (message) => {
        console.log('Mensagem recebida:', message);
        
        // Enviar a mensagem para todos os clientes conectados
        server.clients.forEach(client => {
            if (client.readyState === WebSocket.OPEN) {
                client.send(`Mensagem: ${message}`);
            }
        });
    });

    socket.on('close', () => console.log('Cliente desconectado.'));
    socket.on('error', (error) => console.error('Erro no WebSocket:', error));
});

console.log('Servidor WebSocket rodando na porta 8080');
```

---

## **🖥️ 4. Criando o Cliente Web**

Agora vamos criar o **frontend** do chat.

### **4.1 Criando o arquivo HTML**

Crie a pasta `client/` e dentro dela um arquivo chamado `index.html` com o seguinte conteúdo:

📌 **client/index.html**

```html
<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <title>Chat WebSocket</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <h2>Chat WebSocket</h2>
    <input type="text" id="mensagem" placeholder="Digite uma mensagem">
    <button onclick="enviarMensagem()">Enviar</button>
    <div id="chat"></div>

    <script src="script.js"></script>
</body>
</html>
```

### **4.2 Criando o código JavaScript do cliente**

Agora, crie o arquivo `client/script.js` com o código de comunicação com o WebSocket:

📌 **client/script.js**

```js
const socket = new WebSocket('ws://localhost:8080');

socket.onopen = () => console.log('Conectado ao servidor.');

socket.onmessage = (event) => {
    const chat = document.getElementById('chat');
    const msg = document.createElement('p');
    msg.textContent = event.data;
    chat.appendChild(msg);
};

socket.onerror = (error) => console.error('Erro no WebSocket:', error);
socket.onclose = () => console.log('Conexão fechada.');

function enviarMensagem() {
    const mensagem = document.getElementById('mensagem').value;
    if (mensagem.trim() !== "" && socket.readyState === WebSocket.OPEN) {
        socket.send(mensagem);
        document.getElementById('mensagem').value = "";
    } else {
        alert('Conexão não está aberta ou mensagem vazia.');
    }
}
```

### **4.3 Criando o estilo do chat**

Agora, crie o arquivo `client/styles.css` para estilizar a interface:

📌 **client/styles.css**

```css
body { font-family: Arial, sans-serif; text-align: center; padding: 20px; }
input { width: 80%; padding: 10px; margin-bottom: 10px; }
button { padding: 10px 15px; cursor: pointer; }
#chat { max-width: 600px; margin: auto; text-align: left; }
p { background: #f3f3f3; padding: 10px; border-radius: 5px; }
```

---

## **🚀 5. Executando o projeto**

Agora que tudo está pronto, vamos testar o chat! 🎉

### **5.1 Iniciando o servidor WebSocket**

No terminal, dentro da pasta do projeto, rode o comando:

```sh
node server/server.js
```

Isso iniciará o servidor WebSocket na porta **8080**.

### **5.2 Abrindo o chat no navegador**

Agora, abra o arquivo `client/index.html` em um navegador **(Chrome, Edge, Firefox, etc.)**.

---

## **🛠️ 6. Testando e Melhorando**

Agora que o chat está funcionando, experimente: ✅ **Abrir múltiplas abas** no navegador e enviar mensagens para ver como elas são recebidas por todos os clientes.  
✅ **Adicionar novos recursos**, como nomes de usuários e histórico de mensagens.

---

## **💡 Dicas Extras**

- Se quiser rodar o servidor automaticamente, adicione um script no `package.json`:
    
    📌 **package.json**
    
    ```json
    "scripts": {
      "start": "node server/server.js"
    }
    ```
    
    Agora, basta rodar:
    
    ```sh
    npm start
    ```
    
- Caso o WebSocket não conecte, verifique se o firewall do sistema está bloqueando a porta **8080**.
    

---

## **🎯 Conclusão**

Parabéns! 🎉 Você criou um **chat WebSocket** simples usando **Node.js** no backend e **HTML + JavaScript** no frontend. Esse conhecimento pode ser expandido para criar aplicações mais complexas, como chats com autenticação e armazenamento de mensagens.

 
 
---

### 🚀 [ricardotecpro.github.io](https://ricardotecpro.github.io/)

