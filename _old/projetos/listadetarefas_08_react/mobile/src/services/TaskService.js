import api from './api';

const TaskService = {
    getAll: async () => {
        const response = await api.get('/tarefas');
        return response.data;
    },
    getById: async (id) => {
        const response = await api.get(`/tarefas/${id}`);
        return response.data;
    },
    create: async (task) => {
        const response = await api.post('/tarefas', task);
        return response.data;
    },
    update: async (id, task) => {
        const response = await api.put(`/tarefas/${id}`, task);
        return response.data;
    },
    delete: async (id) => {
        await api.delete(`/tarefas/${id}`);
    }
};

export default TaskService;
