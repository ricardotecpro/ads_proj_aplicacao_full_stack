package br.com.tarefas.api.service;

import br.com.tarefas.api.model.Tarefa;
import br.com.tarefas.api.repository.TarefaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TarefaService {

    @Autowired
    private TarefaRepository tarefaRepository;

    public List<Tarefa> listarTodas() {
        return tarefaRepository.findAll();
    }

    public Tarefa buscarPorId(Long id) {
        return tarefaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa nao encontrada com o id: " + id));
    }

    public Tarefa criarTarefa(Tarefa tarefa) {
        if (tarefa.getTitulo() == null || tarefa.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O titulo da tarefa e obrigatorio.");
        }
        return tarefaRepository.save(tarefa);
    }

    public Tarefa atualizar(Long id, Tarefa tarefaAtualizada) {
        Tarefa tarefaExistente = buscarPorId(id);

        if (tarefaAtualizada.getTitulo() == null || tarefaAtualizada.getTitulo().isBlank()) {
            throw new IllegalArgumentException("O titulo da tarefa nao pode ser vazio.");
        }

        tarefaExistente.setTitulo(tarefaAtualizada.getTitulo());
        tarefaExistente.setConcluida(tarefaAtualizada.isConcluida());

        return tarefaRepository.save(tarefaExistente);
    }

    public Tarefa atualizarStatus(Long id, boolean concluida) {
        Tarefa tarefaExistente = buscarPorId(id);
        tarefaExistente.setConcluida(concluida);
        return tarefaRepository.save(tarefaExistente);
    }

    public Tarefa atualizarTitulo(Long id, String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O novo titulo nao pode ser vazio.");
        }

        Tarefa tarefaExistente = buscarPorId(id);
        tarefaExistente.setTitulo(titulo);
        return tarefaRepository.save(tarefaExistente);
    }

    public void deletarTarefa(Long id) {
        if (!tarefaRepository.existsById(id)) {
            throw new RuntimeException("Tarefa nao encontrada com o id: " + id);
        }
        tarefaRepository.deleteById(id);
    }
}