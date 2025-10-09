package br.com.aula.gestaodeestoques.dto;
import java.util.Set;
// DTO para EXIBIR usuários na API, omitindo a senha.
public record UsuarioDTO(Long id, String login, boolean ativo, Set<String> papeis) {}