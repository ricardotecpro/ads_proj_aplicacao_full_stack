package br.com.liston.api.dto;

// We use Java 21 'record' for immutable and concise DTOs
// jakarta.validation will be used in the future for validation (@NotEmpty, @Size)
public record TarefaRequestDTO(
                String titulo,
                String descricao,
                Boolean concluida // We use Boolean (object) to allow null if desired
) {
}