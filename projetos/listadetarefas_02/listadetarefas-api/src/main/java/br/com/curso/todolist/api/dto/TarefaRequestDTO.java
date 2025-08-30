package br.com.curso.todolist.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TarefaRequestDTO(
        @NotBlank(message = "O título não pode ser vazio.")
        @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres.")
        String titulo,

        String descricao,

        @NotNull(message = "O status de conclusão é obrigatório.")
        Boolean concluida
) {}