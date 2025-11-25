import axios from 'axios';

// Cria uma instância do Axios com a URL base da nossa API
const api = axios.create({
    baseURL: 'http://localhost:8080/api',
});

// Funções de CRUD para Tarefas
export const getTasks = () => api.get('/tasks');
export const createTask = (task) => api.post('/tasks', task);
export const updateTask = (id, task) => api.put(`/tasks/${id}`, task);
export const deleteTask = (id) => api.delete(`/tasks/${id}`);

export default api;