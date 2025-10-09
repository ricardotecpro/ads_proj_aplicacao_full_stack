package br.com.aula.gestaodeestoques.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record ProdutoFormDTO(
    Integer id,

    @NotBlank(message = "O nome do produto não pode ser vazio.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    String nome,

    @NotNull(message = "A quantidade é obrigatória.")
    @PositiveOrZero(message = "A quantidade não pode ser negativa.")
    Integer quantidade,

    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    BigDecimal preco,

    @NotNull(message = "A categoria é obrigatória.")
    Integer categoriaId, // <-- CORRIGIDO de 'categoria_id' para 'categoriaId'

    @NotNull(message = "O fornecedor é obrigatório.")
    Integer fornecedorId // <-- CORRIGIDO de 'fornecedor_id' para 'fornecedorId'
) {}