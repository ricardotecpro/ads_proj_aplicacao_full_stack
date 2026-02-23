import http from 'k6/http';
import { sleep } from 'k6';

export const options = {
  // Simula 10 usuários virtuais (VUs)
  // por 30 segundos.
  stages: [
    { duration: '30s', target: 10 },
  ],
};

export default function () {
  // Acessa o endpoint HTTP através do Nginx
  http.get('http://localhost/api');
  sleep(0.5); // Espera 500ms entre requisições por usuário
}