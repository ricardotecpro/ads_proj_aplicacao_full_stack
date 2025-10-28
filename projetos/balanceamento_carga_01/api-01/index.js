const express = require('express');
const app = express();
const PORT = 3000;
const SERVER_ID = "API-01"; // <-- Note o ID

app.get('/', (req, res) => {
  console.log(`[${SERVER_ID}] Recebeu requisição`);
  res.json({ servidor: SERVER_ID, timestamp: new Date().toISOString() });
});

app.listen(PORT, () => console.log(`[${SERVER_ID}] Rodando na porta ${PORT}`));