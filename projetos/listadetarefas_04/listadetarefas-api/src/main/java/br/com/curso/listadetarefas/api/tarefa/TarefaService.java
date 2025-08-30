package br.com.curso.listadetarefas.api.tarefa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TarefaService {
    @Autowired
    private TarefaRepository tarefaRepository;

    public List<Tarefa> listarTodas() { return tarefaRepository.findAll(); }
    public Tarefa criar(Tarefa tarefa) { return tarefaRepository.save(tarefa); }
    public void deletar(Long id) {
        if (!tarefaRepository.existsById(id)) {
            throw new RuntimeException("Tarefa não encontrada com o id: " + id);
        }
        tarefaRepository.deleteById(id);
    }

    // --- MODIFICAÇÃO NESTE MÉTODO ---
    public Tarefa atualizar(Long id, TarefaUpdateDTO tarefaDTO) {
        return tarefaRepository.findById(id)
                .map(tarefaExistente -> {
                    // Atualiza a entidade existente APENAS com os dados do DTO
                    tarefaExistente.setDescricao(tarefaDTO.getDescricao());
                    tarefaExistente.setConcluida(tarefaDTO.isConcluida());
                    // Salva a entidade atualizada
                    return tarefaRepository.save(tarefaExistente);
                }).orElseThrow(() -> new RuntimeException("Tarefa não encontrada: " + id));
    }
}