# 🚀 Projeto: Nginx com Balanceamento de Carga e Visualização em Tempo Real

## 🎯 Objetivo

Criar um ambiente que simula um sistema web real, demonstrando visualmente como o Nginx atua como um *Reverse Proxy* (Proxy Reverso) e distribui a carga (*Load Balancing*) entre duas APIs de backend idênticas.

## 🛠️ Tecnologias Utilizadas

*   **Docker & Docker Compose:** Para criar e gerenciar os contêineres dos nossos serviços.
*   **Nginx:** Nosso proxy reverso e balanceador de carga.
*   **Node.js (Express):** Para criar duas APIs de backend.
*   **k6:** Uma ferramenta moderna para teste de carga.
*   **HTML/JS (com Chart.js):** Para criar um *dashboard* de visualização em tempo real.

---

## 📁 Parte 1: Estrutura do Projeto

Garanta que seu projeto tenha a seguinte estrutura de pastas e arquivos:

```
projeto-nginx-visual/
├── api-01/
│   ├── index.js
│   ├── package.json
│   └── Dockerfile
├── api-02/
│   ├── index.js
│   ├── package.json
│   └── Dockerfile
├── nginx/
│   ├── nginx.conf
│   └── Dockerfile
├── visualizer/
│   ├── index.html
│   └── chart.js
├── docker-compose.yml
└── load-test.js
```

---

## ⚙️ Parte 2: Criando as APIs de Backend (Node.js)

**1. Arquivos da `api-01/`**

*   `api-01/package.json`:
    ```json
    {
      "name": "api-01",
      "version": "1.0.0",
      "main": "index.js",
      "scripts": { "start": "node index.js" },
      "dependencies": { "express": "^4.18.2" }
    }
    ```
*   `api-01/index.js` (Corrigido para responder na rota raiz):
    ```javascript
    const express = require('express');
    const app = express();
    const PORT = 3000;
    const SERVER_ID = "API-01";

    app.get('/', (req, res) => {
      console.log(`[${SERVER_ID}] Recebeu requisição`);
      res.json({ servidor: SERVER_ID, timestamp: new Date().toISOString() });
    });

    app.listen(PORT, () => console.log(`[${SERVER_ID}] Rodando na porta ${PORT}`));
    ```
*   `api-01/Dockerfile` (Corrigido para o contexto de build correto):
    ```dockerfile
    FROM node:18-alpine
    WORKDIR /usr/src/app

    # Copia APENAS os arquivos necessários da API-01
    COPY ./api-01/package.json ./
    COPY ./api-01/index.js ./

    RUN npm install

    EXPOSE 3000
    CMD [ "npm", "start" ]
    ```

**2. Arquivos da `api-02/`**

Copie os arquivos da `api-01/` para `api-02/` e mude **APENAS UMA LINHA** no `api-02/index.js` e o `Dockerfile` para refletir a `api-02`.

*   `api-02/index.js` (linha 5):
    ```javascript
    const SERVER_ID = "API-02"; // Mude de API-01 para API-02
    ```
*   `api-02/Dockerfile`:
    ```dockerfile
    FROM node:18-alpine
    WORKDIR /usr/src/app

    # Copia APENAS os arquivos necessários da API-02
    COPY ./api-02/package.json ./
    COPY ./api-02/index.js ./

    RUN npm install

    EXPOSE 3000
    CMD [ "npm", "start" ]
    ```

---

##  proxy Parte 3: Configurando o Nginx

*   `nginx/nginx.conf` (Simplificado para HTTP):
    ```nginx
    events { }

    http {
        # Define o grupo de servidores de backend (Load Balancing)
        upstream backend_servers {
            server api-01:3000;
            server api-02:3000;
        }

        # Servidor principal (HTTP)
        server {
            listen 80;
            server_name localhost;

            # Rota para o Dashboard de Visualização
            location / {
                root /usr/share/nginx/html;
                index index.html;
            }

            # Rota da API (Proxy Reverso)
            location /api {
                # Remove o /api antes de enviar para o backend
                rewrite /api(.*) /$1 break; 
                
                # Envia a requisição para o grupo de servidores
                proxy_pass http://backend_servers;
                
                # Cabeçalhos importantes para o proxy
                proxy_set_header Host $host;
                proxy_set_header X-Real-IP $remote_addr;
                proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_set_header X-Forwarded-Proto $scheme;
            }
        }
    }
    ```

*   `nginx/Dockerfile` (Corrigido para o contexto de build correto):
    ```dockerfile
    FROM nginx:1.23-alpine

    # Copia nossa configuração personalizada (relativo ao 'context')
    COPY ./nginx/nginx.conf /etc/nginx/nginx.conf

    # Copia os arquivos do visualizer (relativo ao 'context')
    COPY ./visualizer /usr/share/nginx/html
    ```

---

## 📊 Parte 4: Criando o Dashboard de Visualização

1.  **Obtenha o Chart.js (Versão Correta):**
    Crie o arquivo `visualizer/chart.js`. Abra o link abaixo, copie **todo o conteúdo** e cole no seu arquivo `chart.js`.

    *   **Link:** `https://cdnjs.cloudflare.com/ajax/libs/Chart.js/4.4.1/chart.umd.min.js`

2.  `visualizer/index.html`:
    ```html
    <!DOCTYPE html>
    <html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Visualizador de Carga Nginx</title>
        <script src="chart.js"></script>
        <style>
            body { font-family: sans-serif; display: grid; place-items: center; background: #f4f4f4; }
            #chart-container { width: 500px; }
            h1 { color: #333; }
            h2 { margin: 5px 0; }
            #api-01 { color: #36A2EB; }
            #api-02 { color: #FF6384; }
        </style>
    </head>
    <body>
        <h1>Dashboard de Balanceamento de Carga</h1>
        <div id="chart-container">
            <canvas id="loadChart"></canvas>
        </div>
        <h2 id="api-01">API-01: 0</h2>
        <h2 id="api-02">API-02: 0</h2>
        <h3>Total: <span id="total">0</span></h3>

        <script>
            const ctx = document.getElementById('loadChart').getContext('2d');
            const counters = { 'API-01': 0, 'API-02': 0 };
            let total = 0;

            const h2_api1 = document.getElementById('api-01');
            const h2_api2 = document.getElementById('api-02');
            const span_total = document.getElementById('total');

            const loadChart = new Chart(ctx, {
                type: 'pie',
                data: {
                    labels: ['API-01', 'API-02'],
                    datasets: [{
                        data: [0, 0],
                        backgroundColor: ['#36A2EB', '#FF6384']
                    }]
                },
                options: { responsive: true, animation: { duration: 500 } }
            });

            async function fetchData() {
                try {
                    const response = await fetch('/api');
                    if (!response.ok) throw new Error('Falha na rede');
                    const data = await response.json();
                    
                    counters[data.servidor]++;
                    total++;

                    h2_api1.innerText = `API-01: ${counters['API-01']}`;
                    h2_api2.innerText = `API-02: ${counters['API-02']}`;
                    span_total.innerText = total;

                    loadChart.data.datasets[0].data = [counters['API-01'], counters['API-02']];
                    loadChart.update();
                } catch (error) {
                    console.error('Erro ao buscar API:', error);
                }
            }
            setInterval(fetchData, 200);
        </script>
    </body>
    </html>
    ```

---

## 🐳 Parte 5: Orquestrando com Docker Compose

*   `docker-compose.yml` (Corrigido para usar `context` e `dockerfile`):
    ```yaml
    services:
      api-01:
        build:
          context: .
          dockerfile: ./api-01/Dockerfile

      api-02:
        build:
          context: .
          dockerfile: ./api-02/Dockerfile

      nginx:
        build:
          context: .
          dockerfile: ./nginx/Dockerfile
        ports:
          - "80:80"
        depends_on:
          - api-01
          - api-02
    ```

---

## ⚡ Parte 6: Script de Teste de Carga (k6)

*   `load-test.js` (Simplificado para HTTP):
    ```javascript
    import http from 'k6/http';
    import { sleep } from 'k6';

    export const options = {
      stages: [
        { duration: '30s', target: 10 }, // Simula 10 usuários por 30 segundos
      ],
    };

    export default function () {
      // Acessa o endpoint da API através do Nginx
      http.get('http://localhost/api');
      sleep(0.5); // Espera 500ms entre requisições
    }
    ```

---

## 🏁 Parte 7: Executando o Projeto!

**1. Instale o k6:**
Siga as instruções em [k6.io](https://k6.io/docs/getting-started/installation/) para o seu sistema operacional.

**2. Suba os Serviços:**
No terminal, na raiz do projeto, execute:
```bash
docker-compose up --build
```
Isso irá construir as imagens e iniciar os 3 contêineres (`api-01`, `api-02`, `nginx`).

**3. Acesse o Visualizador:**
Abra seu navegador e vá para: **`http://localhost`**

Você verá o dashboard. Ele já começará a fazer requisições lentamente, e o gráfico de pizza deve mostrar uma divisão de ~50% para cada API.

**4. Execute o Teste de Carga:**

# K6 Load Testing for API on Windows 11 🚀

K6 é uma poderosa ferramenta open-source para testes de carga de APIs. Abaixo estão os passos para configurar e executar o K6 no Windows 11.

## 1. Instalar o K6 no Windows 11 🖥️

1. Abra um terminal (Prompt de Comando, PowerShell ou Bash).

2. Use o Gerenciador de Pacotes do Windows (Winget) para instalar o K6:

   ```bash
   winget install k6 --source winget
    ```

---

Em **outro** terminal, execute o k6:
```bash
k6 run load-test.js
```

**5. Observe o Dashboard:**
Volte para o navegador. Você verá os números no dashboard subindo **drasticamente rápido**. O gráfico de pizza se ajustará em tempo real, mas deve sempre se manter próximo de 50/50, provando que o Nginx está distribuindo a carga.

**6. Para Desligar:**
Pressione `Ctrl + C` no terminal onde o `docker-compose` está rodando e depois execute:
```bash
docker-compose down
```
