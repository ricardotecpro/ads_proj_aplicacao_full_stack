package br.com.curso.todolist.api.dto;

public record TarefaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        boolean concluida
) {
}