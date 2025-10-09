package br.com.aula.gestaodeestoques.model;

import org.springframework.data.annotation.Id;
import java.math.BigDecimal;

/**
 * Representa um produto no estoque.
 * Usamos um 'record' do Java para criar uma classe de dados imutável de forma concisa.
 * A anotação @Id no campo 'id' informa ao Spring Data que este é o identificador único (chave primária).
 */
public record Produto(
    @Id Integer id,
    String nome,
    int quantidade,
    BigDecimal preco,
    Integer categoriaId, // Chave estrangeira para Categoria
    Integer fornecedorId // Chave estrangeira para Fornecedor
) {
    /**
     * Construtor compacto para validação.
     * Garante que os valores recebidos são válidos antes de criar o objeto.
     */
    public Produto {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do produto não pode ser nulo ou vazio.");
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade não pode ser negativa.");
        }
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O preço não pode ser nulo ou negativo.");
        }
    }
}
