package br.com.aula.gestaodeestoques.dto;
import java.math.BigDecimal;
// DTO para exibir dados de produto, enriquecido com nomes.
public record ProdutoDTO(
    Integer id,
    String nome,
    int quantidade,
    BigDecimal preco,
    String nomeCategoria,
    String nomeFornecedor
) {}