import axios from 'axios'

const apiClient = axios.create({
  baseURL: 'http://localhost:8088/api', // Verifique a porta e o /api
  headers: {
    'Content-Type': 'application/json',
  },
})

export default {
  getTarefas() {
    return apiClient.get('/tarefas')
  },
  addTarefa(tarefa) {
    return apiClient.post('/tarefas', tarefa)
  },
  deleteTarefa(id) {
    return apiClient.delete(`/tarefas/${id}`)
  },
  // --- NOVAS FUNÇÕES ADICIONADAS ---
  updateStatusTarefa(id, concluida) {
    return apiClient.put(`/tarefas/${id}/status`, { concluida })
  },
  updateTituloTarefa(id, titulo) {
    return apiClient.patch(`/tarefas/${id}/titulo`, { titulo })
  },
}
