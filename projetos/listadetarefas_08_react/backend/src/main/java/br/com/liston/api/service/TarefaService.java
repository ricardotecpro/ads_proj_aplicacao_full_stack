package br.com.liston.api.service;

import br.com.liston.api.dto.TarefaRequestDTO;
import br.com.liston.api.dto.TarefaResponseDTO;
import br.com.liston.api.model.Tarefa;
import br.com.liston.api.repository.TarefaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service // Marks as a service component (where logic resides)
public class TarefaService {

    @Autowired // Repository dependency injection
    private TarefaRepository repository;

    // Fetches all tasks and converts to DTO
    @Transactional(readOnly = true) // Read-only transaction
    public List<TarefaResponseDTO> listarTodas() {
        return repository.findAll()
                .stream()
                .map(TarefaResponseDTO::new) // Converts Tarefa -> TarefaResponseDTO
                .collect(Collectors.toList());
    }

    // Search by ID
    @Transactional(readOnly = true)
    public TarefaResponseDTO buscarPorId(Long id) {
        Tarefa tarefa = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));
        return new TarefaResponseDTO(tarefa);
    }

    // Creates a new task
    @Transactional
    public TarefaResponseDTO criar(TarefaRequestDTO dto) {
        if (dto.titulo() == null || dto.titulo().isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }

        Tarefa novaTarefa = new Tarefa(dto.titulo(), dto.descricao());

        // If 'concluida' was sent in DTO, use it. Otherwise, keep default (false).
        if (dto.concluida() != null) {
            novaTarefa.setConcluida(dto.concluida());
        }

        Tarefa tarefaSalva = repository.save(novaTarefa);
        return new TarefaResponseDTO(tarefaSalva);
    }

    // Updates an existing task
    @Transactional
    public TarefaResponseDTO atualizar(Long id, TarefaRequestDTO dto) {
        Tarefa tarefaExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task not found with id: " + id));

        // Updates only provided fields
        if (dto.titulo() != null && !dto.titulo().isBlank()) {
            tarefaExistente.setTitulo(dto.titulo());
        }
        if (dto.descricao() != null) {
            tarefaExistente.setDescricao(dto.descricao());
        }
        if (dto.concluida() != null) {
            tarefaExistente.setConcluida(dto.concluida());
        }

        Tarefa tarefaAtualizada = repository.save(tarefaExistente);
        return new TarefaResponseDTO(tarefaAtualizada);
    }

    // Deletes a task
    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Task not found with id: " + id);
        }
        repository.deleteById(id);
    }
}