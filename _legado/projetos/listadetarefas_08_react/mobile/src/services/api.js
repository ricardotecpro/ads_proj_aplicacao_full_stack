import axios from 'axios';

// Para Android Emulator use 10.0.2.2
// Para dispositivo físico, use o IP da sua máquina (ex: 192.168.1.X)
const API_URL = 'http://10.0.2.2:8080/api';

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

export default api;
