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

@Service // Marca como um componente de serviço (onde fica a lógica)
public class TarefaService {

    @Autowired // Injeção de dependência do repositório
    private TarefaRepository repository;

    // Busca todas as tarefas e converte para DTO
    @Transactional(readOnly = true) // Transação apenas de leitura
    public List<TarefaResponseDTO> listarTodas() {
        return repository.findAll()
                .stream()
                .map(TarefaResponseDTO::new) // Converte Tarefa -> TarefaResponseDTO
                .collect(Collectors.toList());
    }

    // Busca por ID
    @Transactional(readOnly = true)
    public TarefaResponseDTO buscarPorId(Long id) {
        Tarefa tarefa = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com id: " + id));
        return new TarefaResponseDTO(tarefa);
    }

    // Cria uma nova tarefa
    @Transactional
    public TarefaResponseDTO criar(TarefaRequestDTO dto) {
        if (dto.titulo() == null || dto.titulo().isBlank()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }

        Tarefa novaTarefa = new Tarefa(dto.titulo(), dto.descricao());

        // Se 'concluida' foi enviado no DTO, usa o valor. Senão, mantém o padrão (false).
        if (dto.concluida() != null) {
            novaTarefa.setConcluida(dto.concluida());
        }

        Tarefa tarefaSalva = repository.save(novaTarefa);
        return new TarefaResponseDTO(tarefaSalva);
    }

    // Atualiza uma tarefa existente
    @Transactional
    public TarefaResponseDTO atualizar(Long id, TarefaRequestDTO dto) {
        Tarefa tarefaExistente = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tarefa não encontrada com id: " + id));

        // Atualiza apenas os campos fornecidos
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

    // Deleta uma tarefa
    @Transactional
    public void deletar(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Tarefa não encontrada com id: " + id);
        }
        repository.deleteById(id);
    }
}