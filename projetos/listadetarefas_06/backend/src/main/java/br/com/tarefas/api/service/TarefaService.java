package br.com.tarefas.api.service;

import br.com.tarefas.api.model.Tarefa;
import br.com.tarefas.api.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional; // Importar Optional

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public List<Tarefa> listarTodas() {
        return tarefaRepository.findAll();
    }

    public Tarefa criarTarefa(Tarefa tarefa) {
        if (tarefa.getTitulo() == null || tarefa.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O título da tarefa é obrigatório.");
        }
        return tarefaRepository.save(tarefa);
    }

    // --- NOVOS MÉTODOS ADICIONADOS ---

    /**
     * Atualiza o status de conclusão de uma tarefa.
     * 
     * @param id        O ID da tarefa a ser atualizada.
     * @param concluida O novo status de conclusão.
     * @return A tarefa atualizada.
     */
    public Tarefa atualizarStatus(Long id, boolean concluida) {
        // orElseThrow lança uma exceção se a tarefa não for encontrada.
        Tarefa tarefaExistente = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com o id: " + id));

        tarefaExistente.setConcluida(concluida);
        return tarefaRepository.save(tarefaExistente);
    }

    /**
     * Atualiza o título de uma tarefa.
     * 
     * @param id     O ID da tarefa a ser atualizada.
     * @param titulo O novo título.
     * @return A tarefa atualizada.
     */
    public Tarefa atualizarTitulo(Long id, String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O novo título não pode ser vazio.");
        }

        Tarefa tarefaExistente = tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada com o id: " + id));

        tarefaExistente.setTitulo(titulo);
        return tarefaRepository.save(tarefaExistente);
    }

    public void deletarTarefa(Long id) {
        if (!tarefaRepository.existsById(id)) {
            throw new RuntimeException("Tarefa não encontrada com o id: " + id);
        }
        tarefaRepository.deleteById(id);
    }
}