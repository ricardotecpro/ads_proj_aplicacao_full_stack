package br.com.liston.api.dto;

import br.com.liston.api.model.Tarefa;
import java.time.LocalDateTime;

public record TarefaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        boolean concluida,
        LocalDateTime dataCriacao,
        LocalDateTime dataAtualizacao
) {
    // Construtor "Mapper" para converter a Entidade Tarefa em TarefaResponseDTO
    public TarefaResponseDTO(Tarefa tarefa) {
        this(
                tarefa.getId(),
                tarefa.getTitulo(),
                tarefa.getDescricao(),
                tarefa.isConcluida(),
                tarefa.getDataCriacao(),
                tarefa.getDataAtualizacao()
        );
    }
}