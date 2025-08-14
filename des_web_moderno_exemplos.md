## Desenvolvimento Web Moderno: 
### HTTP, TCP/IP, WebSockets, SPA, HTMX, IPv4/IPv6 e Infraestrutura de Rede com Exemplos Práticos

O desenvolvimento web envolve não apenas frameworks modernos, mas também uma **infraestrutura robusta** que garante a comunicação eficiente entre servidores e clientes. Aqui, veremos conceitos como **HTTP, TCP/IP, DNS, DHCP, IPv4/IPv6, CDNs** e exemplos práticos para aplicar esses conceitos.

---

## **1. Códigos de Status HTTP com Exemplo Prático**

Os códigos HTTP indicam o status das requisições entre cliente e servidor.

### **Exemplo: Criando uma API REST com Node.js e Express**

Este código cria um servidor que responde com diferentes códigos HTTP.

```javascript
const express = require('express');
const app = express();
const port = 3000;

// Rota com status 200 (OK)
app.get('/', (req, res) => {
    res.status(200).send('Requisição bem-sucedida!');
});

// Rota com status 404 (Not Found)
app.get('/erro', (req, res) => {
    res.status(404).send('Recurso não encontrado!');
});

// Rota com status 500 (Erro Interno)
app.get('/falha', (req, res) => {
    res.status(500).send('Erro no servidor!');
});

app.listen(port, () => {
    console.log(`Servidor rodando em http://localhost:${port}`);
});
```

### **Testando**

1. Acesse `http://localhost:3000/` → Retorna `200 OK`.
2. Acesse `http://localhost:3000/erro` → Retorna `404 Not Found`.
3. Acesse `http://localhost:3000/falha` → Retorna `500 Internal Server Error`.

---

## **2. Comunicação TCP/IP com Exemplo Prático**

### **Exemplo: Criando um Servidor TCP em Node.js**

O TCP garante a entrega ordenada dos pacotes de dados. Aqui está um exemplo de **servidor e cliente TCP**.

#### **Servidor TCP (Node.js)**

```javascript
const net = require('net');

const server = net.createServer((socket) => {
    console.log('Cliente conectado.');

    socket.on('data', (data) => {
        console.log(`Mensagem recebida: ${data}`);
        socket.write('Resposta do servidor: ' + data);
    });

    socket.on('end', () => console.log('Cliente desconectado.'));
});

server.listen(4000, () => console.log('Servidor TCP rodando na porta 4000'));
```

#### **Cliente TCP (Node.js)**

```javascript
const net = require('net');

const client = net.createConnection({ port: 4000 }, () => {
    console.log('Conectado ao servidor');
    client.write('Olá, servidor!');
});

client.on('data', (data) => {
    console.log(`Resposta do servidor: ${data}`);
    client.end();
});

client.on('end', () => console.log('Desconectado do servidor'));
```

### **Testando**

1. **Execute o servidor TCP**: `node servidor.js`.
2. **Execute o cliente TCP**: `node cliente.js`.
3. **Saída esperada**:
    - No servidor: "Mensagem recebida: Olá, servidor!"
    - No cliente: "Resposta do servidor: Olá, servidor!"

---

## **3. WebSockets: Comunicação em Tempo Real**

Os **WebSockets** permitem comunicação bidirecional entre cliente e servidor.

### **Exemplo: Criando um Chat com WebSockets (Node.js e HTML)**

#### **Servidor WebSocket (Node.js)**

```javascript
const WebSocket = require('ws');
const server = new WebSocket.Server({ port: 8080 });

server.on('connection', (socket) => {
    console.log('Cliente conectado.');

    socket.on('message', (message) => {
        console.log('Mensagem recebida:', message);
        socket.send(`Servidor recebeu: ${message}`);
    });

    socket.on('close', () => console.log('Cliente desconectado.'));
});

console.log('Servidor WebSocket rodando na porta 8080');
```

#### **Cliente WebSocket (HTML + JavaScript)**

```html
<!DOCTYPE html>
<html lang="pt">
<head>
    <meta charset="UTF-8">
    <title>Chat WebSocket</title>
</head>
<body>
    <h2>Chat WebSocket</h2>
    <input type="text" id="mensagem" placeholder="Digite uma mensagem">
    <button onclick="enviarMensagem()">Enviar</button>
    <p id="resposta"></p>

    <script>
        const socket = new WebSocket('ws://localhost:8080');

        socket.onmessage = (event) => {
            document.getElementById('resposta').innerText = event.data;
        };

        function enviarMensagem() {
            const mensagem = document.getElementById('mensagem').value;
            socket.send(mensagem);
        }
    </script>
</body>
</html>
```

### **Testando**

1. **Inicie o servidor WebSocket**: `node servidor.js`.
2. **Abra o HTML no navegador e envie mensagens**.
3. **Veja a resposta do servidor sendo exibida na página**.

---

## **4. Resolução de DNS com Exemplo Prático**

O **DNS** converte nomes de domínio em endereços IP.

### **Exemplo: Fazendo uma Consulta DNS em Node.js**

```javascript
const dns = require('dns');

dns.lookup('google.com', (err, address) => {
    if (err) throw err;
    console.log(`Endereço IP do Google: ${address}`);
});
```

### **Testando**

Execute o script com `node dns.js`.

- **Saída esperada**: `Endereço IP do Google: 142.250.217.206` (pode variar).

---

## **5. Testando IPv4 e IPv6**

Para verificar o **endereço IPv4 e IPv6 do seu dispositivo**, use o seguinte comando:

```sh
ipconfig (Windows)
ifconfig (Linux/Mac)
```

Ou execute o seguinte código em **Node.js**:

```javascript
const os = require('os');

const interfaces = os.networkInterfaces();
for (let nome in interfaces) {
    interfaces[nome].forEach((info) => {
        console.log(`${nome}: ${info.family} - ${info.address}`);
    });
}
```

**Saída esperada** (Exemplo):

```
Wi-Fi: IPv4 - 192.168.1.10
Wi-Fi: IPv6 - fe80::1c4d:4567:abcd:ef90
```

---

## **6. Simulando uma CDN**

Uma **CDN** distribui conteúdo para melhorar a performance.

### **Exemplo: Servindo Arquivos Estáticos com Express.js (Simulação de CDN)**

```javascript
const express = require('express');
const app = express();

app.use('/static', express.static('public')); // Pasta pública para arquivos estáticos

app.listen(3000, () => {
    console.log('Servidor rodando em http://localhost:3000/static/arquivo.jpg');
});
```

1. Crie uma pasta `public/` e adicione uma imagem `arquivo.jpg`.
2. Execute o servidor com `node cdn.js`.
3. Acesse `http://localhost:3000/static/arquivo.jpg` no navegador.

Isso simula um CDN, servindo arquivos de forma otimizada.

---

## **Conclusão**

Com esses exemplos práticos, você pode testar e entender melhor conceitos essenciais como **HTTP, TCP/IP, WebSockets, DNS, IPv4/IPv6 e CDNs**.


---

### [ricardotecpro.github.io](https://ricardotecpro.github.io/)
