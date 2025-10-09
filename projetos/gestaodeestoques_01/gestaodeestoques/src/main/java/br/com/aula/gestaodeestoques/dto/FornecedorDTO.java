package br.com.aula.gestaodeestoques.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
public record FornecedorDTO(
    Integer id,
    @NotBlank(message = "Nome do fornecedor é obrigatório")
    String nome,
    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}", message = "CNPJ inválido")
    String cnpj
) {}