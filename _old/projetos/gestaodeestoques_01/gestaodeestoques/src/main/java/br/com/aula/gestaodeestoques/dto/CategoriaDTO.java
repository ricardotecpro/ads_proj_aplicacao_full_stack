package br.com.aula.gestaodeestoques.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record CategoriaDTO(
    Integer id,
    @NotBlank(message = "Nome da categoria é obrigatório")
    @Size(min = 3, message = "Nome deve ter no mínimo 3 caracteres")
    String nome
) {}