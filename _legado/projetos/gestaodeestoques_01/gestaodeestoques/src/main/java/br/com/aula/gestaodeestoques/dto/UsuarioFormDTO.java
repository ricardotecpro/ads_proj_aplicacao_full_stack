package br.com.aula.gestaodeestoques.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
// DTO para CRIAR/ATUALIZAR usuários.
public record UsuarioFormDTO(
    Long id,
    @NotBlank String login,
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres") String senha,
    @NotNull Boolean ativo,
    @NotNull Set<Long> papeisIds
) {}