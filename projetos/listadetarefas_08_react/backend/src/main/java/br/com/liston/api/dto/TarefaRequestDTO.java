package br.com.liston.api.dto;

// Usamos 'record' do Java 21 para DTOs imutáveis e concisos
// jakarta.validation será usado futuramente para validar (@NotEmpty, @Size)
public record TarefaRequestDTO(
        String titulo,
        String descricao,
        Boolean concluida // Usamos Boolean (objeto) para permitir nulo se quisermos
) {}